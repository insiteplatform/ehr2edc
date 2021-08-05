package com.custodix.insite.local.ehr2edc.infra.edc.rave.odm;

import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;

import com.custodix.insite.local.ehr2edc.infra.edc.rave.odm.model.ODM;
import com.custodix.insite.local.ehr2edc.shared.exceptions.SystemException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class RaveODMSerializer {

	private static final Logger LOGGER = getLogger(RaveODMSerializer.class);

	private final ObjectMapper objectMapper;

	public RaveODMSerializer(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	public ODM deserialize(String odmXML) {
		try {
			return objectMapper.readValue(odmXML, ODM.class);
		} catch (Exception e) {
			LOGGER.warn("Could not parse result from external EDC: {}", odmXML);
		}
		return ODM.newBuilder()
				.build();
	}

	public String serialize(ODM odm) {
		try {
			String serialized = objectMapper.writeValueAsString(odm);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Generated odm xml: {}", serialized);
			}
			return serialized;
		} catch (JsonProcessingException e) {
			throw new SystemException("Unable to serialize ODM.", e);
		}
	}
}
