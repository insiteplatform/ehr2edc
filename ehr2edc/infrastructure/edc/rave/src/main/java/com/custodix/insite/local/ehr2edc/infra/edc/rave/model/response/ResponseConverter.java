package com.custodix.insite.local.ehr2edc.infra.edc.rave.model.response;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ResponseConverter {

	private final ObjectMapper objectMapper;

	public ResponseConverter(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	public <T> T deserialize(String xml, Class<T> clazz) throws IOException {
		return objectMapper.readValue(xml, clazz);
	}

}
