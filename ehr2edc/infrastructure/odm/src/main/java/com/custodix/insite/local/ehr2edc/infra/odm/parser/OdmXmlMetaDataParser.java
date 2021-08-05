package com.custodix.insite.local.ehr2edc.infra.odm.parser;

import com.custodix.insite.local.ehr2edc.infra.odm.parser.model.XmlOdm;

public interface OdmXmlMetaDataParser {
	XmlOdm parseToXmlModel(String xml);
}
