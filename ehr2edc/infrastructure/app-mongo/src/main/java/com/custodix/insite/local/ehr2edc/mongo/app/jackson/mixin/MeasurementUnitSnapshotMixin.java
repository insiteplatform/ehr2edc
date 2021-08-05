package com.custodix.insite.local.ehr2edc.mongo.app.jackson.mixin;

import com.custodix.insite.local.ehr2edc.snapshots.MeasurementUnitSnapshot;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = MeasurementUnitSnapshot.Builder.class)
interface MeasurementUnitSnapshotMixin {
}
