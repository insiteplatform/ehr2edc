package com.custodix.insite.local.ehr2edc.mongo.app.review.provenance;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.annotation.TypeAlias;

import com.custodix.insite.local.ehr2edc.provenance.model.Dosage;
import com.custodix.insite.local.ehr2edc.provenance.model.MedicationConcept;
import com.custodix.insite.local.ehr2edc.provenance.model.ProvenanceDataPoint;
import com.custodix.insite.local.ehr2edc.provenance.model.ProvenanceMedication;
import com.custodix.insite.local.ehr2edc.query.executor.medication.domain.EventType;

@TypeAlias("ReviewedProvenanceMedication")
final class ReviewedProvenanceMedicationDocument implements ReviewedProvenanceDataPointDocument {
	private final ReviewedMedicationConceptDocument concept;
	private final LocalDateTime startDate;
	private final LocalDateTime endDate;
	private final ReviewedDosageDocument dosage;
	private final String administrationRoute;
	private final String doseForm;
	private final String dosingFrequency;
	private final EventType eventType;

	@PersistenceConstructor
	private ReviewedProvenanceMedicationDocument(ReviewedMedicationConceptDocument concept, LocalDateTime startDate,
                                                 LocalDateTime endDate, ReviewedDosageDocument dosage, String administrationRoute, String doseForm,
                                                 String dosingFrequency, EventType eventType) {
		this.concept = concept;
		this.startDate = startDate;
		this.endDate = endDate;
		this.dosage = dosage;
		this.administrationRoute = administrationRoute;
		this.doseForm = doseForm;
		this.dosingFrequency = dosingFrequency;
		this.eventType = eventType;
	}

	private ReviewedProvenanceMedicationDocument(Builder builder) {
		concept = builder.concept;
		startDate = builder.startDate;
		endDate = builder.endDate;
		dosage = builder.dosage;
		administrationRoute = builder.administrationRoute;
		doseForm = builder.doseForm;
		dosingFrequency = builder.dosingFrequency;
		eventType = builder.eventType;
	}

	@Override
	public ProvenanceDataPoint restore() {
		return ProvenanceMedication.newBuilder()
				.withConcept(getRestoredConcept())
				.withStartDate(startDate)
				.withEndDate(endDate)
				.withDosage(getRestoredDosage())
				.withAdministrationRoute(administrationRoute)
				.withDoseForm(doseForm)
				.withDosingFrequency(dosingFrequency)
				.withEventType(eventType)
				.build();
	}

	private Dosage getRestoredDosage() {
		return Optional.ofNullable(dosage)
				.map(ReviewedDosageDocument::restore)
				.orElse(null);
	}

	private MedicationConcept getRestoredConcept() {
		return Optional.ofNullable(concept)
				.map(ReviewedMedicationConceptDocument::restore)
				.orElse(null);
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static final class Builder {
		private ReviewedMedicationConceptDocument concept;
		private LocalDateTime startDate;
		private LocalDateTime endDate;
		private ReviewedDosageDocument dosage;
		private String administrationRoute;
		private String doseForm;
		private String dosingFrequency;
		private EventType eventType;

		private Builder() {
		}

		public Builder withConcept(ReviewedMedicationConceptDocument concept) {
			this.concept = concept;
			return this;
		}

		public Builder withStartDate(LocalDateTime startDate) {
			this.startDate = startDate;
			return this;
		}

		public Builder withEndDate(LocalDateTime endDate) {
			this.endDate = endDate;
			return this;
		}

		public Builder withDosage(ReviewedDosageDocument dosage) {
			this.dosage = dosage;
			return this;
		}

		public Builder withAdministrationRoute(String administrationRoute) {
			this.administrationRoute = administrationRoute;
			return this;
		}

		public Builder withDoseForm(String doseForm) {
			this.doseForm = doseForm;
			return this;
		}

		public Builder withDosingFrequency(String dosingFrequency) {
			this.dosingFrequency = dosingFrequency;
			return this;
		}

		public Builder withEventType(EventType eventType) {
			this.eventType = eventType;
			return this;
		}

		public ReviewedProvenanceMedicationDocument build() {
			return new ReviewedProvenanceMedicationDocument(this);
		}
	}
}
