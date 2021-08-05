package com.custodix.insite.local.ehr2edc.jackson.serializers;

import java.io.IOException;

import com.custodix.insite.local.ehr2edc.vocabulary.EventId;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class EventIdSerializer extends StdSerializer<EventId> {

	public EventIdSerializer() {
		this(null);
	}

	private EventIdSerializer(Class<EventId> t) {
		super(t);
	}

	@Override
	public void serialize(EventId value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
		jgen.writeString(value.getId());
	}
}
