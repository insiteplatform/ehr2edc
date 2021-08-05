package com.custodix.insite.local.ehr2edc.mongo.app.jackson.mixin;

import com.custodix.insite.local.ehr2edc.vocabulary.ExternalEDCConnection;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = ExternalEDCConnection.Builder.class)
interface ExternalEDCConnectionMixin {

}
