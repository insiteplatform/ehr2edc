package com.custodix.insite.local.ehr2edc.mongo.app.querymapping;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;

import com.custodix.insite.local.ehr2edc.querymapping.model.ItemQueryMapping;

public class PersistedItemQueryMapping {
	@Id
	private String itemId;
	private ItemQueryMapping itemQueryMapping;

	@PersistenceConstructor
	public PersistedItemQueryMapping(String itemId, ItemQueryMapping itemQueryMapping) {
		this.itemId = itemId;
		this.itemQueryMapping = itemQueryMapping;
	}

	public String getItemId() {
		return itemId;
	}

	public ItemQueryMapping getItemQueryMapping() {
		return itemQueryMapping;
	}
}
