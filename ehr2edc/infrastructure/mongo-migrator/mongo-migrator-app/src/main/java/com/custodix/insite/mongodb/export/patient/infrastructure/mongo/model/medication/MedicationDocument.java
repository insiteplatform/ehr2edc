package com.custodix.insite.mongodb.export.patient.infrastructure.mongo.model.medication;

import java.time.LocalDateTime;

import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import com.custodix.insite.mongodb.export.patient.domain.model.medication.EventType;
import com.custodix.insite.mongodb.vocabulary.SubjectId;

@Document(collection = MedicationDocument.COLLECTION)
public class MedicationDocument {
	public static final String COLLECTION = "Medication";
	public static final String SUBJECTID_FIELD = "subjectId";

	private SubjectId subjectId;
	private LocalDateTime startDate;
	private LocalDateTime endDate;
	private MedicationConcept concept;
	private String route;
	private String doseFormat;
	private String frequency;
	private EventType eventType;
	private Dosage dosage;

	@PersistenceConstructor
	public MedicationDocument(SubjectId subjectId, LocalDateTime startDate, LocalDateTime endDate,
			MedicationConcept concept, String route, String doseFormat, EventType eventType, Dosage dosage) {
		this.subjectId = subjectId;
		this.startDate = startDate;
		this.endDate = endDate;
		this.concept = concept;
		this.route = route;
		this.doseFormat = doseFormat;
		this.eventType = eventType;
		this.dosage = dosage;
	}

	private MedicationDocument(Builder builder) {
		subjectId = builder.subjectId;
		startDate = builder.startDate;
		endDate = builder.endDate;
		concept = builder.concept;
		route = builder.route;
		doseFormat = builder.doseFormat;
		frequency = builder.frequency;
		eventType = builder.eventType;
		dosage = builder.dosage;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public SubjectId getSubjectId() {
		return subjectId;
	}

	public String getRoute() {
		return route;
	}

	public String getDoseFormat() {
		return doseFormat;
	}

	public String getFrequency() {
		return frequency;
	}

	public EventType getEventType() {
		return eventType;
	}

	public LocalDateTime getStartDate() {
		return startDate;
	}

	public LocalDateTime getEndDate() {
		return endDate;
	}

	public MedicationConcept getConcept() {
		return concept;
	}

	public Dosage getDosage() {
		return dosage;
	}

	public static final class Builder {
		private SubjectId subjectId;
		private LocalDateTime startDate;
		private LocalDateTime endDate;
		private MedicationConcept concept;
		private String route;
		private String doseFormat;
		private String frequency;
		private EventType eventType;
		private Dosage dosage;

		private Builder() {
		}

		public Builder withSubjectId(SubjectId subjectId) {
			this.subjectId = subjectId;
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

		public Builder withConcept(MedicationConcept concept) {
			this.concept = concept;
			return this;
		}

		public Builder withRoute(String route) {
			this.route = route;
			return this;
		}

		public Builder withDoseFormat(String doseFormat) {
			this.doseFormat = doseFormat;
			return this;
		}

		public Builder withFrequency(String frequency) {
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

		public MedicationDocument build() {
			return new MedicationDocument(this);
		}
	}
}
