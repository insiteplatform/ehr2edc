package com.custodix.insite.local.ehr2edc;

import static java.util.Collections.emptyMap;

public final class ItemQueryMappingsObjectMother {

    public static ItemQueryMappings aDefaultItemQueryMappings() {
        return aDefaultEventDefinitionBuilder()
                .build();
    }

    public static ItemQueryMappings.Builder aDefaultEventDefinitionBuilder() {
        return ItemQueryMappings.newBuilder()
                .withItemQueryMappings(emptyMap());
    }

}