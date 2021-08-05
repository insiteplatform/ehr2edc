package com.custodix.insite.local.ehr2edc.jackson.deserializers;

import java.io.IOException;

import com.custodix.insite.local.ehr2edc.vocabulary.FormDefinitionId;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class FormDefinitionIdDeserializer extends StdDeserializer<FormDefinitionId> {

	public FormDefinitionIdDeserializer() {
		this(null);
	}

	private FormDefinitionIdDeserializer(Class<?> vc) {
		super(vc);
	}

	@Override
	public FormDefinitionId deserialize(JsonParser jp, DeserializationContext ctx) throws IOException {
		JsonNode node = jp.getCodec()
				.readTree(jp);
		String id = node.asText();
		return FormDefinitionId.of(id);
	}
}
