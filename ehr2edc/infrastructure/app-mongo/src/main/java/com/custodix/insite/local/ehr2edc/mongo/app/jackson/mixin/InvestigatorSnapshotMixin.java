package com.custodix.insite.local.ehr2edc.mongo.app.jackson.mixin;

import com.custodix.insite.local.ehr2edc.snapshots.InvestigatorSnapshot;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = InvestigatorSnapshot.Builder.class)
interface InvestigatorSnapshotMixin {
}
