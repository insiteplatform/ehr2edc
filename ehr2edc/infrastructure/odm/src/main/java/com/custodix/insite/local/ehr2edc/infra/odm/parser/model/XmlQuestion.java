package com.custodix.insite.local.ehr2edc.infra.odm.parser.model;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

public class XmlQuestion {
	@XmlElement(name = "TranslatedText")
	private List<XmlTranslatedText> translatedTexts;

	public List<XmlTranslatedText> getTranslatedTexts() {
		return translatedTexts;
	}
}
