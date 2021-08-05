package com.custodix.insite.local.ehr2edc.snapshots;

public class MeasurementUnitSnapshotObjectMother {

	public static MeasurementUnitSnapshot aDefaultMeasurementUnitSnapshot() {
		return aDefaultMeasurementUnitSnapshotBuilder().build();
	}

	private static MeasurementUnitSnapshot.Builder aDefaultMeasurementUnitSnapshotBuilder() {
		return MeasurementUnitSnapshot.newBuilder()
				.withId("MeasurementUnitSnapshot.id")
				.withName("MeasurementUnitSnapshot.name");
	}
}
