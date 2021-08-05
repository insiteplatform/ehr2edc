package com.custodix.insite.local.ehr2edc.mongo.app.jackson;

import org.springframework.stereotype.Service;

import com.custodix.insite.local.ehr2edc.domain.service.ObjectToStringMappingService;
import com.custodix.insite.local.ehr2edc.jackson.VocabularyModule;
import com.custodix.insite.local.ehr2edc.shared.exceptions.SystemException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ObjectToMongoJsonMappingService implements ObjectToStringMappingService {

	private final ObjectMapper objectMapper;

	public ObjectToMongoJsonMappingService() {
		this.objectMapper = AppMongoJacksonConfiguration.createObjectMapper();
		objectMapper.registerModule(VocabularyModule.create());
	}

	@Override
	public String map(Object o) {
		try {
			return objectMapper.writeValueAsString(o);
		} catch (JsonProcessingException e) {
			throw new SystemException("Could not serialize object to json", e);
		}
	}
}
