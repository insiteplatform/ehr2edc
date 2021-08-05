package com.custodix.insite.local.ehr2edc.infra.odm.parser.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

public class XmlTranslatedText {
	@XmlAttribute(name = "lang", namespace = "http://www.w3.org/XML/1998/namespace")
	private String lang = "";

	@XmlValue
	private String text;

	public XmlTranslatedText() {
		//for JAXB
	}

	public XmlTranslatedText(final String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}

	public String getLang() {
		return lang;
	}
}
