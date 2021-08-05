package com.custodix.insite.local.ehr2edc.snapshots;

import static com.custodix.insite.local.ehr2edc.snapshots.ItemGroupDefinitionSnapshotObjectMother.generateContextItemGroup;
import static com.custodix.insite.local.ehr2edc.snapshots.ItemGroupDefinitionSnapshotObjectMother.generateItemGroup;
import static java.util.Collections.singletonList;

import com.custodix.insite.local.ehr2edc.vocabulary.FormDefinitionId;

public class FormDefinitionSnapshotObjectMother {

	public static FormDefinitionSnapshot aDefaultFormDefinitionSnapshot() {
		return aDefaultFormDefinitionSnapshotBuilder()
				.build();
	}

	public static FormDefinitionSnapshot.Builder aDefaultFormDefinitionSnapshotBuilder() {
		return FormDefinitionSnapshot.newBuilder()
				.withId(FormDefinitionId.of("FormDefinitionSnapshot.id"))
				.withName("FormDefinitionSnapshot.name")
				.withItemGroupDefinitions(singletonList(ItemGroupDefinitionSnapshotObjectMother.aDefaultItemGroupDefinition()));
	}

	static FormDefinitionSnapshot formDefinitionSnapshot() {
		return FormDefinitionSnapshot.newBuilder()
					.withId(FormDefinitionId.of("POPULATE-ME"))
					.build();
	}

	public static FormDefinitionSnapshot generateFormDefinition(int index) {
		return FormDefinitionSnapshot.newBuilder()
				.withId(FormDefinitionId.of("form-" + index))
				.withName("Form nr. " + index)
				.withItemGroupDefinitions(singletonList(index == 2 ? generateContextItemGroup(index) : generateItemGroup(index)))
				.build();

	}
}
