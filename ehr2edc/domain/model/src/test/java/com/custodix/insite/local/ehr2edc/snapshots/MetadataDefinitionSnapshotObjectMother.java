package com.custodix.insite.local.ehr2edc.snapshots;

import static com.custodix.insite.local.ehr2edc.snapshots.EventDefinitionSnapshotObjectMother.aDefaultEventDefinitionSnapshot;
import static com.custodix.insite.local.ehr2edc.snapshots.EventDefinitionSnapshotObjectMother.generateEventDefinition;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

public class MetadataDefinitionSnapshotObjectMother {

	public static MetaDataDefinitionSnapshot.Builder aDefaultMetaDataDefinitionSnapshotBuilder() {
		return MetaDataDefinitionSnapshot.newBuilder()
				.withEventDefinitions(asList(aDefaultEventDefinitionSnapshot()));
	}

	public static MetaDataDefinitionSnapshot aMetaDataDefinitionSnapshotWithEventDefinitions(
			EventDefinitionSnapshot... eventDefinitionSnapshots) {
		return MetaDataDefinitionSnapshot.newBuilder()
				.withEventDefinitions(asList(eventDefinitionSnapshots))
				.build();
	}

	public static MetaDataDefinitionSnapshot generateMetadata() {
		return MetaDataDefinitionSnapshot.newBuilder()
				.withEventDefinitions(singletonList(generateEventDefinition()))
				.build();
	}
}
