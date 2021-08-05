package com.custodix.insite.local.ehr2edc.snapshots;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;

import com.custodix.insite.local.ehr2edc.vocabulary.ItemDefinitionId;
import com.custodix.insite.local.ehr2edc.vocabulary.StudyId;

public final class StudySnapshot {
	private final StudyId studyId;
	private final String name;
	private final String description;

	private final MetaDataDefinitionSnapshot metadata;

	private final Collection<SubjectSnapshot> subjects;
	private final Collection<InvestigatorSnapshot> investigators;
	private final Map<ItemDefinitionId, ItemQueryMappingSnapshot> itemQueryMappings;

	private StudySnapshot(Builder builder) {
		studyId = builder.studyId;
		name = builder.name;
		description = builder.description;
		metadata = builder.metadata;
		subjects = builder.subjects;
		investigators = builder.investigators;
		itemQueryMappings = builder.itemQueryMappings == null ? Collections.emptyMap() : builder.itemQueryMappings;
	}

	public StudyId getStudyId() {
		return studyId;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public MetaDataDefinitionSnapshot getMetadata() {
		return metadata;
	}

	public Collection<SubjectSnapshot> getSubjects() {
		return subjects;
	}

	public Collection<InvestigatorSnapshot> getInvestigators() {
		return investigators;
	}

	public Map<ItemDefinitionId, ItemQueryMappingSnapshot> getItemQueryMappings() {
		return itemQueryMappings;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static final class Builder {
		private StudyId studyId;
		private String name;
		private String description;
		private MetaDataDefinitionSnapshot metadata = MetaDataDefinitionSnapshot.newBuilder()
				.build();
		private Collection<SubjectSnapshot> subjects = new HashSet<>();
		private Collection<InvestigatorSnapshot> investigators = new HashSet<>();
		private Map<ItemDefinitionId, ItemQueryMappingSnapshot> itemQueryMappings;

		private Builder() {
		}

		public Builder copy(StudySnapshot snapshot) {
			studyId = snapshot.studyId;
			name = snapshot.name;
			description = snapshot.description;
			metadata = snapshot.metadata;
			subjects = snapshot.subjects;
			investigators = snapshot.investigators;
			return this;
		}

		public Builder withStudyId(StudyId studyId) {
			this.studyId = studyId;
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

		public Builder withMetadata(MetaDataDefinitionSnapshot metadata) {
			this.metadata = metadata;
			return this;
		}

		public Builder withSubjects(Collection<SubjectSnapshot> subjects) {
			this.subjects = subjects;
			return this;
		}

		public Builder withInvestigators(Collection<InvestigatorSnapshot> investigators) {
			this.investigators = investigators;
			return this;
		}

		public Builder withItemQueryMappings(final Map<ItemDefinitionId, ItemQueryMappingSnapshot> val) {
			itemQueryMappings = val;
			return this;
		}

		public StudySnapshot build() {
			return new StudySnapshot(this);
		}
	}
}
