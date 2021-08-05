package com.custodix.insite.local.ehr2edc.rest.jackson;

import org.springframework.context.annotation.Configuration;

import com.custodix.insite.local.ehr2edc.jackson.ProjectionModule;
import com.custodix.insite.local.ehr2edc.jackson.QueryModule;
import com.custodix.insite.local.ehr2edc.jackson.VocabularyModule;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Configuration
public class JacksonWebConfiguration {
	JacksonWebConfiguration(ObjectMapper objectMapper) {
		objectMapper.registerModule(QueryModule.create());
		objectMapper.registerModule(ProjectionModule.create());
		objectMapper.registerModule(VocabularyModule.create());
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
	}
}