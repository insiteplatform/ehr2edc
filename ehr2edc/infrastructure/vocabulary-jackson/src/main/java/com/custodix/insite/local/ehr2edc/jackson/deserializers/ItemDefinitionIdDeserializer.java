package com.custodix.insite.local.ehr2edc.jackson.deserializers;

import java.io.IOException;

import com.custodix.insite.local.ehr2edc.vocabulary.ItemDefinitionId;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class ItemDefinitionIdDeserializer extends StdDeserializer<ItemDefinitionId> {

	public ItemDefinitionIdDeserializer() {
		this(null);
	}

	private ItemDefinitionIdDeserializer(Class<?> vc) {
		super(vc);
	}

	@Override
	public ItemDefinitionId deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException {
		JsonNode node = jsonParser.getCodec()
				.readTree(jsonParser);
		String id = node.asText();
		return ItemDefinitionId.of(id);
	}


}
