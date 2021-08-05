package com.custodix.insite.local.ehr2edc.jackson.deserializers;

import java.io.IOException;

import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class SubjectIdDeserializer extends StdDeserializer<SubjectId> {

	public SubjectIdDeserializer() {
		this(null);
	}

	public SubjectIdDeserializer(Class<?> vc) {
		super(vc);
	}

	@Override
	public SubjectId deserialize(JsonParser jp, DeserializationContext ctx) throws IOException {
		JsonNode node = jp.getCodec()
				.readTree(jp);
		String id = node.asText()
				.trim();
		return SubjectId.of(id);
	}
}