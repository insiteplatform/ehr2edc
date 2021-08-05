package com.custodix.insite.local.ehr2edc.command;

import static com.custodix.insite.local.ehr2edc.shared.annotations.security.Allow.Rule.ANYONE;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.custodix.insite.local.ehr2edc.shared.annotations.security.Allow;
import com.custodix.insite.local.ehr2edc.vocabulary.StudyId;
import com.custodix.insite.local.ehr2edc.vocabulary.StudyODM;

public interface CreateStudy {

	@Allow(ANYONE)
	Response create(@Valid @NotNull Request request);

	final class Request {
		@NotNull
		@Valid
		private final StudyODM studyODM;

		private Request(Builder builder) {
			studyODM = builder.studyODM;
		}

		public StudyODM getStudyODM() {
			return studyODM;
		}

		public static Builder newBuilder() {
			return new Builder();
		}

		public static final class Builder {
			private StudyODM studyODM;

			private Builder() {
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

	final class Response {
		private final StudyId studyId;

		private Response(Builder builder) {
			studyId = builder.studyId;
		}

		public StudyId getStudyId() {
			return studyId;
		}

		public static Builder newBuilder() {
			return new Builder();
		}

		public static Builder newBuilder(Response copy) {
			Builder builder = new Builder();
			builder.studyId = copy.getStudyId();
			return builder;
		}

		public static final class Builder {
			private StudyId studyId;

			private Builder() {
			}

			public Response build() {
				return new Response(this);
			}

			public Builder withStudyId(StudyId studyId) {
				this.studyId = studyId;
				return this;
			}
		}
	}

}
