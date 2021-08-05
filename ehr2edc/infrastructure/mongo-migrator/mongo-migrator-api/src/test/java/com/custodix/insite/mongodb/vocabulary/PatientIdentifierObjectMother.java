package com.custodix.insite.mongodb.vocabulary;

import static com.custodix.insite.mongodb.vocabulary.NamespaceObjectMother.aDefaultNamespace;
import static com.custodix.insite.mongodb.vocabulary.PatientIdObjectMother.aDefaultPatientId;
import static com.custodix.insite.mongodb.vocabulary.SubjectIdObjectMother.aDefaultSubjectId;

public class PatientIdentifierObjectMother {

    public static PatientIdentifier aDefaultPatientIdentifier(){
        return aDefaultPatientIdentifierBuilder()
                .build();
    }

    public static PatientIdentifier.Builder aDefaultPatientIdentifierBuilder() {
        return PatientIdentifier.newBuilder()
                .withNamespace(aDefaultNamespace())
                .withPatientId(aDefaultPatientId())
                .withSubjectId(aDefaultSubjectId());
    }
}
