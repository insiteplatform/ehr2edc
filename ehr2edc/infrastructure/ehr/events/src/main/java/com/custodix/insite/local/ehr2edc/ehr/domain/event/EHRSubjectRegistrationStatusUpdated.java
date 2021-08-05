package com.custodix.insite.local.ehr2edc.ehr.domain.event;

public final class EHRSubjectRegistrationStatusUpdated implements AsyncSerializationSafeDomainEvent {
	private final String patientId;
	private final String namespace;
	private final String subjectId;
	private final EHRSubjectRegistrationStatus status;

	private EHRSubjectRegistrationStatusUpdated(Builder builder) {
		patientId = builder.patientId;
		namespace = builder.namespace;
		subjectId = builder.subjectId;
		status = builder.status;
	}

	public String getPatientId() {
		return patientId;
	}

	public String getNamespace() {
		return namespace;
	}

	public String getSubjectId() {
		return subjectId;
	}

	public EHRSubjectRegistrationStatus getStatus() {
		return status;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static final class Builder {
		private String patientId;
		private String namespace;
		private String subjectId;
		private EHRSubjectRegistrationStatus status;

		private Builder() {
		}

		public Builder withPatientId(String val) {
			patientId = val;
			return this;
		}

		public Builder withNamespace(String val) {
			namespace = val;
			return this;
		}

		public Builder withSubjectId(String val) {
			subjectId = val;
			return this;
		}

		public Builder withStatus(EHRSubjectRegistrationStatus val) {
			status = val;
			return this;
		}

		public EHRSubjectRegistrationStatusUpdated build() {
			return new EHRSubjectRegistrationStatusUpdated(this);
		}
	}
}
