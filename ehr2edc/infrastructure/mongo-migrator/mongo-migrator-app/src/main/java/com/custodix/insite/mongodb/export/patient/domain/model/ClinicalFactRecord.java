package com.custodix.insite.mongodb.export.patient.domain.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class ClinicalFactRecord {
	private final Long observationId;
	private final String patientId;
	private final String namespace;
	private final Timestamp startDate;
	private final String label;

	private final BigDecimal value;
	private final String unit;
	private final BigDecimal ulnValue;
	private final BigDecimal llnValue;

	private final String conceptCode;

	private ClinicalFactRecord(Builder builder) {
		observationId = builder.observationId;
		patientId = builder.patientId;
		namespace = builder.namespace;
		startDate = builder.startDate;
		label = builder.label;
		value = builder.value;
		unit = builder.unit;
		ulnValue = builder.ulnValue;
		llnValue = builder.llnValue;
		conceptCode = builder.conceptCode;
	}

	public Long getObservationId() {
		return observationId;
	}

	public String getPatientId() {
		return patientId;
	}

	public String getNamespace() {
		return namespace;
	}

	public Timestamp getStartDate() {
		return startDate;
	}

	public String getLabel() {
		return label;
	}

	public BigDecimal getValue() {
		return value;
	}

	public String getUnit() {
		return unit;
	}

	public BigDecimal getUlnValue() {
		return ulnValue;
	}

	public BigDecimal getLlnValue() {
		return llnValue;
	}

	public String getConceptCode() {
		return conceptCode;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static final class Builder {
		private Long observationId;
		private String patientId;
		private String namespace;
		private Timestamp startDate;
		private String label;
		private BigDecimal value;
		private String unit;
		private BigDecimal ulnValue;
		private BigDecimal llnValue;
		private String conceptCode;

		private Builder() {
		}

		public Builder withObservationId(Long val) {
			observationId = val;
			return this;
		}

		public Builder withPatientId(String val) {
			patientId = val;
			return this;
		}

		public Builder withNamespace(String val) {
			namespace = val;
			return this;
		}

		public Builder withStartDate(Timestamp val) {
			startDate = val;
			return this;
		}

		public Builder withLabel(String val) {
			label = val;
			return this;
		}

		public Builder withValue(BigDecimal val) {
			value = val;
			return this;
		}

		public Builder withUnit(String val) {
			unit = val;
			return this;
		}

		public Builder withUlnValue(BigDecimal val) {
			ulnValue = val;
			return this;
		}

		public Builder withLlnValue(BigDecimal val) {
			llnValue = val;
			return this;
		}

		public Builder withConceptCode(String val) {
			conceptCode = val;
			return this;
		}

		public ClinicalFactRecord build() {
			return new ClinicalFactRecord(this);
		}
	}
}