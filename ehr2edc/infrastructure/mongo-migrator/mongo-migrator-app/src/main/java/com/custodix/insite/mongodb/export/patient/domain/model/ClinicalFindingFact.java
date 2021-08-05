package com.custodix.insite.mongodb.export.patient.domain.model;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import com.custodix.insite.mongodb.export.patient.domain.model.common.Concept;
import com.custodix.insite.mongodb.export.patient.domain.model.common.ConceptPath;
import com.custodix.insite.mongodb.export.patient.domain.model.common.Modifier;
import com.custodix.insite.mongodb.export.patient.domain.model.common.Observation;
import com.custodix.insite.mongodb.vocabulary.PatientIdentifier;

public class ClinicalFindingFact implements SummarizableObservationFact {
	public static final String CLINICAL_FINDING_CATEGORY = "clinical finding";
	private final PatientIdentifier patientIdentifier;
	private final Instant effectiveDate;
	private final String label;

	private final Observation valueObservation;
	private final Observation ulnObservation;
	private final Observation llnObservation;

	private final Concept localConcept;
	private final Concept referenceConcept;
	private final List<ConceptPath> conceptPaths;

	private final Modifier laterality;
	private final Modifier position;
	private final Modifier location;

	private ClinicalFindingFact(final Builder builder) {
		patientIdentifier = builder.patientIdentifier;
		effectiveDate = builder.effectiveDate;
		label = builder.label;
		valueObservation = builder.valueObservation;
		ulnObservation = builder.ulnObservation;
		llnObservation = builder.llnObservation;
		localConcept = builder.localConcept;
		referenceConcept = builder.referenceConcept;
		conceptPaths = builder.conceptPaths;
		laterality = builder.laterality;
		position = builder.position;
		location = builder.location;
	}

	public Builder toBuilder() {
		Builder builder = new Builder();
		builder.patientIdentifier = getPatientIdentifier();
		builder.effectiveDate = getEffectiveDate();
		builder.label = getLabel();
		builder.valueObservation = getValueObservation();
		builder.ulnObservation = getUlnObservation();
		builder.llnObservation = getLlnObservation();
		builder.localConcept = getLocalConcept();
		builder.referenceConcept = getReferenceConcept();
		builder.conceptPaths = getConceptPaths();
		builder.laterality = getLaterality().orElse(null);
		builder.position = getPosition().orElse(null);
		builder.location = getLocation().orElse(null);
		return builder;
	}

	public PatientIdentifier getPatientIdentifier() {
		return patientIdentifier;
	}

	public Instant getEffectiveDate() {
		return effectiveDate;
	}

	public String getLabel() {
		return label;
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

	public Concept getLocalConcept() {
		return localConcept;
	}

	public Concept getReferenceConcept() {
		return referenceConcept;
	}

	public List<ConceptPath> getConceptPaths() {
		return conceptPaths;
	}

	public Optional<Modifier> getLaterality() {
		return Optional.ofNullable(laterality);
	}

	public Optional<Modifier> getLocation() {
		return Optional.ofNullable(location);
	}

	public Optional<Modifier> getPosition() {
		return Optional.ofNullable(position);
	}

	@Override
	public Instant getObservationInstant() {
		return effectiveDate;
	}

	@Override
	public String getCategory() {
		return CLINICAL_FINDING_CATEGORY;
	}

	@Override
	public String getSubjectIdentifier() {
		return patientIdentifier.getSubjectId()
				.getId();
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static final class Builder {
		private PatientIdentifier patientIdentifier;
		private Instant effectiveDate;
		private String label;
		private Observation valueObservation;
		private Observation ulnObservation;
		private Observation llnObservation;
		private Concept localConcept;
		private Concept referenceConcept;
		private List<ConceptPath> conceptPaths;
		private Modifier laterality;
		private Modifier location;
		private Modifier position;

		private Builder() {
		}

		public Builder withPatientIdentifier(PatientIdentifier val) {
			patientIdentifier = val;
			return this;
		}

		public Builder withEffectiveDate(Instant val) {
			effectiveDate = val;
			return this;
		}

		public Builder withLabel(String val) {
			label = val;
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

		public Builder withLocalConcept(Concept val) {
			localConcept = val;
			return this;
		}

		public Builder withReferenceConcept(Concept val) {
			referenceConcept = val;
			return this;
		}

		public Builder withConceptPaths(List<ConceptPath> val) {
			conceptPaths = val;
			return this;
		}

		public Builder withLaterality(final Modifier val) {
			laterality = val;
			return this;
		}

		public Builder withLocation(final Modifier val) {
			location = val;
			return this;
		}

		public Builder withPosition(final Modifier val) {
			position = val;
			return this;
		}

		public ClinicalFindingFact build() {
			return new ClinicalFindingFact(this);
		}
	}
}