package com.custodix.insite.local.ehr2edc.mongo.app.document;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import com.custodix.insite.local.ehr2edc.snapshots.InvestigatorSnapshot;
import com.custodix.insite.local.ehr2edc.snapshots.ItemQueryMappingSnapshot;
import com.custodix.insite.local.ehr2edc.snapshots.StudySnapshot;
import com.custodix.insite.local.ehr2edc.snapshots.SubjectSnapshot;
import com.custodix.insite.local.ehr2edc.vocabulary.ItemDefinitionId;
import com.custodix.insite.local.ehr2edc.vocabulary.StudyId;

@Document(collection = StudyDocument.COLLECTION)
public final class StudyDocument {
	public static final String COLLECTION = "studyMongoSnapshot";

	@Id
	private final String studyId;

	private final String name;
	private final String description;

	private final MetaDataDefinitionDocument metadata;

	private final Collection<SubjectDocument> subjects;
	private final Collection<InvestigatorDocument> investigators;
	private final Collection<ItemQueryMappingDocument> itemQueryMappings;

	//CHECKSTYLE:OFF
	@PersistenceConstructor
	private StudyDocument(String studyId, String name, String description, MetaDataDefinitionDocument metadata,
			Collection<SubjectDocument> subjects, Collection<InvestigatorDocument> investigators,
			Collection<ItemQueryMappingDocument> itemQueryMappings) {
		this.studyId = studyId;
		this.name = name;
		this.description = description;
		this.metadata = metadata;
		this.subjects = subjects;
		this.investigators = investigators;
		this.itemQueryMappings = itemQueryMappings;
	}

	private StudyDocument(Builder builder) {
		studyId = builder.studyId;
		name = builder.name;
		description = builder.description;
		metadata = builder.metadata;
		subjects = builder.subjects;
		investigators = builder.investigators;
		itemQueryMappings = builder.itemQueryMappings;
	}
	//CHECKSTYLE:ON

	public static StudyDocument fromSnapshot(StudySnapshot studySnapshot) {
		return new StudyDocument(studySnapshot.getStudyId()
				.getId(), studySnapshot.getName(), studySnapshot.getDescription(),
				MetaDataDefinitionDocument.fromSnapshot(studySnapshot.getMetadata()),
				SubjectDocument.fromSnapshots(studySnapshot.getSubjects()),
				InvestigatorDocument.fromSnapshots(studySnapshot.getInvestigators()),
				ItemQueryMappingDocument.fromSnapshots(studySnapshot.getItemQueryMappings()));
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public Builder toBuilder() {
		Builder builder = new Builder();
		builder.studyId = getStudyId();
		builder.name = getName();
		builder.description = getDescription();
		builder.metadata = getMetadata();
		builder.subjects = getSubjects();
		builder.investigators = getInvestigators();
		builder.itemQueryMappings = getItemQueryMappings();
		return builder;
	}

	public String getStudyId() {
		return studyId;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public MetaDataDefinitionDocument getMetadata() {
		return metadata;
	}

	public Collection<SubjectDocument> getSubjects() {
		return subjects;
	}

	public Collection<InvestigatorDocument> getInvestigators() {
		return investigators;
	}

	public Collection<ItemQueryMappingDocument> getItemQueryMappings() {
		return itemQueryMappings;
	}

	public StudySnapshot toSnapshot() {
		return StudySnapshot.newBuilder()
				.withStudyId(StudyId.of(studyId))
				.withMetadata(metadata.toSnapshot())
				.withName(name)
				.withDescription(description)
				.withInvestigators(toInvestigatorSnapshots())
				.withSubjects(toSubjectSnapshots())
				.withItemQueryMappings(toItemQueryMappings())
				.build();
	}

	private Map<ItemDefinitionId, ItemQueryMappingSnapshot> toItemQueryMappings() {
		return itemQueryMappings == null ?
				Collections.emptyMap() :
				itemQueryMappings.stream()
						.collect(Collectors.toMap(this::toItemDefinitionId, this::toItemQueryMappingSnapshot));
	}

	private ItemDefinitionId toItemDefinitionId(ItemQueryMappingDocument i) {
		return ItemDefinitionId.of(i.getItemId());
	}

	private ItemQueryMappingSnapshot toItemQueryMappingSnapshot(ItemQueryMappingDocument i) {
		return ItemQueryMappingSnapshot.newBuilder()
				.withValue(i.getValue())
				.build();
	}

	private List<SubjectSnapshot> toSubjectSnapshots() {
		return subjects.stream()
				.map(SubjectDocument::toSnapshot)
				.collect(Collectors.toList());
	}

	private List<InvestigatorSnapshot> toInvestigatorSnapshots() {
		return investigators.stream()
				.map(InvestigatorDocument::toSnapshot)
				.collect(Collectors.toList());
	}

	public static final class Builder {
		private String studyId;
		private String name;
		private String description;
		private MetaDataDefinitionDocument metadata;
		private Collection<SubjectDocument> subjects;
		private Collection<InvestigatorDocument> investigators;
		private Collection<ItemQueryMappingDocument> itemQueryMappings;

		private Builder() {
		}

		public Builder withStudyId(final String val) {
			studyId = val;
			return this;
		}

		public Builder withName(final String val) {
			name = val;
			return this;
		}

		public Builder withDescription(final String val) {
			description = val;
			return this;
		}

		public Builder withMetadata(final MetaDataDefinitionDocument val) {
			metadata = val;
			return this;
		}

		public Builder withSubjects(final Collection<SubjectDocument> val) {
			subjects = val;
			return this;
		}

		public Builder withInvestigators(final Collection<InvestigatorDocument> val) {
			investigators = val;
			return this;
		}

		public Builder withItemQueryMappings(final Collection<ItemQueryMappingDocument> val) {
			itemQueryMappings = val;
			return this;
		}

		public StudyDocument build() {
			return new StudyDocument(this);
		}
	}
}
