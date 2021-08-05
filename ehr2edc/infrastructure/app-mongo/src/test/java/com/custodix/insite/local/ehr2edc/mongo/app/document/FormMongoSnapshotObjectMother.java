package com.custodix.insite.local.ehr2edc.mongo.app.document;

import static com.custodix.insite.local.ehr2edc.mongo.app.document.ItemGroupMongoSnapshotObjectMother.aDefaultItemGroupMongoSnapshot;

import java.util.Collections;

public class FormMongoSnapshotObjectMother {

	public static FormDocument aDefaultFormMongoSnapshot() {
		return aDefaultFormMongoSnapshotBuilder().build();
	}

	public static FormDocument.Builder aDefaultFormMongoSnapshotBuilder() {
		return FormDocument.newBuilder()
				.withInstanceId("instanceId-123")
				.withFormDefinitionId("123-123")
				.withName("Form name")
				.withItemGroups(Collections.singletonList(aDefaultItemGroupMongoSnapshot()));
	}

}