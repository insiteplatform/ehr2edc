package com.custodix.insite.local.ehr2edc.mongo.app.configuration;

import java.io.IOException;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

import com.custodix.insite.local.ehr2edc.snapshots.StudySnapshot;
import com.fasterxml.jackson.databind.ObjectMapper;

@ReadingConverter
class StudySnapshotReadConverter implements Converter<Document, StudySnapshot> {

	private final ObjectMapper objectMapper;

	StudySnapshotReadConverter(@Qualifier("appMongoObjectMapper") ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	public StudySnapshot convert(Document source) {
		try {

			return objectMapper.readValue(source.toJson()
					.replace("\"_id\"", "\"id\""), StudySnapshot.class);
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}
}