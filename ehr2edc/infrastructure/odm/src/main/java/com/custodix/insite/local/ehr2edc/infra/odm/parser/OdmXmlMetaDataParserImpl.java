package com.custodix.insite.local.ehr2edc.infra.odm.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.custodix.insite.local.ehr2edc.infra.odm.parser.model.XmlOdm;
import com.custodix.insite.local.ehr2edc.shared.exceptions.SystemException;
import com.custodix.insite.local.ehr2edc.shared.exceptions.UserException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;

@Component
public class OdmXmlMetaDataParserImpl implements OdmXmlMetaDataParser {
	private static final Logger LOGGER = LoggerFactory.getLogger(OdmXmlMetaDataParserImpl.class);

	@Override
	public XmlOdm parseToXmlModel(String xml) {
		JacksonXmlModule module = new JacksonXmlModule();
		module.setDefaultUseWrapper(false);
		ObjectMapper objectMapper = new XmlMapper(module);
		objectMapper.registerModule(new JaxbAnnotationModule());
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		return parse(xml, objectMapper);
	}

	private XmlOdm parse(String xml, ObjectMapper objectMapper) {
		try {
			return objectMapper.readValue(xml, XmlOdm.class);
		} catch (JsonParseException | JsonMappingException e) {
			LOGGER.error("ODM XML is invalid", e);
			throw new UserException("ODM XML is invalid: " + e.getMessage());
		} catch (Exception e) {
			throw new SystemException("Could not parse ODM XML", e);
		}
	}
}
