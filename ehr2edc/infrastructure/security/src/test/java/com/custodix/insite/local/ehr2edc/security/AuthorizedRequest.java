package com.custodix.insite.local.ehr2edc.security;

import static com.custodix.insite.local.ehr2edc.shared.annotations.security.Allow.Rule.ASSIGNED_INVESTIGATOR;
import static com.custodix.insite.local.ehr2edc.shared.annotations.security.Allow.Rule.AUTHENTICATED;
import static com.custodix.insite.local.ehr2edc.shared.annotations.security.Allow.Rule.DRM;

import com.custodix.insite.local.ehr2edc.shared.annotations.AuthorizationCorrelator;
import com.custodix.insite.local.ehr2edc.shared.annotations.security.Allow;
import com.custodix.insite.local.ehr2edc.vocabulary.StudyId;

public interface AuthorizedRequest {

	@Allow(AUTHENTICATED)
	void doAuthenticatedRequest(Request request);

	@Allow(ASSIGNED_INVESTIGATOR)
	void doRequest(Request request);

	@Allow(DRM)
	void doDRMRequest();

	final class Request {

		@AuthorizationCorrelator
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
