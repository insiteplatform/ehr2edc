package com.custodix.insite.local.ehr2edc.infra.odm.parser.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "CodeListRef")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlCodeListRef {
	@XmlAttribute(name = "CodeListOID")
	private String codeListOid;

	public String getCodeListOid() {
		return codeListOid;
	}
}
