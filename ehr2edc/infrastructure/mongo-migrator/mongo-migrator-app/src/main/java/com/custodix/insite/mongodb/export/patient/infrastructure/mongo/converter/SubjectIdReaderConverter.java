package com.custodix.insite.mongodb.export.patient.infrastructure.mongo.converter;

import static org.apache.commons.lang3.StringUtils.isBlank;

import org.springframework.core.convert.converter.Converter;

import com.custodix.insite.mongodb.export.patient.domain.exceptions.DomainException;
import com.custodix.insite.mongodb.vocabulary.SubjectId;

public class SubjectIdReaderConverter implements Converter<String, SubjectId> {

	@Override
	public SubjectId convert(final String subjectId) {
		if(isBlank(subjectId)) {
			throw new DomainException("Cannot convert a null or blank subject id value.");
		}
		return SubjectId.of(subjectId);
	}
}
