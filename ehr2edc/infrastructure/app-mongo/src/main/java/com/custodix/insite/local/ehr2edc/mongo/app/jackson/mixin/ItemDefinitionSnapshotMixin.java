package com.custodix.insite.local.ehr2edc.mongo.app.jackson.mixin;

import com.custodix.insite.local.ehr2edc.snapshots.ItemDefinitionSnapshot;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = ItemDefinitionSnapshot.Builder.class)
interface ItemDefinitionSnapshotMixin {
}
