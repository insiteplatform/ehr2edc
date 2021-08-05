package com.custodix.insite.mongodb.export.patient.domain.event;

import java.time.LocalDateTime;

public final class ExportPatientStarting {

	private final String patientId;
	private final String namespace;
	private final String subjectId;
	private final LocalDateTime date;
	private final String patientExporterId;

	private ExportPatientStarting(final Builder builder) {
		patientId = builder.patientId;
		namespace = builder.namespace;
		subjectId = builder.subjectId;
		date = builder.date;
		patientExporterId = builder.patientExporterId;
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

	public String getPatientExporterId() {
		return patientExporterId;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static final class Builder {
		private String subjectId;
		private LocalDateTime date;
		private String patientExporterId;
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

		public Builder withPatientExporterId(final String val) {
			patientExporterId = val;
			return this;
		}

		public Builder withPatientId(final String val) {
			patientId = val;
			return this;
		}

		public Builder withNamespace(final String val) {
			namespace = val;
			return this;
		}

		public ExportPatientStarting build() {
			return new ExportPatientStarting(this);
		}
	}
}
