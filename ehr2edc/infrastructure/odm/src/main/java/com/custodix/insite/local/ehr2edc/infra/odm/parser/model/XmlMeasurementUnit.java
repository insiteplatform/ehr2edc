package com.custodix.insite.local.ehr2edc.infra.odm.parser.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "MeasurementUnit")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlMeasurementUnit {
	@XmlAttribute(name = "OID")
	private String oid;

	@XmlAttribute(name = "Name")
	private String name;

	public String getOid() {
		return oid;
	}

	public String getName() {
		return name;
	}
}
