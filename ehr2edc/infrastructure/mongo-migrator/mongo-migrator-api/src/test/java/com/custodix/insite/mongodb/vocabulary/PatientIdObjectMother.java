package com.custodix.insite.mongodb.vocabulary;

import java.util.UUID;

public class PatientIdObjectMother {

    public static PatientId aDefaultPatientId() {
        return PatientId.of(UUID.randomUUID().toString());
    }
}
