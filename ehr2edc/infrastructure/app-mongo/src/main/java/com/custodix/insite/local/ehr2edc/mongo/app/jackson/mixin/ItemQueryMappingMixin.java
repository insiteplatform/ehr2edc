package com.custodix.insite.local.ehr2edc.mongo.app.jackson.mixin;

import com.custodix.insite.local.ehr2edc.querymapping.model.ItemQueryMapping;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = ItemQueryMapping.Builder.class)
interface ItemQueryMappingMixin {
}
