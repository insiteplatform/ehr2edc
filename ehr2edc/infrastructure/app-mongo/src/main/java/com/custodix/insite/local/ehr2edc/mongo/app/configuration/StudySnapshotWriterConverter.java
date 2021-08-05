package com.custodix.insite.local.ehr2edc.mongo.app.configuration;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

import com.custodix.insite.local.ehr2edc.snapshots.StudySnapshot;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@WritingConverter
class StudySnapshotWriterConverter implements Converter<StudySnapshot, Document> {
	private final ObjectMapper objectMapper;

	StudySnapshotWriterConverter(@Qualifier("appMongoObjectMapper") ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	public Document convert(StudySnapshot source) {
		try {
			return Document.parse(objectMapper.writeValueAsString(source)
					.replace("\"id\"", "\"_id\""));
		} catch (JsonProcessingException e) {
			throw new IllegalArgumentException(e);
		}
	}
}