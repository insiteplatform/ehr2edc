package com.custodix.insite.mongodb.export.patient.domain.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class LaboratoryObservationFactRecord {
	private final String patientNum;
	private final String patientMasterIndex;
	private final Timestamp endDate;
	private final Timestamp startDate;
	private final String conceptCD;
	private final String conceptLabel;
	private final BigDecimal value;
	private final String unit;
	private final BigDecimal ulnValue;
	private final BigDecimal llnValue;
	private final String vendor;

	private LaboratoryObservationFactRecord(Builder builder) {
		patientNum = builder.patientNum;
		patientMasterIndex = builder.patientMasterIndex;
		endDate = builder.endDate;
		startDate = builder.startDate;
		conceptCD = builder.conceptCD;
		conceptLabel = builder.conceptLabel;
		value = builder.value;
		unit = builder.unit;
		ulnValue = builder.ulnValue;
		llnValue = builder.llnValue;
		vendor = builder.vendor;
	}

	public String getPatientNum() {
		return patientNum;
	}

	public String getPatientMasterIndex() {
		return patientMasterIndex;
	}

	public Timestamp getEndDate() {
		return endDate;
	}

	public Timestamp getStartDate() {
		return startDate;
	}

	public String getConceptCD() {
		return conceptCD;
	}

	public String getConceptLabel() {
		return conceptLabel;
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

	public String getVendor() {
		return vendor;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static final class Builder {
		private String patientNum;
		private String patientMasterIndex;
		private Timestamp endDate;
		private Timestamp startDate;
		private String conceptCD;
		private String conceptLabel;
		private BigDecimal value;
		private String unit;
		private BigDecimal ulnValue;
		private BigDecimal llnValue;
		private String vendor;

		private Builder() {
		}

		public Builder withPatientNum(String val) {
			patientNum = val;
			return this;
		}

		public Builder withPatientMasterIndex(String val) {
			patientMasterIndex = val;
			return this;
		}

		public Builder withEndDate(Timestamp val) {
			endDate = val;
			return this;
		}

		public Builder withStartDate(Timestamp val) {
			startDate = val;
			return this;
		}

		public Builder withConceptCD(String val) {
			conceptCD = val;
			return this;
		}

		public Builder withConceptLabel(String val) {
			conceptLabel = val;
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

		public Builder withVendor(String val) {
			vendor = val;
			return this;
		}

		public LaboratoryObservationFactRecord build() {
			return new LaboratoryObservationFactRecord(this);
		}
	}
}