package com.custodix.insite.mongodb.export.patient.infrastructure.batch.exportpatient.clinicalfinding;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;

import com.custodix.insite.mongodb.export.patient.domain.model.ClinicalFindingFact;
import com.custodix.insite.mongodb.export.patient.domain.model.common.Observation;
import com.custodix.insite.mongodb.export.patient.infrastructure.mongo.model.concept.ConceptCode;
import com.custodix.insite.mongodb.export.patient.infrastructure.mongo.model.measurement.Measurement;
import com.custodix.insite.mongodb.export.patient.infrastructure.mongo.model.vitalsign.VitalSignConcept;
import com.custodix.insite.mongodb.export.patient.infrastructure.mongo.model.vitalsign.VitalSignDocument;
import com.custodix.insite.mongodb.export.patient.infrastructure.mongo.repository.VitalSignDocumentRepository;

@StepScope
public final class ClinicalFindingFactMongoWriter implements ItemWriter<ClinicalFindingFact> {

	private final VitalSignDocumentRepository vitalSignsRepository;

	ClinicalFindingFactMongoWriter(final VitalSignDocumentRepository vitalSignsRepository) {
		this.vitalSignsRepository = vitalSignsRepository;
	}

	@Override
	public void write(List<? extends ClinicalFindingFact> list) {
		list.stream()
				.filter(Objects::nonNull)
				.map(new ToVitalSignDocument())
				.forEach(vitalSignsRepository::save);
	}

	class ToVitalSignDocument implements Function<ClinicalFindingFact, VitalSignDocument> {

		@Override
		public VitalSignDocument apply(final ClinicalFindingFact clinicalFindingFact) {

			return VitalSignDocument.newBuilder()
					.withSubjectId(clinicalFindingFact.getPatientIdentifier()
							.getSubjectId())
					.withConcept(getVitalSignConcept(clinicalFindingFact))
					.withEffectiveDateTime(
							clinicalFindingFact.getEffectiveDate().atZone(ZoneId.systemDefault()).toLocalDateTime())
					.withMeasurement(getMeasurement(clinicalFindingFact))
					.build();
		}

		private Measurement getMeasurement(final ClinicalFindingFact clinicalFindingFact) {
			Measurement.Builder measurementBuilder = Measurement.newBuilder();

			return measurementBuilder.withLowerLimit(getObservationValue(clinicalFindingFact.getLlnObservation()))
					.withUpperLimit(getObservationValue(clinicalFindingFact.getUlnObservation()))
					.withValue(getObservationValue(clinicalFindingFact.getValueObservation()))
					.withUnit(getObservationValueUnit(clinicalFindingFact.getValueObservation()))
					.build();
		}

		private String getObservationValueUnit(final Observation observation) {
			return observation != null && !observation.getValues()
					.isEmpty() ?
					observation.getValues()
							.get(0)
							.getUnit() :
					null;
		}

		private BigDecimal getObservationValue(final Observation observation) {
			return observation != null && !observation.getValues()
					.isEmpty() ?
					(BigDecimal) observation.getValues()
							.get(0)
							.getValue() :
					null;
		}

		private VitalSignConcept getVitalSignConcept(final ClinicalFindingFact clinicalFindingFact) {
			ConceptCode concept = ConceptCode.conceptFor(clinicalFindingFact.getReferenceConcept()
					.getCode());
			VitalSignConcept.Builder vitalSignConceptBuilder = VitalSignConcept.newBuilder()
					.withConcept(concept);
			clinicalFindingFact.getLaterality()
					.ifPresent(laterality -> {
						vitalSignConceptBuilder.withLaterality(laterality.getReferenceCode());
					});
			clinicalFindingFact.getPosition()
					.ifPresent(position -> {
						vitalSignConceptBuilder.withPosition(position.getReferenceCode());
					});
			clinicalFindingFact.getLocation()
					.ifPresent(location -> {
						vitalSignConceptBuilder.withLocation(location.getReferenceCode());
					});
			return vitalSignConceptBuilder.build();
		}
	}
}
