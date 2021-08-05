package com.custodix.insite.mongodb.export.patient.infrastructure.mongo.model.observationsummary;

import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import com.custodix.insite.mongodb.vocabulary.SubjectId;

@Document(collection = ObservationSummaryDocument.COLLECTION)
public final class ObservationSummaryDocument {
	public static final String COLLECTION = "ObservationSummary";

	@Id
	@SuppressWarnings("unused")
	private String id;

	private final SubjectId subjectId;
	private final LocalDate date;
	private final String category;
	private final Integer amountOfObservations;

	@PersistenceConstructor
	private ObservationSummaryDocument(final SubjectId subjectId, final LocalDate date, final String category, final Integer amountOfObservations) {
		this.subjectId = subjectId;
		this.date = date;
		this.category = category;
		this.amountOfObservations = amountOfObservations;
	}
	private ObservationSummaryDocument(Builder builder) {
		subjectId = builder.subjectId;
		date = builder.date;
		category = builder.category;
		amountOfObservations = builder.amountOfObservations;
	}
	public SubjectId getSubjectId() {
		return subjectId;
	}

	public LocalDate getDate() {
		return date;
	}

	public String getCategory() {
		return category;
	}

	public Integer getAmountOfObservations() {
		return amountOfObservations;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static final class Builder {
		private SubjectId subjectId;
		private LocalDate date;
		private String category;
		private Integer amountOfObservations;

		private Builder() {
		}

		public Builder withSubjectId(final SubjectId val) {
			subjectId = val;
			return this;
		}

		public Builder withDate(final LocalDate val) {
			date = val;
			return this;
		}

		public Builder withCategory(final String val) {
			category = val;
			return this;
		}

		public Builder withAmountOfObservations(final Integer val) {
			amountOfObservations = val;
			return this;
		}

		public ObservationSummaryDocument build() {
			return new ObservationSummaryDocument(this);
		}
	}
}
