package com.custodix.insite.local.ehr2edc.mongo.app.document;

import static com.custodix.insite.local.ehr2edc.mongo.app.document.FormDefinitionDocumentObjectMother.aDefaultFormDefinitionDocument;
import static java.util.Collections.singletonList;

public class EventDefinitionDocumentObjectMother {

	public static EventDefinitionDocument aDefaultEventDefinitionDocument() {
		return aDefaultEventDefinitionDocumentBuilder().build();
	}

	public static EventDefinitionDocument.Builder aDefaultEventDefinitionDocumentBuilder() {
		return EventDefinitionDocument.newBuilder()
				.withId("eventDefinitionId-123")
				.withName("event-name-123")
				.withFormDefinitions(singletonList(aDefaultFormDefinitionDocument()));
	}
}
