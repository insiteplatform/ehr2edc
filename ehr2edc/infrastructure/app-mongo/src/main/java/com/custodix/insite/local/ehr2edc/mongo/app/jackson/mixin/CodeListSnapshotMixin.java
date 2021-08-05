package com.custodix.insite.local.ehr2edc.mongo.app.jackson.mixin;

import com.custodix.insite.local.ehr2edc.snapshots.CodeListSnapshot;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = CodeListSnapshot.Builder.class)
interface CodeListSnapshotMixin {
}
