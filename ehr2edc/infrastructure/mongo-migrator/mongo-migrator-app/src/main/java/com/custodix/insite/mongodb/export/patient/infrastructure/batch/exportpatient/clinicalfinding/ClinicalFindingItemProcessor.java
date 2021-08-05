package com.custodix.insite.mongodb.export.patient.infrastructure.batch.exportpatient.clinicalfinding;

import static com.custodix.insite.mongodb.export.patient.infrastructure.batch.exportpatient.BatchExportPatientRunner.SUBJECT_ID_PARAM;
import static java.util.Collections.singletonList;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;

import com.custodix.insite.mongodb.export.patient.domain.model.ClinicalFactRecord;
import com.custodix.insite.mongodb.export.patient.domain.model.ClinicalFindingFact;
import com.custodix.insite.mongodb.export.patient.domain.model.ClinicalFindingItem;
import com.custodix.insite.mongodb.export.patient.domain.model.common.*;
import com.custodix.insite.mongodb.export.patient.domain.repository.ConceptPathRepository;
import com.custodix.insite.mongodb.export.patient.domain.repository.ModifierRepository;
import com.custodix.insite.mongodb.vocabulary.Namespace;
import com.custodix.insite.mongodb.vocabulary.PatientId;
import com.custodix.insite.mongodb.vocabulary.PatientIdentifier;
import com.custodix.insite.mongodb.vocabulary.SubjectId;

@StepScope
class ClinicalFindingItemProcessor implements ItemProcessor<ClinicalFindingItem, ClinicalFindingFact> {
	private static final Logger LOGGER = LoggerFactory.getLogger(ClinicalFindingItemProcessor.class);

	private final ConceptPathRepository conceptPathRepository;
	private final ModifierRepository modifierRepository;
	private final ClinicalFindingExportSettings exportSettings;

	private String subjectId;

	ClinicalFindingItemProcessor(ConceptPathRepository conceptPathRepository, ModifierRepository modifierRepository,
			ClinicalFindingExportSettings exportSettings) {
		this.conceptPathRepository = conceptPathRepository;
		this.modifierRepository = modifierRepository;
		this.exportSettings = exportSettings;
	}

	@BeforeStep
	public void beforeStep(final StepExecution stepExecution) {
		this.subjectId = stepExecution.getJobParameters()
				.getString(SUBJECT_ID_PARAM);
	}

	@Override
	public ClinicalFindingFact process(ClinicalFindingItem item) {
		ClinicalFactRecord record = item.getClinicalFactRecord();
		List<ConceptPath> concepts = getConceptPaths(record.getConceptCode());
		Concept referenceConcept = getReferenceConcept(concepts);
		Modifiers modifiers = getModifiers(item);
		ClinicalFindingFact.Builder builder = ClinicalFindingFact.newBuilder()
				.withEffectiveDate(record.getStartDate()
						.toInstant())
				.withPatientIdentifier(buildPatientIdentifier(record))
				.withValueObservation(buildObservation(record, record.getValue()))
				.withUlnObservation(buildObservation(record, record.getUlnValue()))
				.withLlnObservation(buildObservation(record, record.getLlnValue()))
				.withConceptPaths(concepts)
				.withReferenceConcept(referenceConcept)
				.withLocalConcept(getLocalConcept(concepts))
				.withLabel(record.getLabel());

		modifiers.getModifier(ModifierCategory.LATERALITY)
				.ifPresent(builder::withLaterality);
		modifiers.getModifier(ModifierCategory.POSITION)
				.ifPresent(builder::withPosition);
		modifiers.getModifier(ModifierCategory.LOCATION)
				.ifPresent(builder::withLocation);
		return builder.build();
	}

	private PatientIdentifier buildPatientIdentifier(ClinicalFactRecord record) {
		return PatientIdentifier.newBuilder()
				.withPatientId(PatientId.of(record.getPatientId()))
				.withNamespace(Namespace.of(record.getNamespace()))
				.withSubjectId(SubjectId.of(subjectId))
				.build();
	}

	private Observation buildObservation(ClinicalFactRecord record, BigDecimal value) {
		return Observation.newBuilder()
				.withType(ObservationType.NUMERIC)
				.withValues(singletonList(buildObservationValue(record, value)))
				.build();
	}

	private ObservationValue<BigDecimal> buildObservationValue(ClinicalFactRecord record, BigDecimal value) {
		return ObservationValue.<BigDecimal>newBuilder().withUnit(record.getUnit())
				.withValue(value)
				.build();
	}

	private List<ConceptPath> getConceptPaths(final String conceptCD) {
		return conceptPathRepository.getConceptPaths(conceptCD);
	}

	private Concept getReferenceConcept(List<ConceptPath> conceptPaths) {
		return conceptPaths.get(0)
				.getConcepts()
				.stream()
				.filter(c -> exportSettings.getConceptNamespaces()
						.contains(c.getSchema()))
				.reduce((first, second) -> second)
				.orElseThrow(() -> handleMissingReferenceCode(conceptPaths));
	}

	private Concept getLocalConcept(List<ConceptPath> concepts) {
		return concepts.get(0)
				.getConcepts()
				.stream()
				.reduce((first, second) -> second)
				.orElseThrow(() -> handleMissingReferenceCode(concepts));
	}

	private Modifiers getModifiers(ClinicalFindingItem item) {
		return item.getModifierCodes()
				.stream()
				.map(modifierRepository::getModifier)
				.filter(Optional::isPresent)
				.map(Optional::get)
				.collect(Collectors.collectingAndThen(Collectors.toList(), Modifiers::new));
	}

	private IllegalStateException handleMissingReferenceCode(List<ConceptPath> path) {
		LOGGER.error("No reference code present concept path was: {}", path.stream()
				.map(ConceptPath::getPath)
				.collect(Collectors.joining(";")));
		return new IllegalStateException("No reference code present");
	}

}