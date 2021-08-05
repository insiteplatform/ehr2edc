package com.custodix.insite.local.ehr2edc.jackson.serializers;

import java.io.IOException;

import com.custodix.insite.local.ehr2edc.vocabulary.EDCSubjectReference;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class EDCSubjectReferenceSerializer extends StdSerializer<EDCSubjectReference> {

	public EDCSubjectReferenceSerializer() {
		this(null);
	}

	private EDCSubjectReferenceSerializer(Class<EDCSubjectReference> t) {
		super(t);
	}

	@Override
	public void serialize(EDCSubjectReference value, JsonGenerator jgen, SerializerProvider provider)
			throws IOException {
		jgen.writeString(value.getId());
	}
}
