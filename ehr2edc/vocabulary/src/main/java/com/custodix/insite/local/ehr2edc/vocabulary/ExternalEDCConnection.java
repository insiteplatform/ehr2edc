package com.custodix.insite.local.ehr2edc.vocabulary;

import java.net.URI;
import java.util.Objects;
import java.util.Optional;

public final class ExternalEDCConnection {
	private final StudyId studyId;
	private final StudyConnectionType connectionType;
	private final EDCSystem edcSystem;
	private final ExternalSiteId externalSiteId;
	private final StudyId studyIdOverride;
	private final URI clinicalDataURI;
	private final String username;
	private final String password;
	private final boolean enabled;

	private ExternalEDCConnection(Builder builder) {
		studyId = builder.studyId;
		connectionType = builder.connectionType;
		edcSystem = builder.edcSystem;
		externalSiteId = builder.externalSiteId;
		studyIdOverride = builder.studyIdOverride;
		clinicalDataURI = builder.clinicalDataURI;
		username = builder.username;
		password = builder.password;
		enabled = builder.enabled;
	}

	public StudyId getStudyId() {
		return studyId;
	}

	public StudyConnectionType getConnectionType() {
		return connectionType;
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

	public boolean isEnabled() {
		return enabled;
	}

	public Optional<StudyId> getStudyIdOverride() {
		return Optional.ofNullable(studyIdOverride);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		ExternalEDCConnection that = (ExternalEDCConnection) o;
		return enabled == that.enabled && Objects.equals(studyId, that.studyId) && connectionType == that.connectionType
				&& edcSystem == that.edcSystem && Objects.equals(externalSiteId, that.externalSiteId) && Objects.equals(
				studyIdOverride, that.studyIdOverride) && Objects.equals(clinicalDataURI, that.clinicalDataURI)
				&& Objects.equals(username, that.username) && Objects.equals(password, that.password);
	}

	@Override
	public int hashCode() {
		return Objects.hash(studyId, connectionType, edcSystem, externalSiteId, studyIdOverride, clinicalDataURI,
				username, password, enabled);
	}

	@Override
	public String toString() {
		return "ExternalEDCConnection{" + "studyId=" + studyId + ", connectionType=" + connectionType + ", edcSystem="
				+ edcSystem + ", externalSiteId=" + externalSiteId + ", studyIdOverride=" + studyIdOverride
				+ ", clinicalDataURI=" + clinicalDataURI + ", username='" + username + '\'' + ", password='" + password
				+ '\'' + ", enabled=" + enabled + '}';
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static Builder newBuilder(ExternalEDCConnection copy) {
		Builder builder = new Builder();
		builder.studyId = copy.studyId;
		builder.connectionType = copy.connectionType;
		builder.edcSystem = copy.edcSystem;
		builder.externalSiteId = copy.getExternalSiteId();
		builder.clinicalDataURI = copy.getClinicalDataURI();
		builder.username = copy.getUsername();
		builder.password = copy.getPassword();
		builder.enabled = copy.isEnabled();
		builder.studyIdOverride = copy.studyIdOverride;
		return builder;
	}

	public static final class Builder {
		private StudyId studyId;
		private StudyConnectionType connectionType;
		private EDCSystem edcSystem;
		private ExternalSiteId externalSiteId;
		private StudyId studyIdOverride;
		private URI clinicalDataURI;
		private String username;
		private String password;
		private boolean enabled;

		private Builder() {
		}

		public Builder withStudyId(StudyId studyId) {
			this.studyId = studyId;
			return this;
		}

		public Builder withConnectionType(StudyConnectionType connectionType) {
			this.connectionType = connectionType;
			return this;
		}

		public Builder withEdcSystem(EDCSystem edcSystem) {
			this.edcSystem = edcSystem;
			return this;
		}

		public Builder withExternalSiteId(ExternalSiteId externalSiteId) {
			this.externalSiteId = externalSiteId;
			return this;
		}

		public Builder withStudyIdOVerride(StudyId studyIdOverride) {
			this.studyIdOverride = studyIdOverride;
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

		public Builder withEnabled(boolean enabled) {
			this.enabled = enabled;
			return this;
		}

		public ExternalEDCConnection build() {
			return new ExternalEDCConnection(this);
		}
	}
}
