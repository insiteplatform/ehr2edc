package com.custodix.insite.local.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import eu.ehr4cr.workbench.local.global.CustomLocaleResolver;
import eu.ehr4cr.workbench.local.global.CustomSpringMessageResolver;

@Configuration
public class InternationalizationConfiguration {

	@Bean
	public CustomLocaleResolver localeResolver(
			@Value("${enableInternationalization}") boolean enableInternalization) {
		CustomLocaleResolver customLocaleResolver = new CustomLocaleResolver();
		customLocaleResolver.setResolveLocale(enableInternalization);
		customLocaleResolver.setUsedLocaleResolver(new AcceptHeaderLocaleResolver());
		return customLocaleResolver;
	}

	@Bean
	public CustomSpringMessageResolver messageSource() {
		CustomSpringMessageResolver customSpringMessageResolver = new CustomSpringMessageResolver();
		customSpringMessageResolver.setBasename("classpath:html-locales/messages");
		customSpringMessageResolver.setFallbackToSystemLocale(false);
		customSpringMessageResolver.setDefaultEncoding("UTF-8");
		return customSpringMessageResolver;
	}
}
