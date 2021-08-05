package com.custodix.insite.local.ehr2edc.config;

import org.springframework.boot.actuate.autoconfigure.mongo.MongoHealthIndicatorAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.context.annotation.*;

import com.custodix.insite.local.ehr2edc.mongo.app.configuration.EHR2EDCMongoDBAppConfiguration;
import com.custodix.insite.local.ehr2edc.query.fhir.mongo.configuration.Ehr2EdcFhirDbConfiguration;

@Configuration
@ComponentScan(basePackages = { "com.custodix.insite.local.ehr2edc" },
			   excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
													  value = EHR2EDCConfig.class))
@ImportAutoConfiguration({ MongoHealthIndicatorAutoConfiguration.class,
						   MongoRepositoriesAutoConfiguration.class,
						   ValidationAutoConfiguration.class,
						   EHR2EDCMongoDBAppConfiguration.class,
						   Ehr2EdcFhirDbConfiguration.class,
						   JacksonAutoConfiguration.class })

@PropertySources({ @PropertySource("classpath:ehr2edc.properties"),
				   @PropertySource("${ehr2edc.config.file}"),
				   @PropertySource("classpath:ehr2edc-infra-mongo-query.properties"),
				   @PropertySource("${ehr2edc-infra-mongo-query.config.file}"),
				   @PropertySource("classpath:ehr2edc-infra-mongo-fhir.properties"),
				   @PropertySource("${ehr2edc-infra-mongo-fhir.config.file}"),
				   @PropertySource("classpath:ehr2edc-infra-app-mongo.properties"),
				   @PropertySource("${ehr2edc-infra-app-mongo.config.file}"),
				   @PropertySource("classpath:ehr2edc-infra-mongo-migrator.properties"),
				   @PropertySource("${ehr2edc-infra-mongo-migrator.config.file}"),
				   @PropertySource("classpath:ehr2edc-infra-web.properties"),
				   @PropertySource("${ehr2edc-infra-web.config.file}") })
public class EHR2EDCAppConfig {
}
