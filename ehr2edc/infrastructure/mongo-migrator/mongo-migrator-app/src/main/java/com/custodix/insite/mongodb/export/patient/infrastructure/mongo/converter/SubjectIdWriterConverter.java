package com.custodix.insite.mongodb.export.patient.infrastructure.mongo.converter;

import org.springframework.core.convert.converter.Converter;

import com.custodix.insite.mongodb.export.patient.domain.exceptions.DomainException;
import com.custodix.insite.mongodb.vocabulary.SubjectId;

public class SubjectIdWriterConverter implements Converter<SubjectId, String> {

	@Override
	public String convert(final SubjectId subjectId) {
		if(subjectId == null) {
			throw new DomainException("Cannot convert a null subject id.");
		}
		return subjectId.getId();
	}
}
