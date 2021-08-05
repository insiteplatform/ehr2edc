package com.custodix.insite.local.ehr2edc.jackson.serializers;

import java.io.IOException;

import com.custodix.insite.local.ehr2edc.vocabulary.ExternalSiteId;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class ExternalSiteIdSerializer extends StdSerializer<ExternalSiteId> {

	public ExternalSiteIdSerializer() {
		this(null);
	}

	private ExternalSiteIdSerializer(Class<ExternalSiteId> t) {
		super(t);
	}

	@Override
	public void serialize(ExternalSiteId value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
		jgen.writeString(value.getId());
	}
}