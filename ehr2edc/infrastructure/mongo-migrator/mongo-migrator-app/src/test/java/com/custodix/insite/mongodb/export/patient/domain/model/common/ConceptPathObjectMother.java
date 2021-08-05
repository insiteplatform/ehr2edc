package com.custodix.insite.mongodb.export.patient.domain.model.common;

public class ConceptPathObjectMother {

    public static ConceptPath aDefaultConceptPath() {
        return ConceptPath.newBuilder()
                .withPath("/defaultSchema_defaultCode/")
                .build();
    }

}