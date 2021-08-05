package com.custodix.insite.local.ehr2edc.query;

import static com.custodix.insite.local.ehr2edc.shared.annotations.security.Allow.Rule.ASSIGNED_INVESTIGATOR;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.custodix.insite.local.ehr2edc.shared.annotations.AuthorizationCorrelator;
import com.custodix.insite.local.ehr2edc.shared.annotations.security.Allow;
import com.custodix.insite.local.ehr2edc.vocabulary.StudyId;
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId;

public interface ListSubjects {

	@Allow(ASSIGNED_INVESTIGATOR)
	Response list(@Valid @NotNull Request request);

	final class Request {
		@AuthorizationCorrelator
		@NotNull
		@Valid
		private final StudyId studyId;

		private Request(Builder builder) {
			studyId = builder.studyId;
		}

		public StudyId getStudyId() {
			return studyId;
		}

		public static Builder newBuilder() {
			return new Builder();
		}

		public static Builder newBuilder(Request copy) {
			Builder builder = new Builder();
			builder.studyId = copy.studyId;
			return builder;
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

	final class Response {
		private final List<SubjectId> subjects;

		private Response(Builder builder) {
			subjects = builder.subjects;
		}

		public List<SubjectId> getSubjects() {
			return subjects;
		}

		public static Builder newBuilder() {
			return new Builder();
		}

		public static final class Builder {
			private List<SubjectId> subjects;

			private Builder() {
			}

			public Builder withSubjects(List<SubjectId> subjects) {
				this.subjects = subjects;
				return this;
			}

			public Response build() {
				return new Response(this);
			}
		}
	}
}