package com.custodix.insite.local.ehr2edc.infra.odm.parser.model;

import java.util.List;
import java.util.Optional;

import javax.xml.bind.annotation.*;

@XmlRootElement(name = "MetaDataVersion")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlMetaDataVersion {
	@XmlAttribute(name = "OID")
	private String oid;
	@XmlAttribute(name = "Name")
	private String name;
	@XmlElement(name = "Protocol")
	private XmlProtocol protocol;
	@XmlElement(name = "StudyEventDef")
	private List<XmlStudyEventDef> studyEventDefs;
	@XmlElement(name = "FormDef")
	private List<XmlFormDef> formDefs;
	@XmlElement(name = "ItemGroupDef")
	private List<XmlItemGroupDef> itemGroupDefs;
	@XmlElement(name = "ItemDef")
	private List<XmlItemDef> itemDefs;
	@XmlElement(name = "CodeList")
	private List<XmlCodeList> codeLists;

	public String getOid() {
		return oid;
	}

	public String getName() {
		return name;
	}

	public XmlProtocol getProtocol() {
		return protocol;
	}

	public List<XmlStudyEventDef> getStudyEventDefs() {
		return studyEventDefs;
	}

	public List<XmlFormDef> getFormDefs() {
		return formDefs;
	}

	public List<XmlItemGroupDef> getItemGroupDefs() {
		return itemGroupDefs;
	}

	public List<XmlItemDef> getItemDefs() {
		return itemDefs;
	}

	public List<XmlCodeList> getCodeLists() {
		return codeLists;
	}

	public Optional<XmlCodeList> getCodeList(String oid) {
		return codeLists.stream()
				.filter(cl -> cl.getOid()
						.equals(oid))
				.findFirst();
	}

	public Optional<XmlItemDef> getItemDef(String oid) {
		return itemDefs.stream()
				.filter(cl -> cl.getOid()
						.equals(oid))
				.findFirst();
	}

	public Optional<XmlItemGroupDef> getItemGroupDef(String oid) {
		return itemGroupDefs.stream()
				.filter(cl -> cl.getOid()
						.equals(oid))
				.findFirst();
	}

	public Optional<XmlFormDef> getFormDef(String oid) {
		return formDefs.stream()
				.filter(cl -> cl.getOid()
						.equals(oid))
				.findFirst();
	}
}
