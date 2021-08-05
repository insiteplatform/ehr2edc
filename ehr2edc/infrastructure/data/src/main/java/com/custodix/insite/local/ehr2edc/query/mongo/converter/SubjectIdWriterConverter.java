package com.custodix.insite.local.ehr2edc.query.mongo.converter;

import org.springframework.core.convert.converter.Converter;

import com.custodix.insite.local.ehr2edc.shared.exceptions.DomainException;
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId;

public class SubjectIdWriterConverter implements Converter<SubjectId, String> {

	@Override
	public String convert(final SubjectId subjectId) {
		if (subjectId == null) {
			throw new DomainException("Cannot convert a null subject id.");
		}
		return subjectId.getId();
	}
}
