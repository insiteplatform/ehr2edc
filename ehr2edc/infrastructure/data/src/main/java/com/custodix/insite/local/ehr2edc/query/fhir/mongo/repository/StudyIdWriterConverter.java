package com.custodix.insite.local.ehr2edc.query.fhir.mongo.repository;

import org.springframework.core.convert.converter.Converter;

import com.custodix.insite.local.ehr2edc.shared.exceptions.DomainException;
import com.custodix.insite.local.ehr2edc.vocabulary.StudyId;

public class StudyIdWriterConverter implements Converter<StudyId, String> {

	@Override
	public String convert(final StudyId studyId) {
		if (studyId == null) {
			throw new DomainException("Cannot convert a null study id.");
		}
		return studyId.getId();
	}
}
