package com.custodix.insite.local.ehr2edc.mongo.app.document;

import static com.custodix.insite.local.ehr2edc.mongo.app.document.ItemMongoSnapshotObjectMother.aDefaultItemMongoSnapshot;

import java.util.Collections;

public class ItemGroupMongoSnapshotObjectMother {

	public static ItemGroupDocument aDefaultItemGroupMongoSnapshot() {
		return aDefaultItemGroupMongoSnapshotBuilder().build();
	}

	public static ItemGroupDocument.Builder aDefaultItemGroupMongoSnapshotBuilder() {
		return ItemGroupDocument.newBuilder()
				.withDefinition(aDefaultDefinition())
				.withItems(Collections.singletonList(aDefaultItemMongoSnapshot()));
	}

	private static ItemGroupDocument.DefinitionMongoSnapshot aDefaultDefinition() {
		return ItemGroupDocument.DefinitionMongoSnapshot.newBuilder()
				.withId("123-123")
				.build();
	}

}