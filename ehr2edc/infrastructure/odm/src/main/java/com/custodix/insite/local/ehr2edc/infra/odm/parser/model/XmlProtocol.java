package com.custodix.insite.local.ehr2edc.infra.odm.parser.model;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Protocol")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlProtocol {

	@XmlElement(name = "StudyEventRef")
	private List<XmlStudyEventRef> studyEventRefs;

	public List<XmlStudyEventRef> getStudyEventRefs() {
		return studyEventRefs;
	}
}
