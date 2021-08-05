package com.custodix.insite.local.ehr2edc.jackson.deserializers;

import java.io.IOException;

import com.custodix.insite.local.ehr2edc.vocabulary.FormId;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public final class FormIdDeserializer extends StdDeserializer<FormId> {

	public FormIdDeserializer() {
		this(null);
	}

	private FormIdDeserializer(Class<?> vc) {
		super(vc);
	}

	@Override
	public FormId deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException {
		JsonNode node = jsonParser.getCodec()
				.readTree(jsonParser);
		String id = node.asText();
		return FormId.newBuilder()
				.withId(id)
				.build();
	}
}
