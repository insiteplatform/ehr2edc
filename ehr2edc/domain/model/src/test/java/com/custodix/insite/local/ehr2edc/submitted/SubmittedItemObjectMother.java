package com.custodix.insite.local.ehr2edc.submitted;

import static com.custodix.insite.local.ehr2edc.submitted.SubmittedItemLabelObjectMother.aDefaultSubmittedItemLabelBuilder;
import static com.custodix.insite.local.ehr2edc.submitted.SubmittedLabeledValueObjectMother.aDefaultSubmittedLabeledValue;

import org.springframework.test.util.ReflectionTestUtils;

import com.custodix.insite.local.ehr2edc.metadata.model.ItemLabel;
import com.custodix.insite.local.ehr2edc.populator.PopulatedItem;
import com.custodix.insite.local.ehr2edc.vocabulary.ItemDefinitionId;
import com.custodix.insite.local.ehr2edc.vocabulary.ItemId;
import com.custodix.insite.local.ehr2edc.vocabulary.SubmittedItemId;

public class SubmittedItemObjectMother {
	public static SubmittedItem.Builder aDefaultSubmittedItemBuilder() {
		return SubmittedItem.newBuilder()
				.withInstanceId(SubmittedItemId.of("instance-1"))
				.withId(ItemDefinitionId.of("itemId"))
				.withValue(aDefaultSubmittedLabeledValue())
				.withLabel(aDefaultSubmittedItemLabelBuilder().build())
				.withKey(true)
				.withPopulatedItemId(ItemId.of("populatedItemId"))
				.withSubmittedToEDC(true);
	}

	public static SubmittedItem aSubmittedItem(PopulatedItem populatedItem) {
		SubmittedItem.Builder builder = SubmittedItem.newBuilder()
				.withInstanceId(SubmittedItemId.of("${json-unit.ignore}"))
				.withId(populatedItem.getId())
				.withDataPoint(populatedItem.getDataPoint())
				.withValue(SubmittedLabeledValue.newBuilder()
						.withValue(populatedItem.getValueCode())
						.build())
				.withLabel(SubmittedItemLabelObjectMother.aSubmittedItemLabel(
						(ItemLabel) ReflectionTestUtils.getField(populatedItem, "label")))
				.withPopulatedItemId(populatedItem.getInstanceId())
				.withSubmittedToEDC(!populatedItem.isReadOnly())
				.withKey(populatedItem.isKey());
		addUnit(builder, populatedItem);
		return builder.build();
	}

	private static void addUnit(SubmittedItem.Builder builder, PopulatedItem populatedItem) {
		populatedItem.getMeasurementUnitReference()
				.ifPresent(unit -> {
					builder.withSubmittedMeasurementUnitReference(SubmittedMeasurementUnitReference.newBuilder()
							.withId(unit.getId())
							.withSubmittedToEDC(!unit.isReadOnly())
							.build());
				});
	}
}
