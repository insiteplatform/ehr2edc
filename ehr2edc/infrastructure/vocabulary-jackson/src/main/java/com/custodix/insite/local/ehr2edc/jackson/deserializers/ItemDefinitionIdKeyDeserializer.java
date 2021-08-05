package com.custodix.insite.local.ehr2edc.jackson.deserializers;

import java.io.IOException;

import com.custodix.insite.local.ehr2edc.vocabulary.ItemDefinitionId;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;

public class ItemDefinitionIdKeyDeserializer extends KeyDeserializer {

	@Override
	public Object deserializeKey(String key, DeserializationContext ctxt) throws IOException {
		return ItemDefinitionId.of(key);
	}
}
