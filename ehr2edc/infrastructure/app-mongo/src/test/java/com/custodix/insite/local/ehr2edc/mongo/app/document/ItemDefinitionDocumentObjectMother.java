package com.custodix.insite.local.ehr2edc.mongo.app.document;

import static java.util.Collections.singletonList;

public class ItemDefinitionDocumentObjectMother {

	public static ItemDefinitionDocument aDefaultItemDefinitionDocument() {
		return aDefaultItemDefinitionDocumentBuilder().build();
	}

	public static ItemDefinitionDocument.Builder aDefaultItemDefinitionDocumentBuilder() {
		return ItemDefinitionDocument.newBuilder()
				.withId("ItemDefinitionSnapshot.id-123")
				.withCodeList(CodeListDocument.newBuilder().withId("CodeListSnapshot.id-123").build())
				.withDataType("dataType")
				.withLength(123)
				.withMeasurementUnits(singletonList(MeasurementUnitDocumentObjectMother.aDefaultMeasurementUnitDocument()));
	}
}