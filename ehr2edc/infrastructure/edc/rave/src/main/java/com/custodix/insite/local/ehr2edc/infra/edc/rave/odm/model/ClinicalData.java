package com.custodix.insite.local.ehr2edc.infra.edc.rave.odm.model;

import static com.custodix.insite.local.ehr2edc.infra.edc.rave.odm.model.Namespaces.DEFAULT;

import java.util.List;

import javax.xml.bind.annotation.*;

@XmlRootElement(name = "ClinicalData")
@XmlAccessorType(XmlAccessType.FIELD)
public final class ClinicalData {
	@XmlAttribute(name = "StudyOID")
	private String studyOID;
	@XmlAttribute(name = "MetaDataVersionOID")
	private String MetaDataVersionOID;
	@XmlElement(name = "SubjectData",
				namespace = DEFAULT)
	private List<SubjectData> subjectData;

	private ClinicalData() {
		//JAXB deserialization
	}

	private ClinicalData(Builder builder) {
		studyOID = builder.studyOID;
		MetaDataVersionOID = builder.MetaDataVersionOID;
		subjectData = builder.subjectData;
	}

	public String getStudyOID() {
		return studyOID;
	}

	public List<SubjectData> getSubjectData() {
		return subjectData;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static final class Builder {
		private String studyOID;
		private String MetaDataVersionOID;
		private List<SubjectData> subjectData;

		private Builder() {
		}

		public Builder withStudyOID(String studyOID) {
			this.studyOID = studyOID;
			return this;
		}

		public Builder withMetaDataVersionOID(String MetaDataVersionOID) {
			this.MetaDataVersionOID = MetaDataVersionOID;
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
