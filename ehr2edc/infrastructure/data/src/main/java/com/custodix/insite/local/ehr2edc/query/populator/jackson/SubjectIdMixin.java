package com.custodix.insite.local.ehr2edc.query.populator.jackson;

import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = SubjectId.Builder.class)
interface SubjectIdMixin {

}
