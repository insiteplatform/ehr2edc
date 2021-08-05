package com.custodix.insite.local.ehr2edc.jackson.serializers;

import java.io.IOException;

import com.custodix.insite.local.ehr2edc.vocabulary.ItemId;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class ItemIdSerializer extends StdSerializer<ItemId> {

	public ItemIdSerializer() {
		this(null);
	}

	private ItemIdSerializer(Class<ItemId> t) {
		super(t);
	}

	@Override
	public void serialize(ItemId value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
		jgen.writeString(value.getId());
	}
}
