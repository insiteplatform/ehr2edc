package com.custodix.insite.local.ehr2edc.infra.edc.rave.odm.model;

import static com.custodix.insite.local.ehr2edc.infra.edc.rave.odm.model.Namespaces.DEFAULT;

import java.util.List;

import javax.xml.bind.annotation.*;

@XmlRootElement(name = "StudyEventData")
@XmlAccessorType(XmlAccessType.FIELD)
public class StudyEventData {

	@XmlAttribute(name = "StudyEventOID")
	private String studyEventOID;
	@XmlAttribute(name = "StudyEventRepeatKey")
	private String studyEventRepeatKey;
	@XmlElement(name = "FormData",
				namespace = DEFAULT)
	private List<FormData> formData;

	private StudyEventData(Builder builder) {
		studyEventOID = builder.studyEventOID;
		studyEventRepeatKey = builder.studyEventRepeatKey;
		formData = builder.formData;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static final class Builder {
		private String studyEventOID;
		private String studyEventRepeatKey;
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

		public Builder withFormData(List<FormData> formData) {
			this.formData = formData;
			return this;
		}

		public StudyEventData build() {
			return new StudyEventData(this);
		}
	}
}
