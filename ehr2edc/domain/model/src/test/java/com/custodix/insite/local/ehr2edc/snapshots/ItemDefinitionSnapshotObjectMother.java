package com.custodix.insite.local.ehr2edc.snapshots;

import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;
import static java.util.UUID.randomUUID;

import com.custodix.insite.local.ehr2edc.vocabulary.ItemDefinitionId;

public class ItemDefinitionSnapshotObjectMother {

	public static ItemDefinitionSnapshot generateItem(String contextSuffix) {
		String itemId = randomUUID().toString();
		CodeListSnapshot codeList = CodeListSnapshot.newBuilder().withId("item-codelist-" + itemId + contextSuffix).build();
		QuestionSnapshot question = new QuestionSnapshot(singletonMap("EN", "item-label-" + itemId + contextSuffix));
		ItemLabelSnapshot label = new ItemLabelSnapshot("item-name-" + itemId, question);
		return ItemDefinitionSnapshot.newBuilder()
				.withId(ItemDefinitionId.of(itemId + contextSuffix))
				.withDataType("item-datatype-" + itemId + contextSuffix)
				.withCodeList(codeList)
				.withLabel(label)
				.build();
	}

	public static ItemDefinitionSnapshot generateItem() {
		return generateItem("");
	}

	public static ItemDefinitionSnapshot aDefaultItemDefinition() {
		return aDefaultItemDefinitionBuilder().build();
	}

	public static ItemDefinitionSnapshot.Builder aDefaultItemDefinitionBuilder() {
		return ItemDefinitionSnapshot.newBuilder()
				.withId(ItemDefinitionId.of("ItemDefinitionSnapshot.id"))
				.withLabel(new ItemLabelSnapshot("ItemDefinitionSnapshot.name",
						new QuestionSnapshot(singletonMap("EN", "QuestionSnapshot.en"))))
				.withCodeList(CodeListSnapshot.newBuilder().withId("CodeListSnapshot.id").build())
				.withDataType("dataType")
				.withLength(123)
				.withMeasurementUnits(singletonList(MeasurementUnitSnapshotObjectMother.aDefaultMeasurementUnitSnapshot()));
	}
}