package com.custodix.insite.local.ehr2edc.query;

import static com.custodix.insite.local.ehr2edc.shared.annotations.security.Allow.Rule.AUTHENTICATED;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.custodix.insite.local.ehr2edc.shared.annotations.security.Allow;
import com.custodix.insite.local.ehr2edc.vocabulary.PatientCDWReference;
import com.custodix.insite.local.ehr2edc.vocabulary.StudyId;

public interface ListAvailableStudies {

	@Allow(AUTHENTICATED)
	Response availableStudies(@Valid @NotNull Request request);

	final class Request {
		@NotNull
		@Valid
		private final PatientCDWReference patientCDWReference;

		private Request(Builder builder) {
			patientCDWReference = builder.patientCDWReference;
		}

		public PatientCDWReference getPatientCDWReference() {
			return patientCDWReference;
		}

		public static Builder newBuilder() {
			return new Builder();
		}

		public static Builder newBuilder(Request copy) {
			Builder builder = new Builder();
			builder.patientCDWReference = copy.patientCDWReference;
			return builder;
		}

		public static final class Builder {
			private PatientCDWReference patientCDWReference;

			private Builder() {
			}

			public Builder withPatientCDWReference(PatientCDWReference patientCDWReference) {
				this.patientCDWReference = patientCDWReference;
				return this;
			}

			public Request build() {
				return new Request(this);
			}
		}
	}

	final class Response {
		private final List<Study> availableStudies;

		private Response(Builder builder) {
			availableStudies = builder.availableStudies;
		}

		public List<Study> getAvailableStudies() {
			return availableStudies;
		}

		public static Builder newBuilder() {
			return new Builder();
		}

		public static Builder newBuilder(Response copy) {
			Builder builder = new Builder();
			builder.availableStudies = copy.availableStudies;
			return builder;
		}

		public static final class Builder {
			private List<Study> availableStudies = new ArrayList<>();

			private Builder() {
			}

			public Builder withAvailableStudies(List<Study> availableStudies) {
				this.availableStudies = availableStudies;
				return this;
			}

			public Response build() {
				return new Response(this);
			}
		}
	}

	final class Study {
		private final StudyId studyId;
		private final String name;
		private final String description;

		private Study(Builder builder) {
			studyId = builder.studyId;
			name = builder.name;
			description = builder.description;
		}

		public StudyId getStudyId() {
			return studyId;
		}

		public String getName() {
			return name;
		}

		public String getDescription() {
			return description;
		}

		public static Builder newBuilder() {
			return new Builder();
		}

		public static Builder newBuilder(Study copy) {
			Builder builder = new Builder();
			builder.studyId = copy.studyId;
			builder.name = copy.name;
			builder.description = copy.description;
			return builder;
		}

		public static final class Builder {
			private StudyId studyId;
			private String name;
			private String description;

			private Builder() {
			}

			public Builder withStudyId(StudyId studyId) {
				this.studyId = studyId;
				return this;
			}

			public Builder withName(String name) {
				this.name = name;
				return this;
			}

			public Builder withDescription(String description) {
				this.description = description;
				return this;
			}

			public Study build() {
				return new Study(this);
			}
		}
	}
}