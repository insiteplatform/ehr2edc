package com.custodix.insite.local.ehr2edc.populator;

import java.time.LocalDate;

import static com.custodix.insite.local.ehr2edc.ItemQueryMappingsObjectMother.aDefaultItemQueryMappings;
import static com.custodix.insite.local.ehr2edc.metadata.model.EventDefinitionObjectMother.aDefaultEventDefinition;
import static com.custodix.insite.local.ehr2edc.vocabulary.EDCSubjectReferenceObjectMother.aRandomEdcSubjectReference;
import static com.custodix.insite.local.ehr2edc.vocabulary.PatientCDWReferenceObjectMother.aDefaultPatientCDWReference;
import static com.custodix.insite.local.ehr2edc.vocabulary.StudyIdObjectMother.aRandomStudyId;
import static com.custodix.insite.local.ehr2edc.vocabulary.UserIdentifierObjectMother.aDefaultUserIdentifier;

@SuppressWarnings("PMD.TooManyStaticImports")
public final class PopulationSpecificationObjectMother {

    public static PopulationSpecification aDefaultPopulationSpecification() {
        return aDefaultPopulationSpecificationBuilder()
                .build();
    }

    public static PopulationSpecification.Builder aDefaultPopulationSpecificationBuilder() {
        return PopulationSpecification.newBuilder()
                .withConsentDate(LocalDate.now())
                .withEdcSubjectReference(aRandomEdcSubjectReference())
                .withPopulator(aDefaultUserIdentifier())
                .withEventDefinition(aDefaultEventDefinition())
                .withPatientCDWReference(aDefaultPatientCDWReference())
                .withStudyId(aRandomStudyId())
                .withReferenceDate(LocalDate.now())
                .withStudyItemQueryMappings(aDefaultItemQueryMappings());
    }
}