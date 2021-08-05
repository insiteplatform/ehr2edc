package com.custodix.insite.local.ehr2edc.jackson.serializers;

import java.io.IOException;

import com.custodix.insite.local.ehr2edc.vocabulary.UserIdentifier;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class UserIdentifierSerializer extends StdSerializer<UserIdentifier> {

	public UserIdentifierSerializer() {
		this(null);
	}

	private UserIdentifierSerializer(Class<UserIdentifier> t) {
		super(t);
	}

	@Override
	public void serialize(UserIdentifier value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
		jgen.writeString(value.getId());
	}
}
