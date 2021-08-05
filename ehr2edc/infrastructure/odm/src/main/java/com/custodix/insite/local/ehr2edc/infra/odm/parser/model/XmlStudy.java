package com.custodix.insite.local.ehr2edc.infra.odm.parser.model;

import javax.xml.bind.annotation.*;

@XmlRootElement(name = "Study")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlStudy {
	@XmlAttribute(name = "OID")
	private String oid;

	@XmlElement(name = "GlobalVariables")
	private XmlGlobalVariables globalVariables;

	@XmlElement(name = "MetaDataVersion")
	private XmlMetaDataVersion metaDataVersion;

	@XmlElement(name = "BasicDefinitions")
	private XmlBasicDefinitions basicDefinitions;

	public String getOid() {
		return oid;
	}

	public XmlGlobalVariables getGlobalVariables() {
		return globalVariables;
	}

	public XmlMetaDataVersion getMetaDataVersion() {
		return metaDataVersion;
	}

	public XmlBasicDefinitions getBasicDefinitions() {
		return basicDefinitions;
	}
}
