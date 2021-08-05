package com.custodix.insite.local.ehr2edc.query.mongo.laboratory.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId;

@Document(collection = LabValueDocument.COLLECTION)
public final class LabValueDocument {

	public static final String COLLECTION = "LabValue";
	@Id
	@SuppressWarnings("unused")
	private String id;

	private final LabConceptField labConcept;
	private final SubjectId subjectId;
	private final LocalDateTime startDate;
	private final LocalDateTime endDate;
	private final LabMeasurementField quantitativeResult;
	private final LabValueInterpretationField qualitativeResult;
	private final String vendor;

	@PersistenceConstructor
	private LabValueDocument(LabConceptField labConcept, SubjectId subjectId, LocalDateTime startDate,
			LocalDateTime endDate, LabMeasurementField quantitativeResult,
			LabValueInterpretationField qualitativeResult, String vendor) {
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

	public LabConceptField getLabConcept() {
		return labConcept;
	}

	public LabMeasurementField getQuantitativeResult() {
		return quantitativeResult;
	}

	public LabValueInterpretationField getQualitativeResult() {
		return qualitativeResult;
	}

	public String getVendor() {
		return vendor;
	}

	public static final class Builder {
		private LabConceptField labConceptField;
		private SubjectId subjectId;
		private LocalDateTime startDate;
		private LocalDateTime endDate;
		private LabMeasurementField quantitativeResult;
		private LabValueInterpretationField qualitativeResult;
		private String vendor;

		private Builder() {
		}

		public Builder forSubject(final SubjectId val) {
			subjectId = val;
			return this;
		}

		public Builder withLabConcept(LabConceptField val) {
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

		public Builder withQuantitativeResult(final LabMeasurementField val) {
			quantitativeResult = val;
			return this;
		}

		public Builder withQualitativeResult(final LabValueInterpretationField val) {
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
