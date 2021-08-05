package com.custodix.insite.local.ehr2edc.vocabulary;

import java.util.UUID;

public final class PatientCDWReferenceObjectMother {

    public static PatientCDWReference aDefaultPatientCDWReference(){
        return aDefaultPatientCDWReferenceBuilder()
                .build();
    }

    public static PatientCDWReference.Builder aDefaultPatientCDWReferenceBuilder() {
        return PatientCDWReference.newBuilder()
                .withSource("defaultSource")
                .withId("defaultId");
    }

    public static PatientCDWReference aRandomPatientCDWReference() {
        return PatientCDWReference.newBuilder()
                .withSource("source-" + UUID.randomUUID().toString())
                .withId("id-" + UUID.randomUUID().toString())
                .build();
    }
}