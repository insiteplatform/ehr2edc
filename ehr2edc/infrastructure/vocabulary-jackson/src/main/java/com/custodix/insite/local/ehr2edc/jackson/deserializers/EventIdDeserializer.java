package com.custodix.insite.local.ehr2edc.jackson.deserializers;

import java.io.IOException;

import com.custodix.insite.local.ehr2edc.vocabulary.EventId;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public final class EventIdDeserializer extends StdDeserializer<EventId> {

	public EventIdDeserializer() {
		this(null);
	}

	private EventIdDeserializer(Class<?> vc) {
		super(vc);
	}

	@Override
	public EventId deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException {
		JsonNode node = jsonParser.getCodec()
				.readTree(jsonParser);
		String id = node.asText();
		return EventId.of(id);
	}
}
