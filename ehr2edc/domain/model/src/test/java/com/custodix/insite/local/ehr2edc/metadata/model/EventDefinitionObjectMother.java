package com.custodix.insite.local.ehr2edc.metadata.model;


import com.custodix.insite.local.ehr2edc.vocabulary.EventDefinitionId;

import static java.util.Collections.emptyList;

public final class EventDefinitionObjectMother {

    public static EventDefinition aDefaultEventDefinition() {
        return aDefaultEventDefinitionBuilder()
                .build();
    }

    public static EventDefinition.Builder aDefaultEventDefinitionBuilder() {
        return EventDefinition.newBuilder()
                .withFormDefinitions(emptyList())
                .withId(EventDefinitionId.of("defaultEventDefinitionId"))
                .withName("defaultName")
                .withParentId("defaultParentId");
    }

}