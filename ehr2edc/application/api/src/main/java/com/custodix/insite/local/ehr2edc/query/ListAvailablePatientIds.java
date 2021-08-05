package com.custodix.insite.local.ehr2edc.query;

import static com.custodix.insite.local.ehr2edc.shared.annotations.security.Allow.Rule.ASSIGNED_INVESTIGATOR;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.custodix.insite.local.ehr2edc.shared.annotations.AuthorizationCorrelator;
import com.custodix.insite.local.ehr2edc.shared.annotations.security.Allow;
import com.custodix.insite.local.ehr2edc.vocabulary.PatientCDWReference;
import com.custodix.insite.local.ehr2edc.vocabulary.StudyId;

public interface ListAvailablePatientIds {

	@Allow(ASSIGNED_INVESTIGATOR)
	Response list(@Valid @NotNull Request request);

	final class Request {
		@AuthorizationCorrelator
		@NotNull
		@Valid
		private final StudyId studyId;

		@NotNull
		@Size(min = 1,
			  max = PatientCDWReference.PATIENT_ID_SOURCE_MAX_LENGTH)
		private final String patientDomain;

		private final String filter;
		private final Long limit;

		private Request(Builder builder) {
			studyId = builder.studyId;
			patientDomain = builder.patientDomain;
			filter = builder.filter;
			limit = builder.limit;
		}

		public StudyId getStudyId() {
			return studyId;
		}

		public String getPatientDomain() {
			return patientDomain;
		}

		public String getFilter() {
			return filter;
		}

		public Long getLimit() {
			return limit;
		}

		public static Builder newBuilder() {
			return new Builder();
		}

		public static final class Builder {
			private StudyId studyId;
			private String patientDomain;
			private String filter;
			private Long limit;

			private Builder() {
			}

			public Builder withStudyId(StudyId studyId) {
				this.studyId = studyId;
				return this;
			}

			public Builder withPatientDomain(String patientDomain) {
				this.patientDomain = patientDomain;
				return this;
			}

			public Builder withFilter(String filter) {
				this.filter = filter;
				return this;
			}

			public Builder withLimit(Long limit) {
				this.limit = limit;
				return this;
			}

			public Request build() {
				return new Request(this);
			}
		}
	}

	final class Response {
		private final List<String> patientIds;

		private Response(Builder builder) {
			patientIds = builder.patientIds;
		}

		public List<String> getPatientIds() {
			return patientIds;
		}

		public static Builder newBuilder() {
			return new Builder();
		}

		public static Builder newBuilder(Response copy) {
			Builder builder = new Builder();
			builder.patientIds = copy.patientIds;
			return builder;
		}

		public static final class Builder {
			private List<String> patientIds;

			private Builder() {
			}

			public Builder withPatientIds(List<String> patientIds) {
				this.patientIds = patientIds;
				return this;
			}

			public Response build() {
				return new Response(this);
			}
		}
	}
}
