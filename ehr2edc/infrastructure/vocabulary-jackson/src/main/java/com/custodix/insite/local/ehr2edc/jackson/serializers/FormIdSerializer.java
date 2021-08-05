package com.custodix.insite.local.ehr2edc.jackson.serializers;

import java.io.IOException;

import com.custodix.insite.local.ehr2edc.vocabulary.FormId;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class FormIdSerializer extends StdSerializer<FormId> {

	public FormIdSerializer() {
		this(null);
	}

	private FormIdSerializer(Class<FormId> t) {
		super(t);
	}

	@Override
	public void serialize(FormId value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
		jgen.writeString(value.getId());
	}
}
