package com.custodix.insite.local.ehr2edc.query;

import static com.custodix.insite.local.ehr2edc.shared.annotations.security.Allow.Rule.AUTHENTICATED;

import java.util.List;

import com.custodix.insite.local.ehr2edc.shared.annotations.security.Allow;
import com.custodix.insite.local.ehr2edc.vocabulary.StudyId;

public interface GetPatientDomains {

	@Allow(AUTHENTICATED)
	Response getAll(Request request);

	final class Request {
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

	final class Response {
		private List<PatientDomain> patientDomains;

		private Response(Builder builder) {
			patientDomains = builder.patientDomains;
		}

		public List<PatientDomain> getPatientDomains() {
			return patientDomains;
		}

		public static Builder newBuilder() {
			return new Builder();
		}

		public static final class Builder {
			private List<PatientDomain> patientDomains;

			private Builder() {
			}

			public Builder withPatientDomains(List<PatientDomain> val) {
				patientDomains = val;
				return this;
			}

			public Response build() {
				return new Response(this);
			}
		}
	}

	final class PatientDomain {
		private final String name;

		private PatientDomain(Builder builder) {
			name = builder.name;
		}

		public String getName() {
			return name;
		}

		public static Builder newBuilder() {
			return new Builder();
		}

		public static final class Builder {
			private String name;

			private Builder() {
			}

			public Builder withName(String val) {
				name = val;
				return this;
			}

			public PatientDomain build() {
				return new PatientDomain(this);
			}
		}
	}
}