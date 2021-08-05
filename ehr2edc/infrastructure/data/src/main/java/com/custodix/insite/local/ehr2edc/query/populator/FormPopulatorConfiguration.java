package com.custodix.insite.local.ehr2edc.query.populator;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.custodix.insite.local.ehr2edc.domain.service.ItemQueryMappingService;
import com.custodix.insite.local.ehr2edc.query.fhir.FhirEHRGatewayFactory;
import com.custodix.insite.local.ehr2edc.query.mongo.MongoEHRGatewayFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class FormPopulatorConfiguration {

	@Bean
	ItemQueryMappingService itemQueryMappingService() {
		ObjectMapper objectMapper = QueryJacksonConfiguration.createObjectMapper();
		return new ItemQueryMappingServiceImpl(objectMapper);
	}

	@Bean
	QueryMappingFormPopulator formPopulator(ItemQueryMappingService itemQueryMappingService,
			EhrGatewayFactoryFactory ehrGatewayFactoryFactory) {
		return new QueryMappingFormPopulator(new FormPopulatorLogger(), itemQueryMappingService,
				ehrGatewayFactoryFactory);
	}

	@Bean
	EhrGatewayFactoryFactory ehrGatewayFactoryFactory(FhirEHRGatewayFactory fhirEHRGatewayFactory, MongoEHRGatewayFactory mongoEHRGatewayFactory) {
		return new EhrGatewayFactoryFactory(fhirEHRGatewayFactory, mongoEHRGatewayFactory);
	}
}
