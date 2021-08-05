package com.custodix.insite.local.ehr2edc;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.custodix.insite.local.ehr2edc.metadata.model.MetaDataDefinition;
import com.custodix.insite.local.ehr2edc.vocabulary.StudyId;

public final class StudyMetaData {
	@NotNull
	@Valid
	private final StudyId id;
	@NotBlank
	private final String name;
	private final String description;
	@NotNull
	private final MetaDataDefinition metaDataDefinition;

	private StudyMetaData(Builder builder) {
		id = builder.id;
		name = builder.name;
		description = builder.description;
		metaDataDefinition = builder.metaDataDefinition;
	}

	public StudyId getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public MetaDataDefinition getMetaDataDefinition() {
		return metaDataDefinition;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static final class Builder {
		private StudyId id;
		private String name;
		private String description;
		private MetaDataDefinition metaDataDefinition;

		private Builder() {
		}

		public Builder withId(StudyId id) {
			this.id = id;
			return this;
		}

		public Builder withName(String name) {
			this.name = name;
			return this;
		}

		public Builder withDescription(String description) {
			this.description = description;
			return this;
		}

		public Builder withMetaDataDefinition(MetaDataDefinition metaDataDefinition) {
			this.metaDataDefinition = metaDataDefinition;
			return this;
		}

		public StudyMetaData build() {
			return new StudyMetaData(this);
		}
	}
}
