package com.custodix.insite.local.ehr2edc.jackson.deserializers;

import java.io.IOException;

import com.custodix.insite.local.ehr2edc.vocabulary.PatientCDWReference;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class PatientIdDeserializer extends StdDeserializer<PatientCDWReference> {

	public PatientIdDeserializer() {
		this(null);
	}

	public PatientIdDeserializer(Class<?> vc) {
		super(vc);
	}

	@Override
	public PatientCDWReference deserialize(JsonParser jp, DeserializationContext ctx) throws IOException {
		JsonNode node = jp.getCodec()
				.readTree(jp);
		String id = node.get("id")
				.asText()
				.trim();
		String source = node.get("source")
				.asText();
		return PatientCDWReference.newBuilder()
				.withSource(source)
				.withId(id)
				.build();
	}
}