package com.custodix.insite.local.ehr2edc.infra.odm.parser.model;

import java.util.List;

import javax.xml.bind.annotation.*;

@XmlRootElement(name = "ItemDef")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlItemDef {
	@XmlAttribute(name = "OID")
	private String oid;

	@XmlAttribute(name = "Name")
	private String name;

	@XmlAttribute(name = "DataType")
	private String dataType;

	@XmlAttribute(name = "Length")
	private int length;

	@XmlElement(name = "CodeListRef")
	private XmlCodeListRef codeListRef;

	@XmlElement(name = "MeasurementUnitRef")
	private List<XmlMeasurementUnitRef> measurementUnitRefs;

	@XmlElement(name = "Question")
	private XmlQuestion question;

	public XmlCodeListRef getCodeListRef() {
		return codeListRef;
	}

	public String getOid() {
		return oid;
	}

	public String getName() {
		return name;
	}

	public String getDataType() {
		return dataType;
	}

	public int getLength() {
		return length;
	}

	public List<XmlMeasurementUnitRef> getMeasurementUnitRefs() {
		return measurementUnitRefs;
	}

	public XmlQuestion getQuestion() { return question; }
}
