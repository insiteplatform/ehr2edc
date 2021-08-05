package com.custodix.insite.local.ehr2edc.jackson.deserializers;

import java.io.IOException;

import com.custodix.insite.local.ehr2edc.vocabulary.EDCSubjectReference;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class EDCSubjectReferenceDeserializer extends StdDeserializer<EDCSubjectReference> {

	public EDCSubjectReferenceDeserializer() {
		this(null);
	}

	public EDCSubjectReferenceDeserializer(Class<?> vc) {
		super(vc);
	}

	@Override
	public EDCSubjectReference deserialize(JsonParser jp, DeserializationContext ctx) throws IOException {
		JsonNode node = jp.getCodec()
				.readTree(jp);
		String id = node.asText()
				.trim();
		return EDCSubjectReference.of(id);
	}
}