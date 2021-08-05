package com.custodix.insite.local.ehr2edc.jackson.deserializers;

import java.io.IOException;

import com.custodix.insite.local.ehr2edc.vocabulary.EventDefinitionId;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class EventDefinitionIdDeserializer extends StdDeserializer<EventDefinitionId> {

	public EventDefinitionIdDeserializer() {
		this(null);
	}

	private EventDefinitionIdDeserializer(Class<?> vc) {
		super(vc);
	}

	@Override
	public EventDefinitionId deserialize(JsonParser jp, DeserializationContext ctx) throws IOException {
		JsonNode node = jp.getCodec()
				.readTree(jp);
		String id = node.asText();
		return EventDefinitionId.of(id);
	}
}
