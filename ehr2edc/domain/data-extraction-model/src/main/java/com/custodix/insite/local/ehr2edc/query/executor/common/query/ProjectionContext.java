package com.custodix.insite.local.ehr2edc.query.executor.common.query;

import java.time.LocalDate;

import com.custodix.insite.local.ehr2edc.vocabulary.EDCSubjectReference;
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId;

public final class ProjectionContext {
	private final DataPoint dataPoint;
	private final SubjectId subjectId;
	private final EDCSubjectReference edcSubjectReference;
	private final LocalDate referenceDate;
	private final LocalDate consentDate;

	private ProjectionContext(Builder builder) {
		dataPoint = builder.dataPoint;
		subjectId = builder.subjectId;
		edcSubjectReference = builder.edcSubjectReference;
		referenceDate = builder.referenceDate;
		consentDate = builder.consentDate;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public DataPoint getDataPoint() {
		return dataPoint;
	}

	public SubjectId getSubjectId() {
		return subjectId;
	}

	public EDCSubjectReference getEdcSubjectReference() {
		return edcSubjectReference;
	}

	public LocalDate getReferenceDate() {
		return referenceDate;
	}

	public LocalDate getConsentDate() {
		return consentDate;
	}

	public static final class Builder {
		private DataPoint dataPoint;
		private SubjectId subjectId;
		private EDCSubjectReference edcSubjectReference;
		private LocalDate referenceDate;
		private LocalDate consentDate;

		private Builder() {
		}

		public Builder withDataPoint(DataPoint dataPoint) {
			this.dataPoint = dataPoint;
			return this;
		}

		public Builder withSubjectId(SubjectId subjectId) {
			this.subjectId = subjectId;
			return this;
		}

		public Builder withEdcSubjectReference(EDCSubjectReference edcSubjectReference) {
			this.edcSubjectReference = edcSubjectReference;
			return this;
		}

		public Builder withReferenceDate(LocalDate referenceDate) {
			this.referenceDate = referenceDate;
			return this;
		}

		public Builder withConsentDate(LocalDate val) {
			consentDate = val;
			return this;
		}

		public ProjectionContext build() {
			return new ProjectionContext(this);
		}
	}
}
