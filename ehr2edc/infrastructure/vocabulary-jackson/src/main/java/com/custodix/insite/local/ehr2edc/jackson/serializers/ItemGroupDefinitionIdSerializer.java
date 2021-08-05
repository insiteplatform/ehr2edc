package com.custodix.insite.local.ehr2edc.jackson.serializers;

import java.io.IOException;

import com.custodix.insite.local.ehr2edc.vocabulary.ItemGroupDefinitionId;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class ItemGroupDefinitionIdSerializer extends StdSerializer<ItemGroupDefinitionId> {

	public ItemGroupDefinitionIdSerializer() {
		this(null);
	}

	private ItemGroupDefinitionIdSerializer(Class<ItemGroupDefinitionId> t) {
		super(t);
	}

	@Override
	public void serialize(ItemGroupDefinitionId value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
		jgen.writeString(value.getId());
	}
}
