package com.custodix.insite.local.ehr2edc.mongo.app.configuration;

import java.io.IOException;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

import com.custodix.insite.local.ehr2edc.querymapping.model.ItemQueryMapping;
import com.fasterxml.jackson.databind.ObjectMapper;

@ReadingConverter
class ItemQueryMappingReadConverter implements Converter<Document, ItemQueryMapping> {

	private final ObjectMapper objectMapper;
	private final String dotReplacement;

	ItemQueryMappingReadConverter(@Qualifier("appMongoObjectMapper") ObjectMapper objectMapper, String dotReplacement) {
		this.objectMapper = objectMapper;
		this.dotReplacement = dotReplacement;
	}

	public ItemQueryMapping convert(Document source) {
		try {
			String sourceString = source.toJson()
					.replace("\"_id\"", "\"id\"")
					.replace(dotReplacement, ".");
			return objectMapper.readValue(sourceString, ItemQueryMapping.class);
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}
}