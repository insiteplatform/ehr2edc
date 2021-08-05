package com.custodix.insite.mongodb.export.patient.infrastructure.mongo.model.labvalue;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import com.custodix.insite.mongodb.vocabulary.SubjectId;

@Document(collection = LabValueDocument.COLLECTION)
public final class LabValueDocument {
	public static final String COLLECTION = "LabValue";
	public static final String SUBJECTID_FIELD = "subjectId";
	public static final String CONCEPT_FIELD = "labConcept.concept";
	public static final String STARTDATE_FIELD = "startDate";
	public static final String ENDDATE_FIELD = "endDate";

	@Id
	@SuppressWarnings("unused")
	private String id;

	private final LabConcept labConcept;
	private final SubjectId subjectId;
	private final LocalDateTime startDate;
	private final LocalDateTime endDate;
	private final LabMeasurement quantitativeResult;
	private final LabValueInterpretation qualitativeResult;
	private final String vendor;

	@PersistenceConstructor
	private LabValueDocument(LabConcept labConcept, SubjectId subjectId, LocalDateTime startDate,
			LocalDateTime endDate, LabMeasurement quantitativeResult,
			LabValueInterpretation qualitativeResult, String vendor) {
		this.labConcept = labConcept;
		this.subjectId = subjectId;
		this.startDate = startDate;
		this.endDate = endDate;
		this.quantitativeResult = quantitativeResult;
		this.qualitativeResult = qualitativeResult;
		this.vendor = vendor;
	}

	private LabValueDocument(Builder builder) {
		labConcept = builder.labConceptField;
		subjectId = builder.subjectId;
		startDate = builder.startDate;
		endDate = builder.endDate;
		quantitativeResult = builder.quantitativeResult;
		qualitativeResult = builder.qualitativeResult;
		vendor = builder.vendor;
	}

	public static Builder newBuilder() {
		return new Builder();
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

	public LabConcept getLabConcept() {
		return labConcept;
	}

	public LabMeasurement getQuantitativeResult() {
		return quantitativeResult;
	}

	public LabValueInterpretation getQualitativeResult() {
		return qualitativeResult;
	}

	public String getVendor() {
		return vendor;
	}

	public static final class Builder {
		private LabConcept labConceptField;
		private SubjectId subjectId;
		private LocalDateTime startDate;
		private LocalDateTime endDate;
		private LabMeasurement quantitativeResult;
		private LabValueInterpretation qualitativeResult;
		private String vendor;

		private Builder() {
		}

		private Builder(LabConcept concept) {
			this.labConceptField = concept;
		}

		public Builder forSubject(final SubjectId val) {
			subjectId = val;
			return this;
		}

		public Builder withLabConcept(LabConcept val) {
			labConceptField = val;
			return this;
		}

		public Builder withStartDate(final LocalDateTime val) {
			startDate = val;
			return this;
		}

		public Builder withEndDate(final LocalDateTime val) {
			endDate = val;
			return this;
		}

		public Builder withQuantitativeResult(final LabMeasurement val) {
			quantitativeResult = val;
			return this;
		}

		public Builder withQualitativeResult(final LabValueInterpretation val) {
			this.qualitativeResult = val;
			return this;
		}

		public Builder withVendor(final String val) {
			this.vendor = val;
			return this;
		}

		public LabValueDocument build() {
			return new LabValueDocument(this);
		}
	}
}
