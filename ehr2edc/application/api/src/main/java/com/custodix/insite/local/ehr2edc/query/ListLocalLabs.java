package com.custodix.insite.local.ehr2edc.query;

import static com.custodix.insite.local.ehr2edc.shared.annotations.security.Allow.Rule.ASSIGNED_INVESTIGATOR;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.custodix.insite.local.ehr2edc.shared.annotations.AuthorizationCorrelator;
import com.custodix.insite.local.ehr2edc.shared.annotations.security.Allow;
import com.custodix.insite.local.ehr2edc.vocabulary.LabName;
import com.custodix.insite.local.ehr2edc.vocabulary.StudyId;

public interface ListLocalLabs {

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
		private final List<LabName> localLabs;

		private Response(Builder builder) {
			localLabs = builder.localLabs;
		}

		public List<LabName> getLocalLabs() {
			return localLabs;
		}

		public static Builder newBuilder() {
			return new Builder();
		}

		public static final class Builder {
			private List<LabName> localLabs;

			private Builder() {
			}

			public Builder withLocalLabs(List<LabName> localLabs) {
				this.localLabs = localLabs;
				return this;
			}

			public Response build() {
				return new Response(this);
			}
		}
	}
}
