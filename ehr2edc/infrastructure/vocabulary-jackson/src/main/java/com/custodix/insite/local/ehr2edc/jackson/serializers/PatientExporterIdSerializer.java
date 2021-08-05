package com.custodix.insite.local.ehr2edc.jackson.serializers;

import java.io.IOException;

import com.custodix.insite.local.ehr2edc.vocabulary.PatientExporterId;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class PatientExporterIdSerializer extends StdSerializer<PatientExporterId> {

	public PatientExporterIdSerializer() {
		this(null);
	}

	private PatientExporterIdSerializer(Class<PatientExporterId> t) {
		super(t);
	}

	@Override
	public void serialize(PatientExporterId value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
		jgen.writeString(value.getId());
	}
}
