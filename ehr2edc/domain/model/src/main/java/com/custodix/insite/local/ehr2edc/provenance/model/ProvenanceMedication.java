package com.custodix.insite.local.ehr2edc.provenance.model;

import java.time.LocalDateTime;
import java.util.Optional;

import com.custodix.insite.local.ehr2edc.query.executor.medication.domain.EventType;
import com.custodix.insite.local.ehr2edc.query.executor.medication.domain.Medication;

public final class ProvenanceMedication implements ProvenanceDataPoint {
	private final MedicationConcept concept;
	private final LocalDateTime startDate;
	private final LocalDateTime endDate;
	private final Dosage dosage;
	private final String administrationRoute;
	private final String doseForm;
	private final String dosingFrequency;
	private final EventType eventType;

	private ProvenanceMedication(Builder builder) {
		concept = builder.concept;
		startDate = builder.startDate;
		endDate = builder.endDate;
		dosage = builder.dosage;
		administrationRoute = builder.administrationRoute;
		doseForm = builder.doseForm;
		dosingFrequency = builder.dosingFrequency;
		eventType = builder.eventType;
	}

	public static ProvenanceMedication from(Medication medication) {
		MedicationConcept medicationConcept = Optional.ofNullable(medication.getMedicationConcept())
				.map(MedicationConcept::from)
				.orElse(null);
		Dosage dosage = Optional.ofNullable(medication.getDosage())
				.map(Dosage::from)
				.orElse(null);
		return ProvenanceMedication.newBuilder()
				.withConcept(medicationConcept)
				.withStartDate(medication.getStartDate())
				.withEndDate(medication.getEndDate())
				.withDosage(dosage)
				.withAdministrationRoute(medication.getAdministrationRoute())
				.withDoseForm(medication.getDoseForm())
				.withDosingFrequency(medication.getDosingFrequency())
				.withEventType(medication.getEventType())
				.build();
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public MedicationConcept getConcept() {
		return concept;
	}

	public LocalDateTime getStartDate() {
		return startDate;
	}

	public LocalDateTime getEndDate() {
		return endDate;
	}

	public Dosage getDosage() {
		return dosage;
	}

	public String getAdministrationRoute() {
		return administrationRoute;
	}

	public String getDoseForm() {
		return doseForm;
	}

	public String getDosingFrequency() {
		return dosingFrequency;
	}

	public EventType getEventType() {
		return eventType;
	}

	public static final class Builder {
		private MedicationConcept concept;
		private LocalDateTime startDate;
		private LocalDateTime endDate;
		private Dosage dosage;
		private String administrationRoute;
		private String doseForm;
		private String dosingFrequency;
		private EventType eventType;

		private Builder() {
		}

		public Builder withConcept(MedicationConcept concept) {
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

		public Builder withDosage(Dosage dosage) {
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

		public ProvenanceMedication build() {
			return new ProvenanceMedication(this);
		}
	}
}
