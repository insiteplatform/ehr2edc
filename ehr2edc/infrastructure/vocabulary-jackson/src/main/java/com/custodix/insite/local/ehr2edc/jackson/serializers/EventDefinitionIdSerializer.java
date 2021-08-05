package com.custodix.insite.local.ehr2edc.jackson.serializers;

import java.io.IOException;

import com.custodix.insite.local.ehr2edc.vocabulary.EventDefinitionId;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class EventDefinitionIdSerializer extends StdSerializer<EventDefinitionId> {

	public EventDefinitionIdSerializer() {
		this(null);
	}

	private EventDefinitionIdSerializer(Class<EventDefinitionId> t) {
		super(t);
	}

	@Override
	public void serialize(EventDefinitionId value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
		jgen.writeString(value.getId());
	}
}
