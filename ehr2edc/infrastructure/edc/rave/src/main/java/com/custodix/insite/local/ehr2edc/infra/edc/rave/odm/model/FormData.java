package com.custodix.insite.local.ehr2edc.infra.edc.rave.odm.model;

import static com.custodix.insite.local.ehr2edc.infra.edc.rave.odm.model.Namespaces.DEFAULT;
import static com.custodix.insite.local.ehr2edc.infra.edc.rave.odm.model.Namespaces.MDSOL;

import java.util.List;

import javax.xml.bind.annotation.*;

@XmlRootElement(name = "FormData")
@XmlAccessorType(XmlAccessType.FIELD)
public class FormData {
	@XmlAttribute(name = "FormOID")
	private String formOid;
	@XmlAttribute(name = "TransactionType")
	private TransactionType transactionType;
	@XmlAttribute(name = "LaboratoryRef",
				  namespace = MDSOL)
	private String laboratoryRef;
	@XmlAttribute(name = "LaboratoryType",
				  namespace = MDSOL)
	private LaboratoryType laboratoryType;
	@XmlElement(name = "ItemGroupData",
				namespace = DEFAULT)
	private List<ItemGroupData> itemGroupData;

	private FormData(Builder builder) {
		formOid = builder.formOid;
		transactionType = builder.transactionType;
		itemGroupData = builder.itemGroupData;
		laboratoryRef = builder.laboratoryRef;
		laboratoryType = builder.laboratoryType;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static final class Builder {
		private String formOid;
		private TransactionType transactionType;
		private String laboratoryRef;
		private LaboratoryType laboratoryType;
		private List<ItemGroupData> itemGroupData;

		private Builder() {
		}

		public Builder withFormOid(String formOid) {
			this.formOid = formOid;
			return this;
		}

		public Builder withTransactionType(TransactionType transactionType) {
			this.transactionType = transactionType;
			return this;
		}

		public Builder withLaboratoryRef(String laboratoryRef) {
			this.laboratoryRef = laboratoryRef;
			return this;
		}

		public Builder withLaboratoryType(LaboratoryType laboratoryType) {
			this.laboratoryType = laboratoryType;
			return this;
		}

		public Builder withItemGroupData(List<ItemGroupData> itemGroupData) {
			this.itemGroupData = itemGroupData;
			return this;
		}

		public FormData build() {
			return new FormData(this);
		}
	}
}
