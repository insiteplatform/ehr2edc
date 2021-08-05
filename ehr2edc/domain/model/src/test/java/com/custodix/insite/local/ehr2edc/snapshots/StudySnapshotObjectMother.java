package com.custodix.insite.local.ehr2edc.snapshots;

import static com.custodix.insite.local.ehr2edc.snapshots.ItemQueryMappingSnapshotObjectMother.aDefaultItemQueryMappingSnapshot;
import static com.custodix.insite.local.ehr2edc.snapshots.MetadataDefinitionSnapshotObjectMother.generateMetadata;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toMap;

import java.util.Map;

import com.custodix.insite.local.ehr2edc.vocabulary.ItemDefinitionId;
import com.custodix.insite.local.ehr2edc.vocabulary.StudyId;
import com.custodix.insite.local.ehr2edc.vocabulary.UserIdentifier;
import com.google.common.collect.Sets;

public class StudySnapshotObjectMother {

	public static StudySnapshot aDefaultStudySnapshot() {
		return aDefaultStudySnapshotBuilder().build();
	}

	public static StudySnapshot.Builder aDefaultStudySnapshotBuilder() {
		return aDefaultStudySnapshotBuilder(UserIdentifier.of("42"));
	}

	public static StudySnapshot.Builder aDefaultStudySnapshotBuilder(UserIdentifier investigator) {
		return StudySnapshot.newBuilder()
				.withStudyId(StudyId.of("studyId-123"))
				.withName("studyName")
				.withDescription("studyDescription")
				.withInvestigators(InvestigatorSnapshotObjectMother.generateInvestigatorFromUserIdentifier(investigator))
				.withMetadata(generateMetadata())
				.withSubjects(Sets.newHashSet(SubjectSnapshotObjectMother.aDefaultSubjectSnapshot()));
	}

	public static StudySnapshot generateStudy(StudyId studyId, String name, String studyDescription,
			UserIdentifier investigator) {
		MetaDataDefinitionSnapshot metadata = generateMetadata();
		Map<ItemDefinitionId, ItemQueryMappingSnapshot> itemQueryMappings = generateItemQueryMappings(metadata);

		return StudySnapshot.newBuilder()
				.withStudyId(studyId)
				.withName(name)
				.withDescription(studyDescription)
				.withInvestigators(InvestigatorSnapshotObjectMother.generateInvestigatorFromUserIdentifier(investigator))
				.withMetadata(metadata)
				.withSubjects(Sets.newHashSet(SubjectSnapshotObjectMother.aDefaultSubjectSnapshot()))
				.withItemQueryMappings(itemQueryMappings)
				.build();
	}

	private static Map<ItemDefinitionId, ItemQueryMappingSnapshot> generateItemQueryMappings(final MetaDataDefinitionSnapshot metadata) {
		return metadata.getEventDefinitions().stream()
					.flatMap( event -> event.getFormDefinitionSnapshots().stream() )
					.flatMap( form -> form.getItemGroupDefinitions().stream() )
					.flatMap( group -> group.getItemDefinitions().stream()).collect(toMap(
							ItemDefinitionSnapshot::getId,
							i -> aDefaultItemQueryMappingSnapshot()
					));
	}

	public static StudySnapshot generateStudyWithoutFormDefinitions(StudyId studyId, UserIdentifier investigator) {
		return StudySnapshot.newBuilder()
				.withStudyId(studyId)
				.withName("Study name")
				.withDescription("Study description")
				.withInvestigators(InvestigatorSnapshotObjectMother.generateInvestigatorFromUserIdentifier(investigator))
				.withMetadata(MetaDataDefinitionSnapshot.newBuilder()
						.withEventDefinitions(singletonList(EventDefinitionSnapshotObjectMother.generateEventDefinitionWithNoFormDefinitions()))
						.build())
				.build();
	}
}