package com.custodix.insite.local.ehr2edc.jackson.deserializers;

import com.custodix.insite.local.ehr2edc.vocabulary.SubmittedEventId;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

public final class SubmittedEventIdDeserializer extends StdDeserializer<SubmittedEventId> {

	public SubmittedEventIdDeserializer() {
		this(null);
	}

	private SubmittedEventIdDeserializer(Class<?> vc) {
		super(vc);
	}

	@Override
	public SubmittedEventId deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException {
		JsonNode node = jsonParser.getCodec()
				.readTree(jsonParser);
		String id = node.asText();
		return SubmittedEventId.newBuilder()
				.withId(id)
				.build();
	}
}
