package com.custodix.insite.mongodb.export.patient.domain.model;

import static com.custodix.insite.mongodb.export.patient.domain.model.common.ConceptObjectMother.aDefaultConcept;
import static com.custodix.insite.mongodb.export.patient.domain.model.common.ConceptPathObjectMother.aDefaultConceptPath;
import static com.custodix.insite.mongodb.export.patient.domain.model.common.ObservationObjectMother.aDefaultObservation;
import static com.custodix.insite.mongodb.vocabulary.PatientIdentifierObjectMother.aDefaultPatientIdentifier;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;

import com.custodix.insite.mongodb.export.patient.domain.model.common.ModifierObjectMother;

public class ClinicalFindingFactObjectMother {


    private static final Instant EFFECTIVE_DATE = LocalDateTime.now().toInstant(ZoneOffset.UTC);

    public static final ClinicalFindingFact aDefaultClinicalFindingFact() {
        return ClinicalFindingFact.newBuilder()
                .withPatientIdentifier(aDefaultPatientIdentifier())
                .withEffectiveDate(EFFECTIVE_DATE)
                .withLabel("defaultLabel")
                .withValueObservation(aDefaultObservation())
                .withUlnObservation(aDefaultObservation())
                .withLlnObservation(aDefaultObservation())
                .withLocalConcept(aDefaultConcept())
                .withReferenceConcept(aDefaultConcept())
                .withConceptPaths(Arrays.asList(aDefaultConceptPath()))
                .withLaterality(ModifierObjectMother.laterality())
                .withPosition(ModifierObjectMother.position())
                .withLocation(ModifierObjectMother.location())
                .build();
    }

}