package com.custodix.insite.local.ehr2edc.jackson.deserializers;

import java.io.IOException;

import com.custodix.insite.local.ehr2edc.vocabulary.StudyId;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class StudyIdDeserializer extends StdDeserializer<StudyId> {

	public StudyIdDeserializer() {
		this(null);
	}

	public StudyIdDeserializer(Class<?> vc) {
		super(vc);
	}

	@Override
	public StudyId deserialize(JsonParser jp, DeserializationContext ctx) throws IOException {
		JsonNode node = jp.getCodec()
				.readTree(jp);
		String id = node.asText();
		return StudyId.of(id);
	}
}
