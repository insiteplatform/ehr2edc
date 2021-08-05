package com.custodix.insite.local.ehr2edc.jackson.serializers;

import java.io.IOException;

import com.custodix.insite.local.ehr2edc.vocabulary.StudyId;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class StudyIdSerializer extends StdSerializer<StudyId> {

	public StudyIdSerializer() {
		this(null);
	}

	private StudyIdSerializer(Class<StudyId> t) {
		super(t);
	}

	@Override
	public void serialize(StudyId value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
		jgen.writeString(value.getId());
	}
}
