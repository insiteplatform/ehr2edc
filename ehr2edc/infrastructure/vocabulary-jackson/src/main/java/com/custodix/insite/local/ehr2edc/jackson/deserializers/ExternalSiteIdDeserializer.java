package com.custodix.insite.local.ehr2edc.jackson.deserializers;

import java.io.IOException;

import com.custodix.insite.local.ehr2edc.vocabulary.ExternalSiteId;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class ExternalSiteIdDeserializer extends StdDeserializer<ExternalSiteId> {

	public ExternalSiteIdDeserializer() {
		this(null);
	}

	public ExternalSiteIdDeserializer(Class<?> vc) {
		super(vc);
	}

	@Override
	public ExternalSiteId deserialize(JsonParser jp, DeserializationContext ctx) throws IOException {
		JsonNode node = jp.getCodec()
				.readTree(jp);
		String id = node.asText();
		return ExternalSiteId.of(id);
	}
}
