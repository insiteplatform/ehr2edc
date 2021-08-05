package com.custodix.insite.local.ehr2edc.infra.edc.openclinica.odm.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public final class ItemData {

	@XmlAttribute(name = "ItemOID")
	private String itemOID;
	@XmlAttribute(name = "Value")
	private String value;
	@XmlElement(name = "MeasurementUnitRef")
	private MeasurementUnitRef measurementUnitRef;
	@XmlElement(name = "AuditRecord")
	private AuditRecord auditRecord;

	private ItemData() {
		// JAXB
	}

	private ItemData(Builder builder) {
		itemOID = builder.itemOID;
		value = builder.value;
		measurementUnitRef = builder.measurementUnitRef;
		auditRecord = builder.auditRecord;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static final class Builder {
		private String itemOID;
		private String value;
		private MeasurementUnitRef measurementUnitRef;
		private AuditRecord auditRecord;

		private Builder() {
		}

		public Builder withItemOID(String itemOID) {
			this.itemOID = itemOID;
			return this;
		}

		public Builder withValue(String value) {
			this.value = value;
			return this;
		}

		public Builder withMeasurementUnitRef(MeasurementUnitRef measurementUnitRef) {
			this.measurementUnitRef = measurementUnitRef;
			return this;
		}

		public Builder withAuditRecord(AuditRecord auditRecord) {
			this.auditRecord = auditRecord;
			return this;
		}

		public ItemData build() {
			return new ItemData(this);
		}
	}
}
