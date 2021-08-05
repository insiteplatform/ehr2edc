package com.custodix.insite.local.ehr2edc.query.executor.laboratory.domain;

import java.time.LocalDateTime;

import com.custodix.insite.local.ehr2edc.query.executor.common.concept.ConceptCode;
import com.custodix.insite.local.ehr2edc.query.executor.common.domain.HasConcept;
import com.custodix.insite.local.ehr2edc.query.executor.common.domain.HasSubjectId;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.DataPoint;
import com.custodix.insite.local.ehr2edc.query.executor.domain.Measurement;
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId;

public final class LabValue implements DataPoint, HasSubjectId, HasConcept {

	private final LabConcept labConcept;
	private final SubjectId subjectId;
	private final LocalDateTime startDate;
	private final LocalDateTime endDate;
	private final Measurement quantitativeResult;
	private final LabValueInterpretation qualitativeResult;
	private final String vendor;

	private LabValue(Builder builder) {
		labConcept = builder.labConcept;
		subjectId = builder.subjectId;
		startDate = builder.startDate;
		endDate = builder.endDate;
		quantitativeResult = builder.quantitativeResult;
		qualitativeResult = builder.qualitativeResult;
		vendor = builder.vendor;
	}

	public static Builder newBuilder(final LabValue copy) {
		Builder builder = new Builder();
		builder.startDate = copy.getStartDate();
		builder.endDate = copy.getEndDate();
		builder.subjectId = copy.getSubjectId();
		builder.labConcept = copy.getLabConcept();
		builder.quantitativeResult = copy.getQuantitativeResult();
		return builder;
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

	public Measurement getQuantitativeResult() {
		return quantitativeResult;
	}

	public LabValueInterpretation getQualitativeResult() {
		return qualitativeResult;
	}

	public String getVendor() {
		return vendor;
	}

	@Override
	public ConceptCode getConcept() {
		return labConcept.getConcept();
	}

	public static final class Builder {
		private LabConcept labConcept;
		private SubjectId subjectId;
		private LocalDateTime startDate;
		private LocalDateTime endDate;
		private Measurement quantitativeResult;
		private LabValueInterpretation qualitativeResult;
		private String vendor;

		private Builder() {
		}

		private Builder(LabConcept concept) {
			this.labConcept = concept;
		}

		public Builder forSubject(final SubjectId val) {
			subjectId = val;
			return this;
		}

		public Builder withLabConcept(LabConcept val) {
			labConcept = val;
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

		public Builder withQuantitativeResult(final Measurement val) {
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

		public LabValue build() {
			return new LabValue(this);
		}
	}
}
