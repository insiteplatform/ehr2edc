package com.custodix.insite.local.ehr2edc.mongo.app.document;

import static com.custodix.insite.local.ehr2edc.mongo.app.document.ItemGroupDefinitionDocumentObjectMother.aDefaultItemGroupDefinitionDocument;

import java.util.Collections;

public class FormDefinitionDocumentObjectMother {
	public static FormDefinitionDocument aDefaultFormDefinitionDocument() {
		return aDefaultFormDefinitionDocumentBuilder().build();
	}

	public static FormDefinitionDocument.Builder aDefaultFormDefinitionDocumentBuilder() {
		return FormDefinitionDocument.newBuilder()
				.withId("123-123")
				.withName("Form name")
				.withItemGroupDefinitions(Collections.singletonList(aDefaultItemGroupDefinitionDocument()))
				.withLocalLab("LAB_1");
	}
}
