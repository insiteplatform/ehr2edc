package com.custodix.insite.local.ehr2edc.infra.odm.parser.model;

import java.util.List;

import javax.xml.bind.annotation.*;

import org.apache.commons.lang3.StringUtils;

@XmlRootElement(name = "ItemGroupDef")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlItemGroupDef {
	@XmlAttribute(name = "OID")
	private String oid;

	@XmlAttribute(name = "Name")
	private String name;

	@XmlAttribute(name = "Repeating")
	private String repeating;

	@XmlElement(name = "ItemRef")
	private List<XmlItemRef> itemRefs;

	public List<XmlItemRef> getItemRefs() {
		return itemRefs;
	}

	public String getOid() {
		return oid;
	}

	public String getName() {
		return name;
	}

	public boolean isRepeating() {
		return StringUtils.equalsIgnoreCase("yes",repeating);
	}
}
