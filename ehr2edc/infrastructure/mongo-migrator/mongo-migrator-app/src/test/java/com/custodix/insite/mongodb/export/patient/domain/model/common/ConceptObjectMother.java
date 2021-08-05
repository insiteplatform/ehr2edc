package com.custodix.insite.mongodb.export.patient.domain.model.common;

public class ConceptObjectMother {

    public static Concept aDefaultConcept(){
        return Concept.newBuilder()
                .withCode("defaultCode")
                .withSchema("defaultSchema")
                .build();
    }
}