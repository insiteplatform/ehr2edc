package com.custodix.insite.local.ehr2edc.metadata.model;

import static com.custodix.insite.local.ehr2edc.metadata.model.MeasurementUnitObjectMother.aDefaultMeasurementUnit;
import static java.util.Collections.singletonList;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.custodix.insite.local.ehr2edc.vocabulary.ItemDefinitionId;

public class ItemDefinitionObjectMother {

	public static ItemDefinition aDefaultItem() {
		return aDefaultItemBuilder().build();
	}

	public static ItemDefinition.Builder aDefaultItemBuilder() {
		return ItemDefinition.newBuilder()
				.withId(ItemDefinitionId.of("123-123"))
				.withCodeList(CodeList.newBuilder()
						.withId("aCodeListId")
						.build())
				.withDataType("aDataType")
				.withLength(10)
				.withMeasurementUnits(singletonList(aDefaultMeasurementUnit()))
				.withLabel(aDefaultItemLabel());
	}

	private static ItemLabel aDefaultItemLabel() {
		Map<Locale, String> translatedText = new HashMap<>();
		translatedText.put(Locale.ENGLISH, "Item label");
		return ItemLabel.newBuilder()
				.withName("Item")
				.withQuestion(new Question(translatedText))
				.build();
	}
}