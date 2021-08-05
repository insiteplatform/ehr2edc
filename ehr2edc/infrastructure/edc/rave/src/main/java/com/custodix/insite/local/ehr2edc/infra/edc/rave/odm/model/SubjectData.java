package com.custodix.insite.local.ehr2edc.infra.edc.rave.odm.model;

import static com.custodix.insite.local.ehr2edc.infra.edc.rave.odm.model.Namespaces.DEFAULT;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "SubjectData")
public final class SubjectData {
	@XmlAttribute(name = "SubjectKey")
	private String subjectKey;
	@XmlAttribute(name = "TransactionType")
	private TransactionType transactionType;
	@XmlElement(name = "SiteRef",
				namespace = DEFAULT)
	private SiteRef siteRef;
	@XmlElement(name = "StudyEventData",
				namespace = DEFAULT)
	private StudyEventData studyEventData;

	private SubjectData() {
		//JAXB deserialization
	}

	private SubjectData(Builder builder) {
		subjectKey = builder.subjectKey;
		transactionType = builder.transactionType;
		siteRef = builder.siteRef;
		studyEventData = builder.studyEventData;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public String getSubjectKey() {
		return subjectKey;
	}

	public SiteRef getSiteRef() {
		return siteRef;
	}

	public static final class Builder {
		private String subjectKey;
		private TransactionType transactionType;
		private SiteRef siteRef;
		private StudyEventData studyEventData;

		private Builder() {
		}

		public Builder withSubjectKey(String subjectKey) {
			this.subjectKey = subjectKey;
			return this;
		}

		public Builder withTransactionType(TransactionType transactionType) {
			this.transactionType = transactionType;
			return this;
		}

		public Builder withSiteRef(SiteRef siteRef) {
			this.siteRef = siteRef;
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
