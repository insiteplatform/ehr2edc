package com.custodix.insite.local.ehr2edc.command;

import static com.custodix.insite.local.ehr2edc.shared.annotations.security.Allow.Rule.ANYONE;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.custodix.insite.local.ehr2edc.shared.annotations.SynchronizationCorrelator;
import com.custodix.insite.local.ehr2edc.shared.annotations.security.Allow;
import com.custodix.insite.local.ehr2edc.vocabulary.StudyId;

public interface DeleteStudy {
	@Allow(ANYONE)
	void delete(@Valid @NotNull Request request);

	final class Request {
		@SynchronizationCorrelator
		@Valid
		@NotNull
		private StudyId studyId;

		private Request(Builder builder) {
			studyId = builder.studyId;
		}

		public static Builder newBuilder() {
			return new Builder();
		}

		public StudyId getStudyId() {
			return studyId;
		}

		public static final class Builder {
			private StudyId studyId;

			private Builder() {
			}

			public Builder withStudyId(StudyId studyId) {
				this.studyId = studyId;
				return this;
			}

			public Request build() {
				return new Request(this);
			}
		}
	}
}
