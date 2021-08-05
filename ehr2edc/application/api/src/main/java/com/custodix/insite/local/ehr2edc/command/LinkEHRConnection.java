package com.custodix.insite.local.ehr2edc.command;

import static com.custodix.insite.local.ehr2edc.shared.annotations.security.Allow.Rule.ANYONE;

import java.net.URI;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.custodix.insite.local.ehr2edc.shared.annotations.SynchronizationCorrelator;
import com.custodix.insite.local.ehr2edc.shared.annotations.security.Allow;
import com.custodix.insite.local.ehr2edc.vocabulary.EHRSystem;
import com.custodix.insite.local.ehr2edc.vocabulary.StudyId;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

public interface LinkEHRConnection {
	@Allow(ANYONE)
	void link(@Valid @NotNull Request request);

	@JsonDeserialize(builder = Request.Builder.class)
	final class Request {
		@SynchronizationCorrelator
		@NotNull
		@Valid
		private final StudyId studyId;
		@NotNull
		private final EHRSystem system;
		@NotNull
		private final URI uri;

		private Request(Builder builder) {
			studyId = builder.studyId;
			system = builder.system;
			uri = builder.uri;
		}

		public static Builder newBuilder() {
			return new Builder();
		}

		public StudyId getStudyId() {
			return studyId;
		}

		public EHRSystem getSystem() {
			return system;
		}

		public URI getUri() {
			return uri;
		}

		public static final class Builder {
			private StudyId studyId;
			private EHRSystem system;
			private URI uri;

			private Builder() {
			}

			public Builder withStudyId(StudyId studyId) {
				this.studyId = studyId;
				return this;
			}

			public Builder withSystem(EHRSystem system) {
				this.system = system;
				return this;
			}

			public Builder withUri(URI uri) {
				this.uri = uri;
				return this;
			}

			public Request build() {
				return new Request(this);
			}
		}
	}
}
