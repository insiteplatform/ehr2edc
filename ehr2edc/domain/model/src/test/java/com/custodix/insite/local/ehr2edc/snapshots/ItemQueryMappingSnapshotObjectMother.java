package com.custodix.insite.local.ehr2edc.snapshots;

public class ItemQueryMappingSnapshotObjectMother {

	protected static final String VALUE =
			"{\"query\":{\"type\":\"demographic\",\"criteria\":{\"criteria\":[{\"type\":\"subject\",\"subjectId\":{\"id\":\"subjectId-679\"}}]}},"
					+ "\"projectors\":[{\"type\":\"dateOfBirthToAge\",\"unit\":\"YEARS\"},{\"type\":\"gender\"}]}";

	public static ItemQueryMappingSnapshot aDefaultItemQueryMappingSnapshot() {
		return ItemQueryMappingSnapshot.newBuilder().withValue(VALUE).build();
	}
}