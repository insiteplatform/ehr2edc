package com.custodix.insite.local.ehr2edc.command;

import static com.custodix.insite.local.ehr2edc.shared.annotations.security.Allow.Rule.ANYONE;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.custodix.insite.local.ehr2edc.shared.annotations.SynchronizationCorrelator;
import com.custodix.insite.local.ehr2edc.shared.annotations.security.Allow;
import com.custodix.insite.local.ehr2edc.vocabulary.StudyId;
import com.custodix.insite.local.ehr2edc.vocabulary.StudyODM;

public interface UpdateStudyMetadata {

	@Allow(ANYONE)
	void update(@Valid @NotNull Request request);

	final class Request {
		@SynchronizationCorrelator
		@Valid
		@NotNull
		private StudyId studyId;
		@NotNull
		private StudyODM studyODM;

		private Request(Builder builder) {
			studyId = builder.studyId;
			studyODM = builder.studyODM;
		}

		public StudyId getStudyId() {
			return studyId;
		}

		public StudyODM getStudyODM() {
			return studyODM;
		}

		public static Builder newBuilder() {
			return new Builder();
		}

		public static final class Builder {
			private StudyId studyId;
			private StudyODM studyODM;

			private Builder() {
			}

			public Builder withStudyId(StudyId studyId) {
				this.studyId = studyId;
				return this;
			}

			public Builder withStudyODM(StudyODM studyODM) {
				this.studyODM = studyODM;
				return this;
			}

			public Request build() {
				return new Request(this);
			}
		}
	}
}
