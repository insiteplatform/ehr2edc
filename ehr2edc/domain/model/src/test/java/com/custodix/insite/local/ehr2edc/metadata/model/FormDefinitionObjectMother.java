package com.custodix.insite.local.ehr2edc.metadata.model;

import static com.custodix.insite.local.ehr2edc.metadata.model.ItemGroupDefinitionObjectMother.aDefaultItemGroupDefinition;

import java.util.Collections;

import com.custodix.insite.local.ehr2edc.vocabulary.FormDefinitionId;

public class FormDefinitionObjectMother {

	public static FormDefinition aDefaultFormDefinition() {
		return aDefaultFormDefinitionBuilder().build();
	}

	public static FormDefinition.Builder aDefaultFormDefinitionBuilder() {
		return FormDefinition.newBuilder()
				.withId(FormDefinitionId.of("123-123"))
				.withItemGroupDefinitions(Collections.singletonList(aDefaultItemGroupDefinition()));
	}
}