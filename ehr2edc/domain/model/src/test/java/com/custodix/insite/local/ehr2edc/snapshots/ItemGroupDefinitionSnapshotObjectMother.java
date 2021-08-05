package com.custodix.insite.local.ehr2edc.snapshots;

import static com.custodix.insite.local.ehr2edc.snapshots.ItemDefinitionSnapshotObjectMother.generateItem;
import static java.util.stream.Collectors.toList;

import java.util.Collections;
import java.util.stream.IntStream;

import com.custodix.insite.local.ehr2edc.vocabulary.ItemGroupDefinitionId;

public class ItemGroupDefinitionSnapshotObjectMother {

	public static ItemGroupDefinitionSnapshot aDefaultItemGroupDefinition() {
		return aDefaultItemGroupDefinitionBuilder().build();
	}

	public static ItemGroupDefinitionSnapshot.Builder aDefaultItemGroupDefinitionBuilder() {
		return ItemGroupDefinitionSnapshot.newBuilder()
				.withId(ItemGroupDefinitionId.of("itemGroup-1"))
				.withName("itemGroup-name-1")
				.withItemDefinitions(Collections.singletonList(ItemDefinitionSnapshotObjectMother.aDefaultItemDefinition()))
				.withRepeating(false);
	}

	public static ItemGroupDefinitionSnapshot generateContextItemGroup(int index) {
		return ItemGroupDefinitionSnapshot.newBuilder()
				.withId(ItemGroupDefinitionId.of("itemGroup-" + index))
				.withName("itemGroup-name-" + index)
				.withItemDefinitions(IntStream.range(0, 3)
						.mapToObj(i -> generateSpecificItemsCases(i))
						.collect(toList()))
				.withRepeating(true)
				.build();
	}

	private static ItemDefinitionSnapshot generateSpecificItemsCases(int i) {
		switch (i){
			case 0: return generateWithCMTRTSuffix();
			default: return generateItem();
		}
	}

	private static ItemDefinitionSnapshot generateWithCMTRTSuffix() {
		return generateItem("CMTRT");
	}

	public static ItemGroupDefinitionSnapshot generateItemGroup(int index) {
		return ItemGroupDefinitionSnapshot.newBuilder()
				.withId(ItemGroupDefinitionId.of("itemGroup-" + index))
				.withName("itemGroup-name-" + index)
				.withItemDefinitions(IntStream.range(0, 3)
						.mapToObj(i -> generateItem())
						.collect(toList()))
				.withRepeating(index % 2 == 1)
				.build();
	}
}