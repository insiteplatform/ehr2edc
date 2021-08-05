package com.custodix.insite.mongodb.export.patient.domain.model.medication;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import com.custodix.insite.mongodb.export.patient.domain.model.SummarizableObservationFact;
import com.custodix.insite.mongodb.export.patient.domain.model.common.Concept;
import com.custodix.insite.mongodb.export.patient.domain.model.common.ConceptPath;
import com.custodix.insite.mongodb.export.patient.domain.model.common.Modifier;
import com.custodix.insite.mongodb.vocabulary.PatientIdentifier;

public final class MedicationFact implements SummarizableObservationFact {
	public static final String MEDICATION_CATEGORY = "medication";
	private final PatientIdentifier patientIdentifier;
	private final Instant startDate;
	private final Instant endDate;

	private final Concept localConcept;
	private final Concept referenceConcept;
	private final String conceptName;
	private final Modifier route;
	private final Modifier doseFormat;
	private final Modifier frequency;
	private final EventType eventType;

	private final Dosage dosage;

	private final List<ConceptPath> conceptPaths;

	private MedicationFact(Builder builder) {
		patientIdentifier = builder.patientIdentifier;
		startDate = builder.startDate;
		endDate = builder.endDate;
		localConcept = builder.localConcept;
		referenceConcept = builder.referenceConcept;
		conceptName = builder.conceptName;
		route = builder.route;
		doseFormat = builder.doseFormat;
		frequency = builder.frequency;
		eventType = builder.eventType;
		dosage = builder.dosage;
		conceptPaths = builder.conceptPaths;
	}

	public PatientIdentifier getPatientIdentifier() {
		return patientIdentifier;
	}

	public Optional<Dosage> getDosage() {
		return Optional.ofNullable(dosage);
	}

	public String getConceptName() {
		return conceptName;
	}

	public Instant getStartDate() {
		return startDate;
	}

	public Instant getEndDate() {
		return endDate;
	}

	public Concept getLocalConcept() {
		return localConcept;
	}

	public Concept getReferenceConcept() {
		return referenceConcept;
	}

	public Optional<Modifier> getRoute() {
		return Optional.ofNullable(route);
	}

	public Optional<Modifier> getDoseFormat() {
		return Optional.ofNullable(doseFormat);
	}

	public Optional<Modifier> getFrequency() {
		return Optional.ofNullable(frequency);
	}

	public Optional<EventType> getEventType() {
		return Optional.ofNullable(eventType);
	}

	public List<ConceptPath> getConceptPaths() {
		return conceptPaths;
	}

	@Override
	public Instant getObservationInstant() {
		return startDate;
	}

	@Override
	public String getCategory() {
		return MEDICATION_CATEGORY;
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
		private Instant startDate;
		private Instant endDate;
		private Concept localConcept;
		private Concept referenceConcept;
		private String conceptName;
		private Modifier route;
		private Modifier doseFormat;
		private Modifier frequency;
		private EventType eventType;
		private Dosage dosage;
		private List<ConceptPath> conceptPaths;

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

		public Builder withLocalConcept(Concept val) {
			localConcept = val;
			return this;
		}

		public Builder withReferenceConcept(Concept val) {
			referenceConcept = val;
			return this;
		}

		public Builder withConceptName(String conceptName) {
			this.conceptName = conceptName;
			return this;
		}

		public Builder withRoute(Modifier route) {
			this.route = route;
			return this;
		}

		public Builder withDoseFormat(Modifier doseFormat) {
			this.doseFormat = doseFormat;
			return this;
		}

		public Builder withFrequency(Modifier frequency) {
			this.frequency = frequency;
			return this;
		}

		public Builder withEventType(EventType eventType) {
			this.eventType = eventType;
			return this;
		}

		public Builder withDosage(Dosage dosage) {
			this.dosage = dosage;
			return this;
		}

		public Builder withConceptPaths(List<ConceptPath> val) {
			conceptPaths = val;
			return this;
		}

		public MedicationFact build() {
			return new MedicationFact(this);
		}
	}
}