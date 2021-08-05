package com.custodix.insite.local.ehr2edc.jackson.serializers;

import java.io.IOException;

import com.custodix.insite.local.ehr2edc.vocabulary.FormDefinitionId;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class FormDefinitionIdSerializer extends StdSerializer<FormDefinitionId> {

	public FormDefinitionIdSerializer() {
		this(null);
	}

	private FormDefinitionIdSerializer(Class<FormDefinitionId> t) {
		super(t);
	}

	@Override
	public void serialize(FormDefinitionId value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
		jgen.writeString(value.getId());
	}
}
