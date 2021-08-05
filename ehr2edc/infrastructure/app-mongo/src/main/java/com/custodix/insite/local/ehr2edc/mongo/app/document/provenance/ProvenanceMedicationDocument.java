package com.custodix.insite.local.ehr2edc.mongo.app.document.provenance;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.annotation.TypeAlias;

import com.custodix.insite.local.ehr2edc.provenance.model.Dosage;
import com.custodix.insite.local.ehr2edc.provenance.model.MedicationConcept;
import com.custodix.insite.local.ehr2edc.provenance.model.ProvenanceDataPoint;
import com.custodix.insite.local.ehr2edc.provenance.model.ProvenanceMedication;
import com.custodix.insite.local.ehr2edc.query.executor.medication.domain.EventType;

@TypeAlias("ProvenanceMedication")
public final class ProvenanceMedicationDocument implements ProvenanceDataPointDocument {
	private final MedicationConceptDocument concept;
	private final LocalDateTime startDate;
	private final LocalDateTime endDate;
	private final DosageDocument dosage;
	private final String administrationRoute;
	private final String doseForm;
	private final String dosingFrequency;
	private final EventType eventType;

	@PersistenceConstructor
	private ProvenanceMedicationDocument(MedicationConceptDocument concept, LocalDateTime startDate,
			LocalDateTime endDate, DosageDocument dosage, String administrationRoute, String doseForm,
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

	private ProvenanceMedicationDocument(Builder builder) {
		concept = builder.concept;
		startDate = builder.startDate;
		endDate = builder.endDate;
		dosage = builder.dosage;
		administrationRoute = builder.administrationRoute;
		doseForm = builder.doseForm;
		dosingFrequency = builder.dosingFrequency;
		eventType = builder.eventType;
	}

	public static ProvenanceMedicationDocument toDocument(ProvenanceMedication provenanceMedication) {
		MedicationConceptDocument concept = Optional.ofNullable(provenanceMedication.getConcept())
				.map(MedicationConceptDocument::toDocument)
				.orElse(null);
		DosageDocument dosage = Optional.ofNullable(provenanceMedication.getDosage())
				.map(DosageDocument::toDocument)
				.orElse(null);
		return ProvenanceMedicationDocument.newBuilder()
				.withConcept(concept)
				.withStartDate(provenanceMedication.getStartDate())
				.withEndDate(provenanceMedication.getEndDate())
				.withDosage(dosage)
				.withAdministrationRoute(provenanceMedication.getAdministrationRoute())
				.withDoseForm(provenanceMedication.getDoseForm())
				.withDosingFrequency(provenanceMedication.getDosingFrequency())
				.withEventType(provenanceMedication.getEventType())
				.build();
	}

	@Override
	public ProvenanceDataPoint restore() {
		MedicationConcept restoredConcept = Optional.ofNullable(concept)
				.map(MedicationConceptDocument::restore)
				.orElse(null);
		Dosage restoredDosage = Optional.ofNullable(dosage)
				.map(DosageDocument::restore)
				.orElse(null);
		return ProvenanceMedication.newBuilder()
				.withConcept(restoredConcept)
				.withStartDate(startDate)
				.withEndDate(endDate)
				.withDosage(restoredDosage)
				.withAdministrationRoute(administrationRoute)
				.withDoseForm(doseForm)
				.withDosingFrequency(dosingFrequency)
				.withEventType(eventType)
				.build();
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static final class Builder {
		private MedicationConceptDocument concept;
		private LocalDateTime startDate;
		private LocalDateTime endDate;
		private DosageDocument dosage;
		private String administrationRoute;
		private String doseForm;
		private String dosingFrequency;
		private EventType eventType;

		private Builder() {
		}

		public Builder withConcept(MedicationConceptDocument concept) {
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

		public Builder withDosage(DosageDocument dosage) {
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

		public ProvenanceMedicationDocument build() {
			return new ProvenanceMedicationDocument(this);
		}
	}
}
