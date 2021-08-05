package com.custodix.insite.local.ehr2edc.infra.edc.rave.odm.model;

import static com.custodix.insite.local.ehr2edc.infra.edc.rave.odm.model.Namespaces.DEFAULT;

import java.util.List;

import javax.xml.bind.annotation.*;

@XmlRootElement(name = "ItemGroupData")
@XmlAccessorType(XmlAccessType.FIELD)
public class ItemGroupData {

	public static final String REPEAT_KEY = "@CONTEXT";
	public static final TransactionType REPEAT_TRANSACTION_TYPE = TransactionType.UPSERT;
	public static final TransactionType INDEX_TRANSACTION_TYPE = TransactionType.UPSERT;

	@XmlAttribute(name = "ItemGroupOID")
	private String itemGroupOID;
	@XmlAttribute(name = "ItemGroupRepeatKey")
	private String itemGroupRepeatKey;
	@XmlAttribute(name = "TransactionType")
	private TransactionType transactionType;

	@XmlElement(name = "ItemData",
				namespace = DEFAULT)
	private List<ItemData> itemData;

	private ItemGroupData(Builder builder) {
		itemGroupOID = builder.itemGroupOID;
		itemGroupRepeatKey = builder.itemGroupRepeatKey;
		transactionType = builder.transactionType;
		itemData = builder.itemData;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static final class Builder {
		private String itemGroupOID;
		private String itemGroupRepeatKey;
		private TransactionType transactionType;
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

		public Builder withTransactionType(TransactionType transactionType) {
			this.transactionType = transactionType;
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