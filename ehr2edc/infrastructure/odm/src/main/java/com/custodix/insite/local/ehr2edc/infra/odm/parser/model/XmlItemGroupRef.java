package com.custodix.insite.local.ehr2edc.infra.odm.parser.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "ItemGroupRef")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlItemGroupRef {
	@XmlAttribute(name = "ItemGroupOID")
	private String itemGroupOid;

	public String getItemGroupOid() {
		return itemGroupOid;
	}
}
