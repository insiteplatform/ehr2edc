package com.custodix.insite.local.ehr2edc.query.mongo.converter;

import static org.apache.commons.lang3.StringUtils.isBlank;

import org.bson.Document;
import org.springframework.core.convert.converter.Converter;

import com.custodix.insite.local.ehr2edc.query.executor.common.concept.ConceptCode;
import com.custodix.insite.local.ehr2edc.shared.exceptions.SystemException;

public class ConceptReaderConverter implements Converter<Document, ConceptCode> {

	@Override
	public ConceptCode convert(final Document conceptDocument) {
		if (conceptDocument == null || isBlank(conceptDocument.getString("code"))) {
			throw new SystemException("Cannot convert a null or blank concept document.");
		}
		return ConceptCode.conceptFor(conceptDocument.getString("code"));
	}
}
