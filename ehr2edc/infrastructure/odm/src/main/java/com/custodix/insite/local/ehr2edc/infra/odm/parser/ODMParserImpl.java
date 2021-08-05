package com.custodix.insite.local.ehr2edc.infra.odm.parser;

import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import com.custodix.insite.local.ehr2edc.StudyMetaData;
import com.custodix.insite.local.ehr2edc.domain.service.ODMParser;
import com.custodix.insite.local.ehr2edc.infra.odm.mapper.XmlModelMapper;
import com.custodix.insite.local.ehr2edc.infra.odm.parser.model.XmlOdm;
import com.custodix.insite.local.ehr2edc.vocabulary.StudyODM;

@Validated
@Component
class ODMParserImpl implements ODMParser {
	private final OdmXmlMetaDataParser odmXmlMetaDataParser;

	ODMParserImpl(OdmXmlMetaDataParser odmXmlMetaDataParser) {
		this.odmXmlMetaDataParser = odmXmlMetaDataParser;
	}

	@Override
	public StudyMetaData parse(StudyODM odm) {
		XmlOdm metaData = odmXmlMetaDataParser.parseToXmlModel(odm.getOdm());
		return new XmlModelMapper().toStudyMetaData(metaData);
	}
}
