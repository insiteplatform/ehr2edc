package com.custodix.insite.local.ehr2edc.query.fhir.mongo.repository;

import static org.apache.commons.lang3.StringUtils.isBlank;

import org.springframework.core.convert.converter.Converter;

import com.custodix.insite.local.ehr2edc.shared.exceptions.DomainException;
import com.custodix.insite.local.ehr2edc.vocabulary.StudyId;

public class StudyIdReaderConverter implements Converter<String, StudyId> {

	@Override
	public StudyId convert(final String studyId) {
		if (isBlank(studyId)) {
			throw new DomainException("Cannot convert a null or blank study id value.");
		}
		return StudyId.of(studyId);
	}
}
