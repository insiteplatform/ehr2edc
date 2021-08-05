package com.custodix.insite.local.ehr2edc.mongo.app.jackson.mixin;

import com.custodix.insite.local.ehr2edc.snapshots.MetaDataDefinitionSnapshot;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = MetaDataDefinitionSnapshot.Builder.class)
interface MetaDataDefinitionSnapshotMixin {
}
