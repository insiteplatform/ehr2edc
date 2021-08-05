package com.custodix.insite.mongodb.export.patient.domain.event;

import java.time.LocalDateTime;

public final class ExportPatientEnded {

	private final String patientId;
	private final String namespace;
	private final String subjectId;
	private final LocalDateTime date;

	private ExportPatientEnded(final Builder builder) {
		patientId = builder.patientId;
		namespace = builder.namespace;
		subjectId = builder.subjectId;
		date = builder.date;
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

	public LocalDateTime getDate() {
		return date;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static final class Builder {
		private String subjectId;
		private LocalDateTime date;
		private String patientId;
		private String namespace;

		private Builder() {
		}

		public Builder withSubjectId(final String val) {
			subjectId = val;
			return this;
		}

		public Builder withDate(final LocalDateTime val) {
			date = val;
			return this;
		}

		public ExportPatientEnded build() {
			return new ExportPatientEnded(this);
		}

		public Builder withPatientId(final String val) {
			patientId = val;
			return this;
		}

		public Builder withNamespace(final String val) {
			namespace = val;
			return this;
		}
	}
}