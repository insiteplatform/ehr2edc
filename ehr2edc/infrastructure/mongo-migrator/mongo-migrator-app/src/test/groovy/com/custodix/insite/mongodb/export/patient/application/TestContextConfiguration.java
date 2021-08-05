package com.custodix.insite.mongodb.export.patient.application;

import java.util.Locale;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

@TestConfiguration
public class TestContextConfiguration {
	static {
		Locale english = Locale.ENGLISH;
		Locale.setDefault(english);
	}

	@Bean
	public MethodValidationPostProcessor bean() {
		return new MethodValidationPostProcessor();
	}
}
