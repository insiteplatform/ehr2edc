package com.custodix.insite.local.ehr2edc.infra.edc.openclinica.odm.model;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public final class ClinicalData {
	@XmlAttribute(name = "StudyOID")
	private String studyOID;
	@XmlAttribute(name = "MetaDataVersionOID")
	private String metaDataVersionOID;
	@XmlElement(name = "SubjectData")
	private List<SubjectData> subjectData;

	private ClinicalData() {
		// JAXB
	}

	private ClinicalData(Builder builder) {
		studyOID = builder.studyOID;
		metaDataVersionOID = builder.metaDataVersionOID;
		subjectData = builder.subjectData;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static final class Builder {
		private String studyOID;
		private String metaDataVersionOID;
		private List<SubjectData> subjectData;

		private Builder() {
		}

		public Builder withStudyOID(String studyOID) {
			this.studyOID = studyOID;
			return this;
		}

		public Builder withMetaDataVersionOID(String metaDataVersionOID) {
			this.metaDataVersionOID = metaDataVersionOID;
			return this;
		}

		public Builder withSubjectData(List<SubjectData> subjectData) {
			this.subjectData = subjectData;
			return this;
		}

		public ClinicalData build() {
			return new ClinicalData(this);
		}
	}
}
