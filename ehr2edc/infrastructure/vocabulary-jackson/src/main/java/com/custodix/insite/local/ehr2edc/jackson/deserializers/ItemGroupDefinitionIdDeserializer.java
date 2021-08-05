package com.custodix.insite.local.ehr2edc.jackson.deserializers;

import java.io.IOException;

import com.custodix.insite.local.ehr2edc.vocabulary.ItemGroupDefinitionId;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class ItemGroupDefinitionIdDeserializer extends StdDeserializer<ItemGroupDefinitionId> {

	public ItemGroupDefinitionIdDeserializer() {
		this(null);
	}

	private ItemGroupDefinitionIdDeserializer(Class<?> vc) {
		super(vc);
	}

	@Override
	public ItemGroupDefinitionId deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException {
		JsonNode node = jsonParser.getCodec()
				.readTree(jsonParser);
		String id = node.asText();
		return ItemGroupDefinitionId.of(id);
	}
}
