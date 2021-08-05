package com.custodix.insite.local.ehr2edc.infra.edc.openclinica.odm.model;

import static com.custodix.insite.local.ehr2edc.infra.edc.openclinica.odm.model.Namespaces.OPEN_CLINICA;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public final class SubjectData {
	@XmlAttribute(name = "StudySubjectID", namespace = OPEN_CLINICA)
	private String subjectKey;
	@XmlElement(name = "StudyEventData")
	private StudyEventData studyEventData;

	private SubjectData() {
		// JAXB
	}

	private SubjectData(Builder builder) {
		subjectKey = builder.subjectKey;
		studyEventData = builder.studyEventData;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static final class Builder {
		private String subjectKey;
		private StudyEventData studyEventData;

		private Builder() {
		}

		public Builder withSubjectKey(String subjectKey) {
			this.subjectKey = subjectKey;
			return this;
		}

		public Builder withStudyEventData(StudyEventData studyEventData) {
			this.studyEventData = studyEventData;
			return this;
		}

		public SubjectData build() {
			return new SubjectData(this);
		}
	}
}
