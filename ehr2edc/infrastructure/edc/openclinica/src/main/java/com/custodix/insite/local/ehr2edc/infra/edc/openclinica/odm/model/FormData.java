package com.custodix.insite.local.ehr2edc.infra.edc.openclinica.odm.model;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public final class FormData {
	@XmlAttribute(name = "FormOID")
	private String formOid;
	@XmlElement(name = "ItemGroupData")
	private List<ItemGroupData> itemGroupData;

	private FormData() {
		// JAXB
	}

	private FormData(Builder builder) {
		formOid = builder.formOid;
		itemGroupData = builder.itemGroupData;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static final class Builder {
		private String formOid;
		private List<ItemGroupData> itemGroupData;

		private Builder() {
		}

		public Builder withFormOid(String formOid) {
			this.formOid = formOid;
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
