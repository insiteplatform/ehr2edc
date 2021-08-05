package com.custodix.insite.local.ehr2edc.infra.edc.openclinica;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;

class ErrorTranslationService {
	private static final Logger LOGGER = LoggerFactory.getLogger(ErrorTranslationService.class);

	private final MessageSource errors;

	ErrorTranslationService(@Qualifier("openClinicaErrorMessageSource") MessageSource errors) {
		this.errors = errors;
	}

	String translate(String errorCode) {
		try {
			return errors.getMessage(errorCode, new Object[] {}, LocaleContextHolder.getLocale());
		} catch (NoSuchMessageException noMessage) {
			LOGGER.warn(noMessage.getMessage());
			return errorCode;
		}
	}
}
