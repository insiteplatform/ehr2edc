package com.custodix.insite.local.ehr2edc.infra.edc.openclinica.odm.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.FIELD)
public final class MeasurementUnitRef {
	@XmlAttribute(name = "MeasurementUnitOID")
	private String measurementUnitOID;

	private MeasurementUnitRef() {
		// JAXB
	}

	private MeasurementUnitRef(Builder builder) {
		measurementUnitOID = builder.measurementUnitOID;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static final class Builder {
		private String measurementUnitOID;

		private Builder() {
		}

		public Builder withMeasurementUnitOID(String measurementUnitOID) {
			this.measurementUnitOID = measurementUnitOID;
			return this;
		}

		public MeasurementUnitRef build() {
			return new MeasurementUnitRef(this);
		}
	}
}
