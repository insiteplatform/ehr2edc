package com.custodix.insite.local.ehr2edc.query.mongo.medication.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import com.custodix.insite.local.ehr2edc.query.executor.medication.domain.EventType;
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId;

@Document(collection = MedicationDocument.COLLECTION)
public final class MedicationDocument {

	public static final String COLLECTION = "Medication";
	@Id
	@SuppressWarnings("unused")
	private String id;

	private final MedicationConceptField concept;
	private final SubjectId subjectId;
	private final LocalDateTime startDate;
	private final LocalDateTime endDate;
	private final String route;
	private final String doseFormat;
	private final String frequency;
	private final DosageField dosage;
	private final EventType eventType;

	@PersistenceConstructor
	//CHECKSTYLE:OFF
	public MedicationDocument(MedicationConceptField concept, SubjectId subjectId, LocalDateTime startDate,
			LocalDateTime endDate, String route, String doseFormat, String frequency, DosageField dosage,
			EventType eventType) {
		this.concept = concept;
		this.subjectId = subjectId;
		this.startDate = startDate;
		this.endDate = endDate;
		this.route = route;
		this.doseFormat = doseFormat;
		this.frequency = frequency;
		this.dosage = dosage;
		this.eventType = eventType;
	}
	//CHECKSTYLE:ON

	private MedicationDocument(Builder builder) {
		id = builder.id;
		concept = builder.concept;
		subjectId = builder.subjectId;
		startDate = builder.startDate;
		endDate = builder.endDate;
		route = builder.route;
		doseFormat = builder.doseFormat;
		frequency = builder.frequency;
		dosage = builder.dosage;
		eventType = builder.eventType;
	}

	public Builder toBuilder() {
		Builder builder = new Builder();
		builder.concept = concept;
		builder.subjectId = subjectId;
		builder.startDate = startDate;
		builder.endDate = endDate;
		builder.route = route;
		builder.doseFormat = doseFormat;
		builder.frequency = frequency;
		return builder;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public String getId() {
		return id;
	}

	public MedicationConceptField getConcept() {
		return concept;
	}

	public SubjectId getSubjectId() {
		return subjectId;
	}

	public LocalDateTime getStartDate() {
		return startDate;
	}

	public LocalDateTime getEndDate() {
		return endDate;
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

	public DosageField getDosage() {
		return dosage;
	}

	public EventType getEventType() {
		return eventType;
	}

	public static final class Builder {
		private String id;
		private MedicationConceptField concept;
		private SubjectId subjectId;
		private LocalDateTime startDate;
		private LocalDateTime endDate;
		private String route;
		private String doseFormat;
		private String frequency;
		private DosageField dosage;
		private EventType eventType;

		private Builder() {
		}

		public Builder withId(String id) {
			this.id = id;
			return this;
		}

		public Builder withConcept(MedicationConceptField concept) {
			this.concept = concept;
			return this;
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

		public Builder withDosage(DosageField dosage) {
			this.dosage = dosage;
			return this;
		}

		public Builder withEventType(EventType eventType) {
			this.eventType = eventType;
			return this;
		}

		public MedicationDocument build() {
			return new MedicationDocument(this);
		}
	}
}
