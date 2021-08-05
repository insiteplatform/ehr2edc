package com.custodix.insite.local.ehr2edc.jackson.serializers;

import com.custodix.insite.local.ehr2edc.vocabulary.SubmittedEventId;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class SubmittedEventIdSerializer extends StdSerializer<SubmittedEventId> {

	public SubmittedEventIdSerializer() {
		this(null);
	}

	private SubmittedEventIdSerializer(Class<SubmittedEventId> t) {
		super(t);
	}

	@Override
	public void serialize(SubmittedEventId value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
		jgen.writeString(value.getId());
	}
}
