package com.custodix.insite.mongodb.export.patient.infrastructure.batch.exportpatient.laboratory;

import static com.custodix.insite.mongodb.export.patient.domain.model.common.ObservationType.NUMERIC;
import static com.custodix.insite.mongodb.export.patient.infrastructure.batch.exportpatient.BatchExportPatientRunner.SUBJECT_ID_PARAM;
import static java.util.Collections.singletonList;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;

import com.custodix.insite.mongodb.export.patient.domain.model.LaboratoryFact;
import com.custodix.insite.mongodb.export.patient.domain.model.LaboratoryObservationFactRecord;
import com.custodix.insite.mongodb.export.patient.domain.model.common.Concept;
import com.custodix.insite.mongodb.export.patient.domain.model.common.ConceptPath;
import com.custodix.insite.mongodb.export.patient.domain.model.common.Observation;
import com.custodix.insite.mongodb.export.patient.domain.model.common.ObservationValue;
import com.custodix.insite.mongodb.export.patient.domain.repository.ConceptPathRepository;
import com.custodix.insite.mongodb.export.patient.domain.repository.LaboratoryConceptInfoRepository;
import com.custodix.insite.mongodb.vocabulary.Namespace;
import com.custodix.insite.mongodb.vocabulary.PatientId;
import com.custodix.insite.mongodb.vocabulary.PatientIdentifier;
import com.custodix.insite.mongodb.vocabulary.SubjectId;

@StepScope
public class LaboratoryObservationFactProcessor
		implements ItemProcessor<LaboratoryObservationFactRecord, LaboratoryFact> {

	private final ConceptPathRepository conceptPathRepository;
	private final LaboratoryConceptInfoRepository conceptInfoRepository;
	private final LaboratoryExportSettings laboratoryExportSettings;

	private SubjectId subjectId;

	public LaboratoryObservationFactProcessor(ConceptPathRepository conceptPathRepository,
			LaboratoryConceptInfoRepository conceptInfoRepository, LaboratoryExportSettings laboratoryExportSettings) {
		this.conceptPathRepository = conceptPathRepository;
		this.conceptInfoRepository = conceptInfoRepository;
		this.laboratoryExportSettings = laboratoryExportSettings;
	}

	@BeforeStep
	public void beforeStep(final StepExecution stepExecution) {
		this.subjectId = SubjectId.of(stepExecution.getJobParameters()
				.getString(SUBJECT_ID_PARAM));
	}

	@Override
	public LaboratoryFact process(final LaboratoryObservationFactRecord record) {
		List<ConceptPath> concepts = getConceptPaths(record.getConceptCD());
		Concept referenceConcept = getReferenceConcept(concepts);
		LaboratoryFact.Builder builder = LaboratoryFact.newBuilder()
				.withPatientIdentifier(mapPatientIdentifier(record))
				.withEndDate(mapEndDate(record))
				.withStartDate(record.getStartDate()
						.toInstant())
				.withConceptPaths(concepts)
				.withReferenceConcept(referenceConcept)
				.withLocalConcept(getLocalConcept(concepts))
				.withLabel(record.getConceptLabel())
				.withValueObservation(buildObservation(record, record.getValue()))
				.withUlnObservation(buildObservation(record, record.getUlnValue()))
				.withLlnObservation(buildObservation(record, record.getLlnValue()))
				.withVendor(record.getVendor());
		conceptInfoRepository.findByConcept(referenceConcept)
				.ifPresent(builder::withConceptInfo);
		return builder.build();
	}

	private Instant mapEndDate(LaboratoryObservationFactRecord record) {
		if (record.getEndDate() == null) {
			return record.getStartDate()
					.toInstant();
		} else {
			return record.getEndDate()
					.toInstant();
		}
	}

	private PatientIdentifier mapPatientIdentifier(LaboratoryObservationFactRecord record) {
		return PatientIdentifier.newBuilder()
				.withPatientId(PatientId.of(record.getPatientNum()))
				.withNamespace(Namespace.of(record.getPatientMasterIndex()))
				.withSubjectId(subjectId)
				.build();
	}

	private Observation buildObservation(LaboratoryObservationFactRecord item, BigDecimal value) {
		return Observation.newBuilder()
				.withType(NUMERIC)
				.withValues(singletonList(buildObservationValue(item, value)))
				.build();
	}

	private ObservationValue<BigDecimal> buildObservationValue(LaboratoryObservationFactRecord item, BigDecimal value) {
		return ObservationValue.<BigDecimal>newBuilder().withUnit(item.getUnit())
				.withValue(value)
				.build();
	}

	private List<ConceptPath> getConceptPaths(final String conceptCD) {
		return conceptPathRepository.getConceptPaths(conceptCD);
	}

	private Concept getReferenceConcept(List<ConceptPath> concepts) {
		return concepts.get(0)
				.getConcepts()
				.stream()
				.filter(c -> Objects.equals(c.getSchema(), laboratoryExportSettings.getConceptNamespace()))
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
}