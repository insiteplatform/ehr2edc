package com.custodix.insite.mongodb.export.patient.domain.model;

import static java.util.Collections.unmodifiableList;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.custodix.insite.mongodb.export.patient.domain.model.common.Concept;
import com.custodix.insite.mongodb.export.patient.domain.model.common.ConceptPath;
import com.custodix.insite.mongodb.export.patient.domain.model.common.Observation;
import com.custodix.insite.mongodb.export.patient.domain.model.common.ObservationValue;
import com.custodix.insite.mongodb.export.patient.domain.model.labvalue.LaboratoryConceptInfo;
import com.custodix.insite.mongodb.export.patient.infrastructure.mongo.model.concept.ConceptCode;
import com.custodix.insite.mongodb.export.patient.infrastructure.mongo.model.labvalue.LabConcept;
import com.custodix.insite.mongodb.export.patient.infrastructure.mongo.model.labvalue.LabMeasurement;
import com.custodix.insite.mongodb.export.patient.infrastructure.mongo.model.labvalue.LabValueDocument;
import com.custodix.insite.mongodb.vocabulary.PatientIdentifier;

public class LaboratoryFact implements SummarizableObservationFact {

	private static final String TYPE = "laboratory";
	public static final String LABORATORY_CATEGORY = "laboratory";

	private final PatientIdentifier patientIdentifier;
	private final Instant startDate;
	private final Instant endDate;
	private final Concept referenceConcept;
	private final Concept localConcept;
	private final String label;
	private final List<ConceptPath> conceptPaths;
	private final LaboratoryConceptInfo conceptInfo;
	private final String vendor;

	private final Observation valueObservation;
	private final Observation ulnObservation;
	private final Observation llnObservation;

	private LaboratoryFact(Builder builder) {
		patientIdentifier = builder.patientIdentifier;
		startDate = builder.startDate;
		endDate = builder.endDate;
		referenceConcept = builder.referenceConcept;
		localConcept = builder.localConcept;
		label = builder.label;
		conceptPaths = unmodifiableList(builder.conceptPaths);
		conceptInfo = builder.conceptInfo;
		valueObservation = builder.valueObservation;
		ulnObservation = builder.ulnObservation;
		llnObservation = builder.llnObservation;
		vendor = builder.vendor;
	}

	public PatientIdentifier getPatientIdentifier() {
		return patientIdentifier;
	}

	public String getType() {
		return TYPE;
	}

	public Instant getStartDate() {
		return startDate;
	}

	public Instant getEndDate() {
		return endDate;
	}

	public Concept getReferenceConcept() {
		return referenceConcept;
	}

	public Concept getLocalConcept() {
		return localConcept;
	}

	public String getLabel() {
		return label;
	}

	public List<ConceptPath> getConceptPaths() {
		return conceptPaths;
	}

	public LaboratoryConceptInfo getConceptInfo() {
		return conceptInfo;
	}

	public Observation getValueObservation() {
		return valueObservation;
	}

	public Observation getUlnObservation() {
		return ulnObservation;
	}

	public Observation getLlnObservation() {
		return llnObservation;
	}

	@Override
	public Instant getObservationInstant() {
		return startDate;
	}

	@Override
	public String getCategory() {
		return LABORATORY_CATEGORY;
	}

	@Override
	public String getSubjectIdentifier() {
		return patientIdentifier.getSubjectId()
				.getId();
	}

	public LabValueDocument toDocument() {
		return LabValueDocument.newBuilder()
				.forSubject(patientIdentifier.getSubjectId())
				.withStartDate(LocalDateTime.ofInstant(startDate, ZoneOffset.systemDefault()))
				.withEndDate(LocalDateTime.ofInstant(endDate, ZoneOffset.systemDefault()))
				.withLabConcept(labConcept())
				.withQuantitativeResult(labMeasurement())
				.withVendor(vendor)
				.build();
	}

	private LabConcept labConcept() {
		return LabConcept.newBuilder()
				.withConcept(ConceptCode.conceptFor(extractCode()))
				.withComponent(extractComponent())
				.withMethod(extractMethodValue())
				.withFastingStatus(extractFastingStatus())
				.withSpecimen(extractSpecimenValue())
				.build();
	}

	private String extractCode() {
		return Optional.ofNullable(referenceConcept.getCode())
				.orElse("");
	}

	private String extractComponent() {
		return Optional.ofNullable(conceptInfo)
				.map(LaboratoryConceptInfo::getComponent)
				.orElse("");
	}

	private String extractMethodValue() {
		return Optional.ofNullable(conceptInfo)
				.flatMap(LaboratoryConceptInfo::getMethodValue)
				.orElse("");
	}

	private String extractFastingStatus() {
		return Optional.ofNullable(conceptInfo)
				.map(LaboratoryConceptInfo::getFastingStatus)
				.map(Enum::name)
				.orElse("");
	}

	private String extractSpecimenValue() {
		return Optional.ofNullable(conceptInfo)
				.map(LaboratoryConceptInfo::getSpecimenValue)
				.orElse("");
	}

	private LabMeasurement labMeasurement() {
		return LabMeasurement.newBuilder()
				.withLowerLimit(getValue(llnObservation.getValues()
						.get(0)))
				.withUpperLimit(getValue(ulnObservation.getValues()
						.get(0)))
				.withValue(getValue(valueObservation.getValues()
						.get(0)))
				.withUnit(getUnit(valueObservation.getValues()
						.get(0)))
				.build();
	}

	private BigDecimal getValue(ObservationValue<?> observationValue) {
		return Optional.ofNullable(observationValue)
				.map(v -> toBigDecimal(v.getValue()))
				.orElse(null);
	}

	private BigDecimal toBigDecimal(Object value) {
		if (value == null) {
			return null;
		}

		BigDecimal result = null;
		if (value instanceof Number) {
			result = value instanceof BigDecimal ?
					(BigDecimal) value :
					BigDecimal.valueOf(((Number) value).doubleValue());
		} else if (value instanceof String) {
			result = new BigDecimal((String) value);
		}
		return result;
	}

	private String getUnit(ObservationValue<?> observationValue) {
		return Optional.ofNullable(observationValue)
				.map(ObservationValue::getUnit)
				.orElse(null);
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static final class Builder {
		private PatientIdentifier patientIdentifier;
		private Instant startDate;
		private Instant endDate;
		private Concept referenceConcept;
		private Concept localConcept;
		private String label;
		private List<ConceptPath> conceptPaths = Collections.emptyList();
		private LaboratoryConceptInfo conceptInfo;
		private Observation valueObservation;
		private Observation ulnObservation;
		private Observation llnObservation;
		private String vendor;

		private Builder() {
		}

		public Builder withPatientIdentifier(PatientIdentifier val) {
			patientIdentifier = val;
			return this;
		}

		public Builder withStartDate(Instant val) {
			startDate = val;
			return this;
		}

		public Builder withEndDate(Instant val) {
			endDate = val;
			return this;
		}

		public Builder withReferenceConcept(Concept val) {
			referenceConcept = val;
			return this;
		}

		public Builder withLocalConcept(Concept val) {
			localConcept = val;
			return this;
		}

		public Builder withLabel(String val) {
			label = val;
			return this;
		}

		public Builder withConceptPaths(List<ConceptPath> val) {
			conceptPaths = val;
			return this;
		}

		public Builder withConceptInfo(LaboratoryConceptInfo val) {
			conceptInfo = val;
			return this;
		}

		public Builder withValueObservation(Observation val) {
			valueObservation = val;
			return this;
		}

		public Builder withUlnObservation(Observation val) {
			ulnObservation = val;
			return this;
		}

		public Builder withLlnObservation(Observation val) {
			llnObservation = val;
			return this;
		}

		public Builder withVendor(String val) {
			vendor = val;
			return this;
		}

		public LaboratoryFact build() {
			return new LaboratoryFact(this);
		}
	}
}