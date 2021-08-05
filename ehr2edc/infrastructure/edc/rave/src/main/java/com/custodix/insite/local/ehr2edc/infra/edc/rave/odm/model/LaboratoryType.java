package com.custodix.insite.local.ehr2edc.infra.edc.rave.odm.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

@XmlType
@XmlEnum
public enum LaboratoryType {
	@XmlEnumValue("Local") LOCAL,
	@XmlEnumValue("Central") CENTRAL
}
