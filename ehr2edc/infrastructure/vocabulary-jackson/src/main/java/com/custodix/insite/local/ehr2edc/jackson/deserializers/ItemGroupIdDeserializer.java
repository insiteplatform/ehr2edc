package com.custodix.insite.local.ehr2edc.jackson.deserializers;

import java.io.IOException;

import com.custodix.insite.local.ehr2edc.vocabulary.ItemGroupId;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public final class ItemGroupIdDeserializer extends StdDeserializer<ItemGroupId> {

	public ItemGroupIdDeserializer() {
		this(null);
	}

	private ItemGroupIdDeserializer(Class<?> vc) {
		super(vc);
	}

	@Override
	public ItemGroupId deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException {
		JsonNode node = jsonParser.getCodec()
				.readTree(jsonParser);
		String id = node.asText();
		return ItemGroupId.newBuilder()
				.withId(id)
				.build();
	}
}
