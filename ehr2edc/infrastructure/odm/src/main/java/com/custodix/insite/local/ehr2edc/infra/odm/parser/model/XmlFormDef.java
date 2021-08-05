package com.custodix.insite.local.ehr2edc.infra.odm.parser.model;

import java.util.List;

import javax.xml.bind.annotation.*;

@XmlRootElement(name = "FormDef")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlFormDef {
	@XmlAttribute(name = "OID")
	private String oid;
	@XmlAttribute(name = "Name")
	private String name;
	@XmlElement(name = "ItemGroupRef")
	private List<XmlItemGroupRef> itemGroupRefs;
	@XmlAttribute(name = "localLab")
	private String localLab;

	public String getOid() {
		return oid;
	}

	public List<XmlItemGroupRef> getItemGroupRefs() {
		return itemGroupRefs;
	}

	public String getName() {
		return name;
	}

	public String getLocalLab() {
		return localLab;
	}
}
