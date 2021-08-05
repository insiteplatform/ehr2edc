package com.custodix.insite.mongodb.export.patient.infrastructure.mongo.model.demographic;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import com.custodix.insite.mongodb.vocabulary.SubjectId;

@Document(collection = DemographicDocument.COLLECTION)
public final class DemographicDocument {
	public static final String COLLECTION = "Demographic";
	public static final String SUBJECTID_FIELD = "subjectId";

	@Id
	@SuppressWarnings("unused")
	private String id;

	private final SubjectId subjectId;
	private final String demographicType;
	private final String value;

	@PersistenceConstructor
	private DemographicDocument(final SubjectId subjectId, final String demographicType, final String value) {
		this.subjectId = subjectId;
		this.demographicType = demographicType;
		this.value = value;
	}

	private DemographicDocument(Builder builder) {
		id = builder.id;
		subjectId = builder.subjectId;
		demographicType = builder.demographicType;
		value = builder.value;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public String getId() {
		return id;
	}

	public SubjectId getSubjectId() {
		return subjectId;
	}

	public String getDemographicType() {
		return demographicType;
	}

	public String getValue() {
		return value;
	}

	public static final class Builder {
		private String id;
		private SubjectId subjectId;
		private String demographicType;
		private String value;

		private Builder() {
		}

		public Builder withId(final String val) {
			id = val;
			return this;
		}

		public Builder withSubjectId(final SubjectId val) {
			subjectId = val;
			return this;
		}

		public Builder withDemographicType(final String val) {
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
