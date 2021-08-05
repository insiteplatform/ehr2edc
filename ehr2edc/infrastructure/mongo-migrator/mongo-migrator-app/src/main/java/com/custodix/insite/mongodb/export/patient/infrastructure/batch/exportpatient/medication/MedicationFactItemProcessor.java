package com.custodix.insite.mongodb.export.patient.infrastructure.batch.exportpatient.medication;

import static com.custodix.insite.mongodb.export.patient.infrastructure.batch.exportpatient.BatchExportPatientRunner.SUBJECT_ID_PARAM;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;

import com.custodix.insite.mongodb.export.patient.domain.model.common.*;
import com.custodix.insite.mongodb.export.patient.domain.model.medication.EventType;
import com.custodix.insite.mongodb.export.patient.domain.model.medication.MedicationFact;
import com.custodix.insite.mongodb.export.patient.domain.model.medication.MedicationItem;
import com.custodix.insite.mongodb.export.patient.domain.model.medication.MedicationRecord;
import com.custodix.insite.mongodb.export.patient.domain.repository.ConceptPathRepository;
import com.custodix.insite.mongodb.export.patient.domain.repository.ModifierRepository;
import com.custodix.insite.mongodb.vocabulary.Namespace;
import com.custodix.insite.mongodb.vocabulary.PatientId;
import com.custodix.insite.mongodb.vocabulary.PatientIdentifier;
import com.custodix.insite.mongodb.vocabulary.SubjectId;

@StepScope
class MedicationFactItemProcessor implements ItemProcessor<MedicationItem, MedicationFact> {
	private final ConceptPathRepository conceptPathRepository;
	private final MedicationExportSettings medicationExportSettings;
	private final ModifierRepository modifierRepository;

	private SubjectId subjectId;

	MedicationFactItemProcessor(ConceptPathRepository conceptPathRepository,
			MedicationExportSettings medicationExportSettings, ModifierRepository modifierRepository) {
		this.conceptPathRepository = conceptPathRepository;
		this.medicationExportSettings = medicationExportSettings;
		this.modifierRepository = modifierRepository;
	}

	@BeforeStep
	public void beforeStep(final StepExecution stepExecution) {
		this.subjectId = SubjectId.of(stepExecution.getJobParameters()
				.getString(SUBJECT_ID_PARAM));
	}

	@Override
	public MedicationFact process(MedicationItem item) {
		MedicationRecord record = item.getMedicationRecord();
		List<ConceptPath> concepts = getConceptPaths(record.getConceptCode());
		Concept referenceConcept = getReferenceConcept(concepts);
		Modifiers modifiers = getModifiers(item);
		MedicationFact.Builder builder = MedicationFact.newBuilder()
				.withPatientIdentifier(buildPatientIdentifier(record))
				.withStartDate(record.getStartDate()
						.toInstant())
				.withEndDate(getEndDate(record))
				.withConceptPaths(concepts)
				.withReferenceConcept(referenceConcept)
				.withLocalConcept(getLocalConcept(concepts))
				.withConceptName(record.getConceptName())
				.withDosage(item.getDosage())
				.withEventType(getEventType(modifiers));

		WithModifiers.modifiers(modifiers).into(builder);

		return builder.build();
	}

	private EventType getEventType(Modifiers modifiers) {
		return modifiers.getModifier(ModifierCategory.EVENT_TYPE)
				.map(modifier -> EventType.UNKNOWN) // TODO: E2E-351
				.orElse(null);
	}

	private PatientIdentifier buildPatientIdentifier(MedicationRecord record) {
		return PatientIdentifier.newBuilder()
				.withPatientId(PatientId.of(record.getPatientNum()))
				.withNamespace(Namespace.of(record.getNamespace()))
				.withSubjectId(subjectId)
				.build();
	}

	private Instant getEndDate(MedicationRecord record) {
		if (record.getEndDate() == null) {
			return record.getStartDate()
					.toInstant();
		} else {
			return record.getEndDate()
					.toInstant();
		}
	}

	private List<ConceptPath> getConceptPaths(final String conceptCD) {
		return conceptPathRepository.getConceptPaths(conceptCD);
	}

	private Concept getReferenceConcept(List<ConceptPath> conceptPaths) {
		return conceptPaths.get(0)
				.getConcepts()
				.stream()
				.filter(c -> Objects.equals(c.getSchema(), medicationExportSettings.getConceptNamespace()))
				.reduce((first, second) -> second)
				.orElseThrow(() -> new IllegalStateException("No reference concept present"));
	}

	private Concept getLocalConcept(List<ConceptPath> concepts) {
		return concepts.get(0)
				.getConcepts()
				.stream()
				.reduce((first, second) -> second)
				.orElseThrow(() -> new IllegalStateException("No local concept present"));
	}

	private Modifiers getModifiers(MedicationItem item) {
		return item.getModifierCodes()
				.stream()
				.map(modifierRepository::getModifier)
				.filter(Optional::isPresent)
				.map(Optional::get)
				.collect(Collectors.collectingAndThen(Collectors.toList(), Modifiers::new));
	}

	private static final class WithModifiers {

		private final Modifiers modifiers;

		private WithModifiers(Modifiers modifiers) {
			this.modifiers = modifiers;
		}

		static WithModifiers modifiers(Modifiers modifiers) {
			return new WithModifiers(modifiers);
		}

		void into(MedicationFact.Builder builder) {
			appendModifier(ModifierCategory.ROUTE, builder::withRoute);
			appendModifier(ModifierCategory.FREQUENCY, builder::withFrequency);
			appendModifier(ModifierCategory.DOSE_FORMAT, builder::withDoseFormat);
		}

		private void appendModifier(ModifierCategory category, Consumer<Modifier> modifierConsumer) {
			modifiers.getModifier(category)
					.ifPresent(modifierConsumer);
		}
	}
}