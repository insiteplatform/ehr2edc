package com.custodix.insite.local.ehr2edc.jackson.serializers;

import java.io.IOException;

import com.custodix.insite.local.ehr2edc.vocabulary.ItemDefinitionId;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class ItemDefinitionIdSerializer extends StdSerializer<ItemDefinitionId> {

	public ItemDefinitionIdSerializer() {
		this(null);
	}

	private ItemDefinitionIdSerializer(Class<ItemDefinitionId> t) {
		super(t);
	}

	@Override
	public void serialize(ItemDefinitionId value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
		jgen.writeString(value.getId());
	}
}
