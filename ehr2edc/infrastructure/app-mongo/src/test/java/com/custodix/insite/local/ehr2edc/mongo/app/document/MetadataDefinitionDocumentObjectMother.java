package com.custodix.insite.local.ehr2edc.mongo.app.document;

import static com.custodix.insite.local.ehr2edc.mongo.app.document.EventDefinitionDocumentObjectMother.aDefaultEventDefinitionDocument;
import static java.util.Collections.singletonList;

public class MetadataDefinitionDocumentObjectMother {

	public static MetaDataDefinitionDocument aDefaultMetaDataDefinitionDocument() {
		return aDefaultMetaDataDefinitionDocumentBuilder().build();
	}

	public static MetaDataDefinitionDocument.Builder aDefaultMetaDataDefinitionDocumentBuilder() {
		return MetaDataDefinitionDocument.newBuilder()
				.withEventDefinitions(singletonList(aDefaultEventDefinitionDocument()));
	}
}
