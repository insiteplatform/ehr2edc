package com.custodix.insite.local.ehr2edc.jackson.serializers;

import java.io.IOException;

import com.custodix.insite.local.ehr2edc.vocabulary.ItemGroupId;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class ItemGroupIdSerializer extends StdSerializer<ItemGroupId> {

	public ItemGroupIdSerializer() {
		this(null);
	}

	private ItemGroupIdSerializer(Class<ItemGroupId> t) {
		super(t);
	}

	@Override
	public void serialize(ItemGroupId value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
		jgen.writeString(value.getId());
	}
}
