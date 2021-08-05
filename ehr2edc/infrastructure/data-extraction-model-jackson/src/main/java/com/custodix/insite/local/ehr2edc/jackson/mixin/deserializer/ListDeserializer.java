package com.custodix.insite.local.ehr2edc.jackson.mixin.deserializer;

import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;

public class ListDeserializer extends KeyDeserializer {
	@Override
	public List<String> deserializeKey(String key, DeserializationContext ctxt) {
		String keyWithoutWhitespace = key.replaceAll("\\s+", "");
		String keyWithoutArrayBrackets = keyWithoutWhitespace.substring(1, keyWithoutWhitespace.length() - 1);
		return Arrays.asList(keyWithoutArrayBrackets.split(","));
	}
}
