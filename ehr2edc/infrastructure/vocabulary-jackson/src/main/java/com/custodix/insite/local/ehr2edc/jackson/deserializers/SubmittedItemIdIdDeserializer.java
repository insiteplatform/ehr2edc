package com.custodix.insite.local.ehr2edc.jackson.deserializers;

import com.custodix.insite.local.ehr2edc.vocabulary.SubmittedItemId;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

public final class SubmittedItemIdIdDeserializer extends StdDeserializer<SubmittedItemId> {
	public SubmittedItemIdIdDeserializer() {
		this(null);
	}

	private SubmittedItemIdIdDeserializer(Class<?> vc) {
		super(vc);
	}

	@Override
	public SubmittedItemId deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException {
		JsonNode node = jsonParser.getCodec()
				.readTree(jsonParser);
		String id = node.asText();
		return SubmittedItemId.of(id);
	}
}
