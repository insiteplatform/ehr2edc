package com.custodix.insite.local.user.infra.validation;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import com.custodix.insite.local.user.vocabulary.validation.Message;
import com.custodix.insite.local.user.vocabulary.validation.TranslationService;

@Service
class MessageSourceTranslationService implements TranslationService {
	private static final Logger LOGGER = LoggerFactory.getLogger(MessageSourceTranslationService.class);

	private final MessageSource messageSource;
	private final boolean internationalizationEnabled;

	MessageSourceTranslationService(MessageSource messageSource,
			@Value("${enableInternationalization}") boolean internationalizationEnabled) {
		this.messageSource = messageSource;
		this.internationalizationEnabled = internationalizationEnabled;
	}

	@Override
	public String translate(Message message) {
		try {
			return messageSource.getMessage(message.getKey(), message.getParameters(), getLocale());
		} catch (NoSuchMessageException noMessage) {
			LOGGER.warn("No key for message: {}", noMessage);
			return message.getKey();
		}
	}

	private Locale getLocale() {
		if (internationalizationEnabled) {
			return LocaleContextHolder.getLocale();
		} else {
			return Locale.getDefault();
		}
	}
}
