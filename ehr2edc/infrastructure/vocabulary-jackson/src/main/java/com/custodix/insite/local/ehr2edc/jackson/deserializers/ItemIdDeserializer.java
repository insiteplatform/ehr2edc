package com.custodix.insite.local.ehr2edc.jackson.deserializers;

import java.io.IOException;

import com.custodix.insite.local.ehr2edc.vocabulary.ItemId;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public final class ItemIdDeserializer extends StdDeserializer<ItemId> {

	public ItemIdDeserializer() {
		this(null);
	}

	private ItemIdDeserializer(Class<?> vc) {
		super(vc);
	}

	@Override
	public ItemId deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException {
		JsonNode node = jsonParser.getCodec()
				.readTree(jsonParser);
		String id = node.asText();
		return ItemId.newBuilder()
				.withId(id)
				.build();
	}
}
