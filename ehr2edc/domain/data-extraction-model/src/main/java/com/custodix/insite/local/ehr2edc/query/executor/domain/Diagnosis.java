package com.custodix.insite.local.ehr2edc.query.executor.domain;

import java.time.ZonedDateTime;

import com.custodix.insite.local.ehr2edc.query.executor.common.concept.ConceptCode;
import com.custodix.insite.local.ehr2edc.query.executor.common.domain.HasSubjectId;
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId;

public class Diagnosis implements HasSubjectId {
	private SubjectId subjectId;
	private ConceptCode diagnosisConcept;
	private ZonedDateTime dateTime;

	private Diagnosis(final Builder builder) {
		subjectId = builder.subjectId;
		diagnosisConcept = builder.diagnosisConcept;
		dateTime = builder.dateTime;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static Builder newBuilder(final Diagnosis copy) {
		Builder builder = new Builder();
		builder.subjectId = copy.getSubjectId();
		builder.diagnosisConcept = copy.getDiagnosisConcept();
		builder.dateTime = copy.getDateTime();
		return builder;
	}

	@Override
	public SubjectId getSubjectId() {
		return subjectId;
	}

	public ConceptCode getDiagnosisConcept() {
		return diagnosisConcept;
	}

	public ZonedDateTime getDateTime() {
		return dateTime;
	}

	public static final class Builder {
		private SubjectId subjectId;
		private ConceptCode diagnosisConcept;
		private ZonedDateTime dateTime;

		private Builder() {
		}

		public Builder withSubjectId(final SubjectId val) {
			subjectId = val;
			return this;
		}

		public Builder withDiagnosisConcept(final ConceptCode val) {
			diagnosisConcept = val;
			return this;
		}

		public Builder withDateTime(final ZonedDateTime val) {
			dateTime = val;
			return this;
		}

		public Diagnosis build() {
			return new Diagnosis(this);
		}
	}
}
