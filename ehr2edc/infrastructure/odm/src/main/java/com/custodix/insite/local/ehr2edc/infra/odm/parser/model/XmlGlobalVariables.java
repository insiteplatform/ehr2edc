package com.custodix.insite.local.ehr2edc.infra.odm.parser.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "GlobalVariables")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlGlobalVariables {

	@XmlElement(name = "StudyName")
	private String name;
	@XmlElement(name = "StudyDescription")
	private String description;

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}
}
