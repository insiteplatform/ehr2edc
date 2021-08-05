package com.custodix.insite.mongodb.export.patient.domain.model.medication;

import java.sql.Timestamp;

public final class MedicationRecord {
	private final String patientNum;
	private final String namespace;
	private final Timestamp startDate;
	private final Timestamp endDate;
	private final String conceptCode;
	private final String conceptName;
	private final Long observationId;

	private MedicationRecord(Builder builder) {
		patientNum = builder.patientNum;
		namespace = builder.namespace;
		startDate = builder.startDate;
		endDate = builder.endDate;
		conceptCode = builder.conceptCode;
		conceptName = builder.conceptName;
		observationId = builder.observationId;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public String getPatientNum() {
		return patientNum;
	}

	public String getNamespace() {
		return namespace;
	}

	public Timestamp getStartDate() {
		return startDate;
	}

	public Timestamp getEndDate() {
		return endDate;
	}

	public String getConceptCode() {
		return conceptCode;
	}

	public String getConceptName() {
		return conceptName;
	}

	public Long getObservationId() {
		return observationId;
	}

	public static final class Builder {
		private String patientNum;
		private String namespace;
		private Timestamp startDate;
		private Timestamp endDate;
		private String conceptCode;
		private String conceptName;
		private Long observationId;

		private Builder() {
		}

		public Builder withPatientNum(String patientNum) {
			this.patientNum = patientNum;
			return this;
		}

		public Builder withNamespace(String namespace) {
			this.namespace = namespace;
			return this;
		}

		public Builder withStartDate(Timestamp startDate) {
			this.startDate = startDate;
			return this;
		}

		public Builder withEndDate(Timestamp endDate) {
			this.endDate = endDate;
			return this;
		}

		public Builder withConceptCode(String conceptCode) {
			this.conceptCode = conceptCode;
			return this;
		}

		public Builder withConceptName(String conceptName) {
			this.conceptName = conceptName;
			return this;
		}

		public Builder withObservationId(Long observationId) {
			this.observationId = observationId;
			return this;
		}

		public MedicationRecord build() {
			return new MedicationRecord(this);
		}
	}
}
