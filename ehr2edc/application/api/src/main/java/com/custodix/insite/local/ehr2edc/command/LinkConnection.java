package com.custodix.insite.local.ehr2edc.command;

import static com.custodix.insite.local.ehr2edc.shared.annotations.security.Allow.Rule.ANYONE;

import java.net.URI;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.custodix.insite.local.ehr2edc.shared.annotations.SynchronizationCorrelator;
import com.custodix.insite.local.ehr2edc.shared.annotations.security.Allow;
import com.custodix.insite.local.ehr2edc.vocabulary.EDCSystem;
import com.custodix.insite.local.ehr2edc.vocabulary.ExternalSiteId;
import com.custodix.insite.local.ehr2edc.vocabulary.StudyConnectionType;
import com.custodix.insite.local.ehr2edc.vocabulary.StudyId;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

public interface LinkConnection {

	@Allow(ANYONE)
	void link(@Valid @NotNull Request request);

	@JsonDeserialize(builder = Request.Builder.class)
	final class Request {
		@SynchronizationCorrelator
		@NotNull
		@Valid
		private final StudyId studyId;
		@NotNull
		private final StudyConnectionType type;
		@NotNull
		private final EDCSystem edcSystem;
		@NotNull
		@Valid
		private final ExternalSiteId externalSiteId;
		@NotNull
		private final URI clinicalDataURI;
		@NotBlank
		private final String username;
		@NotBlank
		private final String password;
		@NotNull
		private final Boolean enabled;
		private final StudyId studyIdOverride;

		private Request(Builder builder) {
			studyId = builder.studyId;
			type = builder.type;
			edcSystem = builder.edcSystem;
			externalSiteId = builder.externalSiteId;
			clinicalDataURI = builder.clinicalDataURI;
			username = builder.username;
			password = builder.password;
			enabled = builder.enabled;
			studyIdOverride = builder.studyIdOverride;
		}

		public static Builder newBuilder(Request copy) {
			Builder builder = new Builder();
			builder.studyId = copy.getStudyId();
			builder.type = copy.getType();
			builder.edcSystem = copy.getEdcSystem();
			builder.externalSiteId = copy.getExternalSiteId();
			builder.clinicalDataURI = copy.getClinicalDataURI();
			builder.username = copy.getUsername();
			builder.password = copy.getPassword();
			builder.enabled = copy.isEnabled();
			builder.studyIdOverride = copy.getStudyIdOverride();
			return builder;
		}

		public StudyId getStudyId() {
			return studyId;
		}

		public StudyConnectionType getType() {
			return type;
		}

		public EDCSystem getEdcSystem() {
			return edcSystem;
		}

		public ExternalSiteId getExternalSiteId() {
			return externalSiteId;
		}

		public URI getClinicalDataURI() {
			return clinicalDataURI;
		}

		public String getUsername() {
			return username;
		}

		public String getPassword() {
			return password;
		}

		public Boolean isEnabled() {
			return enabled;
		}

		public StudyId getStudyIdOverride() {
			return studyIdOverride;
		}

		public static Builder newBuilder() {
			return new Builder();
		}

		public static final class Builder {
			private StudyId studyId;
			private StudyConnectionType type;
			private EDCSystem edcSystem;
			private ExternalSiteId externalSiteId;
			private URI clinicalDataURI;
			private String username;
			private String password;
			private Boolean enabled;
			private StudyId studyIdOverride;

			private Builder() {
			}

			public Builder withStudyIdOverride(StudyId studyIdOverride) {
				this.studyIdOverride = studyIdOverride;
				return this;
			}

			public Builder withStudyId(StudyId studyId) {
				this.studyId = studyId;
				return this;
			}

			public Builder withEdcSystem(EDCSystem edcSystem) {
				this.edcSystem = edcSystem;
				return this;
			}

			public Builder withType(StudyConnectionType type) {
				this.type = type;
				return this;
			}

			public Builder withExternalSiteId(ExternalSiteId externalSiteId) {
				this.externalSiteId = externalSiteId;
				return this;
			}

			public Builder withClinicalDataURI(URI clinicalDataURI) {
				this.clinicalDataURI = clinicalDataURI;
				return this;
			}

			public Builder withUsername(String username) {
				this.username = username;
				return this;
			}

			public Builder withPassword(String password) {
				this.password = password;
				return this;
			}

			public Builder withEnabled(Boolean enabled) {
				this.enabled = enabled;
				return this;
			}

			public Request build() {
				return new Request(this);
			}
		}
	}
}
