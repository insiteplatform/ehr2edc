package com.custodix.insite.local.ehr2edc.infra.edc.openclinica.odm.model;

import static com.custodix.insite.local.ehr2edc.infra.edc.openclinica.odm.model.Namespaces.OPEN_CLINICA;

import java.time.LocalDate;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public final class StudyEventData {

	@XmlAttribute(name = "StudyEventOID")
	private String studyEventOID;
	@XmlAttribute(name = "StudyEventRepeatKey")
	private String studyEventRepeatKey;
	@XmlAttribute(name = "StartDate",
				  namespace = OPEN_CLINICA)
	private String startDate;
	@XmlElement(name = "FormData")
	private List<FormData> formData;

	private StudyEventData() {
		// JAXB
	}

	private StudyEventData(Builder builder) {
		studyEventOID = builder.studyEventOID;
		studyEventRepeatKey = builder.studyEventRepeatKey;
		startDate = builder.startDate.toString();
		formData = builder.formData;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static final class Builder {
		private String studyEventOID;
		private String studyEventRepeatKey;
		private LocalDate startDate;
		private List<FormData> formData;

		private Builder() {
		}

		public Builder withStudyEventOID(String studyEventOID) {
			this.studyEventOID = studyEventOID;
			return this;
		}

		public Builder withStudyEventRepeatKey(String studyEventRepeatKey) {
			this.studyEventRepeatKey = studyEventRepeatKey;
			return this;
		}

		public Builder withStartDate(LocalDate startDate) {
			this.startDate = startDate;
			return this;
		}

		public Builder withFormData(List<FormData> formData) {
			this.formData = formData;
			return this;
		}

		public StudyEventData build() {
			return new StudyEventData(this);
		}
	}
}
