package com.custodix.insite.local.ehr2edc.infra.edc.rave.odm.model;

import static com.custodix.insite.local.ehr2edc.infra.edc.rave.odm.model.Namespaces.DEFAULT;

import javax.xml.bind.annotation.*;

@XmlRootElement(name = "ItemData")
@XmlAccessorType(XmlAccessType.FIELD)
public class ItemData {

	@XmlAttribute(name = "ItemOID")
	private String itemOID;
	@XmlAttribute(name = "Value")
	private String value;
	@XmlAttribute(name = "TransactionType")
	private TransactionType transactionType;
	@XmlElement(name = "MeasurementUnitRef",
				namespace = DEFAULT)
	private MeasurementUnitRef measurementUnitRef;
	@XmlElement(name = "AuditRecord",
				namespace = DEFAULT)
	private AuditRecord auditRecord;

	private ItemData(Builder builder) {
		itemOID = builder.itemOID;
		value = builder.value;
		transactionType = builder.transactionType;
		measurementUnitRef = builder.measurementUnitRef;
		auditRecord = builder.auditRecord;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static final class Builder {
		private String itemOID;
		private String value;
		private TransactionType transactionType;
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

		public Builder withTransactionType(TransactionType transactionType) {
			this.transactionType = transactionType;
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
