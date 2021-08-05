package com.custodix.insite.local.ehr2edc.infra.odm.parser.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "ODM")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlOdm {
	@XmlElement(name = "Study")
	private XmlStudy study;

	public XmlStudy getStudy() {
		return study;
	}

	public void setStudy(final XmlStudy study) {
		this.study = study;
	}
}
