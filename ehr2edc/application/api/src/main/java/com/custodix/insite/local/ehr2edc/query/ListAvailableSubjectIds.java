package com.custodix.insite.local.ehr2edc.query;

import static com.custodix.insite.local.ehr2edc.shared.annotations.security.Allow.Rule.ASSIGNED_INVESTIGATOR;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.custodix.insite.local.ehr2edc.shared.annotations.AuthorizationCorrelator;
import com.custodix.insite.local.ehr2edc.shared.annotations.security.Allow;
import com.custodix.insite.local.ehr2edc.vocabulary.EDCSubjectReference;
import com.custodix.insite.local.ehr2edc.vocabulary.StudyId;

public interface ListAvailableSubjectIds {

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

			public Builder withStudyId(@NotNull @Valid StudyId studyId) {
				this.studyId = studyId;
				return this;
			}

			public Request build() {
				return new Request(this);
			}
		}
	}

	final class Response {
		private final boolean fromEDC;
		private final List<EDCSubjectReference> subjectIds;

		private Response(Builder builder) {
			fromEDC = builder.fromEDC;
			subjectIds = builder.subjectIds;
		}

		public boolean isFromEDC() {
			return fromEDC;
		}

		public List<EDCSubjectReference> getSubjectIds() {
			return subjectIds;
		}

		public static Builder newBuilder() {
			return new Builder();
		}

		public static Builder newBuilder(Response copy) {
			Builder builder = new Builder();
			builder.subjectIds = copy.subjectIds;
			return builder;
		}

		public static final class Builder {
			private boolean fromEDC;
			private List<EDCSubjectReference> subjectIds;

			private Builder() {
			}

			public Builder withFromEDC(boolean fromEDC) {
				this.fromEDC = fromEDC;
				return this;
			}

			public Builder withSubjectIds(List<EDCSubjectReference> subjectIds) {
				this.subjectIds = subjectIds;
				return this;
			}

			public Response build() {
				return new Response(this);
			}
		}
	}
}
