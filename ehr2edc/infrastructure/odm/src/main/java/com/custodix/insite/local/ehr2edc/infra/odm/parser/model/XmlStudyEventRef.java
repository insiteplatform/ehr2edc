package com.custodix.insite.local.ehr2edc.infra.odm.parser.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "StudyEventRef")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlStudyEventRef {

	@XmlAttribute(name = "StudyEventOID")
	private String studyEventOid;

	public String getStudyEventOid() {
		return studyEventOid;
	}
}
