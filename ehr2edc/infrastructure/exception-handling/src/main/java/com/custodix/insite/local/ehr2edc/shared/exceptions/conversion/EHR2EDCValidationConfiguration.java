package com.custodix.insite.local.ehr2edc.shared.exceptions.conversion;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

@Configuration
@ComponentScan(basePackages = { "com.custodix.insite.local.ehr2edc.shared.exceptions.conversion" })
public class EHR2EDCValidationConfiguration {
	@Bean
	public ReloadableResourceBundleMessageSource ehr2edcMessageSource() {
		final ReloadableResourceBundleMessageSource messages = new ReloadableResourceBundleMessageSource();
		messages.setBasename("classpath:ehr2edc/messages");
		return messages;
	}
}
