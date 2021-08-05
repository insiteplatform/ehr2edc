package com.custodix.insite.local.ehr2edc.command;

import static com.custodix.insite.local.ehr2edc.shared.annotations.security.Allow.Rule.ANYONE;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.custodix.insite.local.ehr2edc.shared.annotations.security.Allow;
import com.custodix.insite.local.ehr2edc.vocabulary.EDCSubjectReference;
import com.custodix.insite.local.ehr2edc.vocabulary.StudyId;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

public interface CreateSubjectInEDC {

	@Allow(ANYONE)
	void create(@Valid @NotNull Request request);

	@JsonDeserialize(builder = Request.Builder.class)
	final class Request {
		@NotNull
		@Valid
		private final StudyId studyId;
		@NotNull
		@Valid
		private final EDCSubjectReference edcSubjectReference;

		private Request(Builder builder) {
			studyId = builder.studyId;
			edcSubjectReference = builder.edcSubjectReference;
		}

		public StudyId getStudyId() {
			return studyId;
		}

		public EDCSubjectReference getEdcSubjectReference() {
			return edcSubjectReference;
		}

		public static Builder newBuilder() {
			return new Builder();
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

			public Request build() {
				return new Request(this);
			}
		}
	}
}
