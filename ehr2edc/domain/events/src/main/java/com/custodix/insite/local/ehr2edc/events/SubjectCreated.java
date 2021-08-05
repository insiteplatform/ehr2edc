package com.custodix.insite.local.ehr2edc.events;

import com.custodix.insite.local.ehr2edc.vocabulary.EDCSubjectReference;
import com.custodix.insite.local.ehr2edc.vocabulary.StudyId;

public final class SubjectCreated implements AsyncSerializationSafeDomainEvent {

	private final StudyId studyId;
	private final EDCSubjectReference edcSubjectReference;

	private SubjectCreated(Builder builder) {
		studyId = builder.studyId;
		edcSubjectReference = builder.edcSubjectReference;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public StudyId getStudyId() {
		return studyId;
	}

	public EDCSubjectReference getEdcSubjectReference() {
		return edcSubjectReference;
	}

	public static final class Builder {
		private StudyId studyId;
		private EDCSubjectReference edcSubjectReference;

		private Builder() {
		}

		public Builder withStudyId(StudyId studyId) {
			this.studyId = studyId;
			return this;
		}

		public Builder withEdcSubjectReference(EDCSubjectReference edcSubjectReference) {
			this.edcSubjectReference = edcSubjectReference;
			return this;
		}

		public SubjectCreated build() {
			return new SubjectCreated(this);
		}
	}
}
