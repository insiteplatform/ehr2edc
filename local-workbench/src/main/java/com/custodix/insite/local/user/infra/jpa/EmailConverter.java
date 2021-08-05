package com.custodix.insite.local.user.infra.jpa;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.custodix.insite.local.user.vocabulary.Email;

@Converter
public class EmailConverter implements AttributeConverter<Email, String> {
	@Override
	public String convertToDatabaseColumn(final Email email) {
		return email != null ? email.getValue() : null;
	}

	@Override
	public Email convertToEntityAttribute(final String value) {
		return Email.of(value);
	}
}
