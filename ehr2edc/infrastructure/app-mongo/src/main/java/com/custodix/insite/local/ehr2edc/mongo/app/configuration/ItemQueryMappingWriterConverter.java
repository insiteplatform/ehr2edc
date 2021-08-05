package com.custodix.insite.local.ehr2edc.mongo.app.configuration;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

import com.custodix.insite.local.ehr2edc.querymapping.model.ItemQueryMapping;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@WritingConverter
class ItemQueryMappingWriterConverter implements Converter<ItemQueryMapping, Document> {
	private final ObjectMapper objectMapper;
	private final String dotReplacement;

	ItemQueryMappingWriterConverter(@Qualifier("appMongoObjectMapper") ObjectMapper objectMapper, String dotReplacement) {
		this.objectMapper = objectMapper;
		this.dotReplacement = dotReplacement;
	}

	public Document convert(ItemQueryMapping source) {
		try {
			String sourceString = objectMapper.writeValueAsString(source)
					.replace("\"id\"", "\"_id\"")
					.replace(".", dotReplacement);
			return Document.parse(sourceString);
		} catch (JsonProcessingException e) {
			throw new IllegalArgumentException(e);
		}
	}
}