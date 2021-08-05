package com.custodix.insite.local.ehr2edc.command;

import static com.custodix.insite.local.ehr2edc.shared.annotations.security.Allow.Rule.DRM;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.custodix.insite.local.ehr2edc.shared.annotations.security.Allow;
import com.custodix.insite.local.ehr2edc.shared.annotations.SynchronizationCorrelator;
import com.custodix.insite.local.ehr2edc.vocabulary.StudyId;
import com.custodix.insite.local.ehr2edc.vocabulary.UserIdentifier;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

public interface AssignInvestigator {

	@Allow(DRM)
	void assign(@Valid @NotNull Request request);

	@JsonDeserialize(builder = Request.Builder.class)
	final class Request {
		@SynchronizationCorrelator
		@NotNull
		@Valid
		private final StudyId studyId;

		@NotNull
		@Valid
		private final UserIdentifier investigatorId;

		private Request(Builder builder) {
			studyId = builder.studyId;
			investigatorId = builder.investigatorId;
		}

		public StudyId getStudyId() {
			return studyId;
		}

		public UserIdentifier getInvestigatorId() {
			return investigatorId;
		}

		public static Builder newBuilder() {
			return new Builder();
		}

		public static Builder newBuilder(Request copy) {
			Builder builder = new Builder();
			builder.studyId = copy.studyId;
			builder.investigatorId = copy.investigatorId;
			return builder;
		}

		@JsonPOJOBuilder
		public static final class Builder {
			private StudyId studyId;
			private UserIdentifier investigatorId;

			private Builder() {
			}

			public Builder withStudyId(StudyId studyId) {
				this.studyId = studyId;
				return this;
			}

			public Builder withInvestigatorId(UserIdentifier val) {
				investigatorId = val;
				return this;
			}

			public Request build() {
				return new Request(this);
			}
		}
	}
}