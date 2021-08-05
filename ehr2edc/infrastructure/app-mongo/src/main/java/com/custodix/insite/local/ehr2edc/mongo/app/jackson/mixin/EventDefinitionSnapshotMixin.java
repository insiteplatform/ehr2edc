package com.custodix.insite.local.ehr2edc.mongo.app.jackson.mixin;

import com.custodix.insite.local.ehr2edc.snapshots.EventDefinitionSnapshot;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = EventDefinitionSnapshot.Builder.class)
interface EventDefinitionSnapshotMixin {
}
