package com.custodix.insite.local.ehr2edc.infra.odm.parser.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "ItemRef")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlItemRef {
	@XmlAttribute(name = "ItemOID")
	private String itemOid;

	public String getItemOid() {
		return itemOid;
	}
}
