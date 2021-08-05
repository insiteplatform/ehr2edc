package com.custodix.insite.local.ehr2edc.jackson.serializers;

import java.io.IOException;

import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class SubjectIdSerializer extends StdSerializer<SubjectId> {

	public SubjectIdSerializer() {
		this(null);
	}

	private SubjectIdSerializer(Class<SubjectId> t) {
		super(t);
	}

	@Override
	public void serialize(SubjectId value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
		jgen.writeString(value.getId());
	}
}
