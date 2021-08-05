package com.custodix.insite.local.ehr2edc.infra.edc.openclinica.odm.model;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public final class ItemGroupData {

	public static final String REPEAT_KEY = "@CONTEXT";

	@XmlAttribute(name = "ItemGroupOID")
	private String itemGroupOID;
	@XmlAttribute(name = "ItemGroupRepeatKey")
	private String itemGroupRepeatKey;

	@XmlElement(name = "ItemData")
	private List<ItemData> itemData;

	private ItemGroupData() {
		// JAXB
	}

	private ItemGroupData(Builder builder) {
		itemGroupOID = builder.itemGroupOID;
		itemGroupRepeatKey = builder.itemGroupRepeatKey;
		itemData = builder.itemData;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static final class Builder {
		private String itemGroupOID;
		private String itemGroupRepeatKey;
		private List<ItemData> itemData;

		private Builder() {
		}

		public Builder withItemGroupOID(String itemGroupOID) {
			this.itemGroupOID = itemGroupOID;
			return this;
		}

		public Builder withItemGroupRepeatKey(String itemGroupRepeatKey) {
			this.itemGroupRepeatKey = itemGroupRepeatKey;
			return this;
		}

		public Builder withItemData(List<ItemData> itemData) {
			this.itemData = itemData;
			return this;
		}

		public ItemGroupData build() {
			return new ItemGroupData(this);
		}
	}
}