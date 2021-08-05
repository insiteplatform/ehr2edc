package com.custodix.insite.local.ehr2edc.ehr.mongo.command;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpecConfiguration {
	@Bean
	TestEventPublisher eventPublisher() {
		return new TestEventPublisher();
	}
}
