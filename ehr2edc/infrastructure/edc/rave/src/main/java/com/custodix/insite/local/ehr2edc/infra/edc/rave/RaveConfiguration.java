package com.custodix.insite.local.ehr2edc.infra.edc.rave;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;

import org.springframework.context.annotation.Bean;

import com.custodix.insite.local.ehr2edc.infra.edc.rave.model.response.ResponseConverter;
import com.custodix.insite.local.ehr2edc.infra.edc.rave.odm.RaveODMSerializer;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;

public class RaveConfiguration {

	@Bean
	RaveEDCStudyGateway raveEDCStudyGateway(RaveODMSerializer odmSerializer,
			SubmitReviewedEventErrorResponseHandler submitReviewedEventErrorResponseHandler,
			RaveRestClient raveRestClient) {
		return new RaveEDCStudyGateway(odmSerializer, submitReviewedEventErrorResponseHandler, raveRestClient);
	}

	@Bean
	RaveLabAnalyteRangesGateway raveLabAnalyteRangesGateway(RaveRestClient raveRestClient) {
		return new RaveLabAnalyteRangesGateway(raveRestClient);
	}

	@Bean
	SubmitReviewedEventErrorResponseHandler submitReviewedEventErrorResponseHandler() {
		return new SubmitReviewedEventErrorResponseHandler(new ResponseConverter(createXmlObjectMapper()));
	}

	@Bean
	RaveRestClient raveRestClient() {
		return new RaveRestClient();
	}

	@Bean
	RaveODMSerializer raveODMSerializer() {
		return new RaveODMSerializer(createXmlObjectMapper());
	}

	private ObjectMapper createXmlObjectMapper() {
		JacksonXmlModule module = new JacksonXmlModule();
		module.setDefaultUseWrapper(false);
		ObjectMapper mapper = new XmlMapper(module);
		mapper.registerModule(new JaxbAnnotationModule());
		mapper.configure(FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
		return mapper;
	}
}
