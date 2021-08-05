package com.custodix.insite.local.ehr2edc.mongo.app.jackson.mixin;

import com.custodix.insite.local.ehr2edc.vocabulary.FormDefinitionId;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = FormDefinitionId.Builder.class)
interface FormDefintionIdMixin {

}
