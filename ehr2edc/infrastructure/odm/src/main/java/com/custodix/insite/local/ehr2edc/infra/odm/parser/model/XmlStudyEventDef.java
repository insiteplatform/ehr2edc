package com.custodix.insite.local.ehr2edc.infra.odm.parser.model;

import java.util.List;

import javax.xml.bind.annotation.*;

@XmlRootElement(name = "StudyEventDef")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlStudyEventDef {

	@XmlAttribute(name = "OID")
	private String oid;
	@XmlAttribute(name = "Parent")
	private String parentId;
	@XmlAttribute(name = "Name")
	private String name;
	@XmlElement(name = "FormRef")
	private List<XmlFormRef> formRefs;

	public String getOid() {
		return oid;
	}

	public String getParentId() {
		return parentId;
	}

	public String getName() {
		return name;
	}

	public List<XmlFormRef> getFormRefs() {
		return formRefs;
	}
}
