package com.custodix.insite.local.ehr2edc.shared.exceptions.conversion;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import com.custodix.insite.local.ehr2edc.shared.exceptions.Message;

@Service
class EHR2EDCMessageSourceTranslationService {
	private static final Logger LOGGER = LoggerFactory.getLogger(EHR2EDCMessageSourceTranslationService.class);

	private final MessageSource messageSource;

	EHR2EDCMessageSourceTranslationService(@Qualifier("ehr2edcMessageSource") MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	public String translate(Message message) {
		try {
			return messageSource.getMessage(message.getKey(), message.getParameters(), LocaleContextHolder.getLocale());
		} catch (NoSuchMessageException noMessage){
			LOGGER.error("No message for key", noMessage);
			return message.getKey();
		}
	}
}
