package com.custodix.insite.local.ehr2edc.infra.odm.parser.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "FormRef")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlFormRef {
	@XmlAttribute(name = "FormOID")
	private String formOid;

	public String getFormOid() {
		return formOid;
	}
}
