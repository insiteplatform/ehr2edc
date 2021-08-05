package com.custodix.insite.local.ehr2edc.mongo.app.document;

public class ItemMongoSnapshotObjectMother {

	public static ItemDocument aDefaultItemMongoSnapshot() {
		return aDefaultItemMongoSnapshotBuilder().build();
	}
	public static ItemDocument.Builder aDefaultItemMongoSnapshotBuilder() {
		return ItemDocument.newBuilder()
				.withId("123-123")
				.withValue("aValue");
	}
}