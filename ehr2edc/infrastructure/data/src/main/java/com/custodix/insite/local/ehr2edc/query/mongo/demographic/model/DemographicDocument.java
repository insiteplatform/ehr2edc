package com.custodix.insite.local.ehr2edc.query.mongo.demographic.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import com.custodix.insite.local.ehr2edc.query.executor.demographic.domain.DemographicType;
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId;

@Document(collection = DemographicDocument.COLLECTION)
public class DemographicDocument {
	public static final String COLLECTION = "Demographic";

	@Id
	@SuppressWarnings("unused")
	private String id;

	private SubjectId subjectId;
	private DemographicType demographicType;
	private String value;

	@PersistenceConstructor
	private DemographicDocument(SubjectId subjectId, DemographicType demographicType, String value) {
		this.subjectId = subjectId;
		this.demographicType = demographicType;
		this.value = value;
	}

	private DemographicDocument(final Builder builder) {
		subjectId = builder.subjectId;
		demographicType = builder.demographicType;
		value = builder.value;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public SubjectId getSubjectId() {
		return subjectId;
	}

	public DemographicType getDemographicType() {
		return demographicType;
	}

	public String getValue() {
		return value;
	}

	public static final class Builder {
		private SubjectId subjectId;
		private DemographicType demographicType;
		private String value;

		private Builder() {
		}

		public Builder withSubjectId(final SubjectId val) {
			subjectId = val;
			return this;
		}

		public Builder withDemographicType(final DemographicType val) {
			demographicType = val;
			return this;
		}

		public Builder withValue(final String val) {
			value = val;
			return this;
		}

		public DemographicDocument build() {
			return new DemographicDocument(this);
		}
	}
}
