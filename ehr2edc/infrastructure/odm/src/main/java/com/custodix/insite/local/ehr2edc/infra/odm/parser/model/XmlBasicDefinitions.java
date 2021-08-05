package com.custodix.insite.local.ehr2edc.infra.odm.parser.model;

import java.util.List;
import java.util.Optional;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "BasicDefinitions")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlBasicDefinitions {
	@XmlElement(name = "MeasurementUnit")
	private List<XmlMeasurementUnit> measurementUnits;

	public List<XmlMeasurementUnit> getMeasurementUnits() {
		return measurementUnits;
	}

	public Optional<XmlMeasurementUnit> getMeasurementUnit(String oid) {
		return measurementUnits.stream()
				.filter(cl -> cl.getOid().equals(oid))
				.findFirst();
	}
}
