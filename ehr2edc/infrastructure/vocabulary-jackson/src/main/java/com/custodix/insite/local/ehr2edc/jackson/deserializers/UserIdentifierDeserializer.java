package com.custodix.insite.local.ehr2edc.jackson.deserializers;

import java.io.IOException;

import com.custodix.insite.local.ehr2edc.vocabulary.UserIdentifier;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class UserIdentifierDeserializer extends StdDeserializer<UserIdentifier> {

	public UserIdentifierDeserializer() {
		this(null);
	}

	public UserIdentifierDeserializer(Class<?> vc) {
		super(vc);
	}

	@Override
	public UserIdentifier deserialize(JsonParser jp, DeserializationContext ctx) throws IOException {
		JsonNode node = jp.getCodec()
				.readTree(jp);
		String id = node.asText();
		return UserIdentifier.of(id);
	}
}
