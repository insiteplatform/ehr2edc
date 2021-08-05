package com.custodix.insite.local.ehr2edc.mongo.app.document;

import static com.custodix.insite.local.ehr2edc.mongo.app.document.ItemDefinitionDocumentObjectMother.aDefaultItemDefinitionDocument;
import static java.util.Collections.singletonList;

public class ItemGroupDefinitionDocumentObjectMother {

	public static ItemGroupDefinitionDocument aDefaultItemGroupDefinitionDocument() {
		return aDefaultItemGroupDefinitionBuilder().build();
	}

	public static ItemGroupDefinitionDocument.Builder aDefaultItemGroupDefinitionBuilder() {
		return ItemGroupDefinitionDocument.newBuilder()
				.withId("itemGroupDefinition-id-123")
				.withName("itemGroup-name-1")
				.withItemDefinitions(singletonList(aDefaultItemDefinitionDocument()))
				.withRepeating(false);
	}

}