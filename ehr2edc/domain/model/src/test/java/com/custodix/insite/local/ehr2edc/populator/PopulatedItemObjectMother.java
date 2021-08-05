package com.custodix.insite.local.ehr2edc.populator;

import java.util.Collections;

import com.custodix.insite.local.ehr2edc.metadata.model.ItemLabel;
import com.custodix.insite.local.ehr2edc.provenance.model.ProvenanceDemographicObjectMother;
import com.custodix.insite.local.ehr2edc.query.executor.common.projector.model.LabeledValue;
import com.custodix.insite.local.ehr2edc.vocabulary.ItemDefinitionId;
import com.custodix.insite.local.ehr2edc.vocabulary.ItemId;

public class PopulatedItemObjectMother {
	public static final ItemId ITEM_ID = ItemId.of("item-123-123");
	public static final ItemDefinitionId ITEM_DEFINITION_ID = ItemDefinitionId.of("123-123");
	public static final LabeledValue VALUE = new LabeledValue("aValue");
	public static final String LABEL = "name";

	public static PopulatedItem aDefaultPopulatedItem() {
		return aDefaultPopulatedItemBuilder().build();
	}

	public static PopulatedItem aPopulatedItemWithMeasurementUnitReference(boolean readOnly) {
		PopulatedMeasurementUnitReference measurementUnitReference = PopulatedMeasurementUnitReference.newBuilder()
				.withId("13899.CM/IN.cm")
				.withReadOnly(readOnly)
				.build();
		return aDefaultPopulatedItemBuilder().withMeasurementUnitReference(measurementUnitReference).build();
	}

	public static PopulatedItem.Builder aDefaultPopulatedItemBuilder() {
		return PopulatedItem.newBuilder()
				.withInstanceId(ITEM_ID)
				.withId(ITEM_DEFINITION_ID)
				.withValue(VALUE)
				.withLabel(ItemLabel.newBuilder().withName(LABEL).build())
				.withDataPoint(ProvenanceDemographicObjectMother.aDefaultProvenanceDemographic())
				.withProjectionSteps(Collections.emptyList());
	}
}