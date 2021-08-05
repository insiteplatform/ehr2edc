package com.custodix.insite.local.ehr2edc.vocabulary;

import java.net.URI;

public final class EHRConnection {
	private final StudyId studyId;
	private final URI uri;
	private final EHRSystem system;

	private EHRConnection(Builder builder) {
		studyId = builder.studyId;
		uri = builder.uri;
		system = builder.system;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public StudyId getStudyId() {
		return studyId;
	}

	public URI getUri() {
		return uri;
	}

	public EHRSystem getSystem() {
		return system;
	}

	public static final class Builder {
		private StudyId studyId;
		private URI uri;
		private EHRSystem system;

		private Builder() {
		}

		public Builder withStudyId(StudyId studyId) {
			this.studyId = studyId;
			return this;
		}

		public Builder withUri(URI uri) {
			this.uri = uri;
			return this;
		}

		public Builder withSystem(EHRSystem system) {
			this.system = system;
			return this;
		}

		public EHRConnection build() {
			return new EHRConnection(this);
		}
	}
}
