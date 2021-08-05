package com.custodix.insite.local.ehr2edc.infra.odm.parser.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "CodeList")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlCodeList {
	@XmlAttribute(name = "OID")
	private String oid;

	public String getOid() {
		return oid;
	}
}
