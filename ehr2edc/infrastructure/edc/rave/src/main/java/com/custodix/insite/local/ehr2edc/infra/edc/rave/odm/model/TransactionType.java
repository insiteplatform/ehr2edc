package com.custodix.insite.local.ehr2edc.infra.edc.rave.odm.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

@XmlType
@XmlEnum
public enum TransactionType {
	@XmlEnumValue("Insert") INSERT,
	@XmlEnumValue("Update") UPDATE,
	@XmlEnumValue("Remove") REMOVE,
	@XmlEnumValue("Upsert") UPSERT,
	@XmlEnumValue("Context") CONTEXT
}