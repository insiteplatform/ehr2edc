package com.custodix.insite.local.ehr2edc.mongo.app.mapping

import com.custodix.insite.local.ehr2edc.mongo.app.review.ReviewedFormMongoSnapshot
import com.custodix.insite.local.ehr2edc.mongo.app.review.provenance.ReviewedProvenanceDataPointDocumentFactory
import com.custodix.insite.local.ehr2edc.submitted.SubmittedFormObjectMother
import com.custodix.insite.local.ehr2edc.vocabulary.FormDefinitionId
import com.custodix.insite.local.ehr2edc.vocabulary.FormId
import com.custodix.insite.local.ehr2edc.vocabulary.LabName
import spock.lang.Specification
import spock.lang.Unroll

class ReviewedFormToMongoSnapshotSpec extends Specification {

    @Unroll
    def "Converting reviewed form with name '#name', form definition id '#formDefinitionId', populated form id '#populatedFormId', localLab '#localLab' correctly"() {
        given: "a reviewed form with name '#name', form definition id '#formDefinitionId', populated form id '#populatedFormId', localLab '#localLab'"
        def reviewedForm = SubmittedFormObjectMother.aDefaultSubmittedFormBuilder()
                .withName(name)
                .withFormDefinitionId(FormDefinitionId.of(formDefinitionId))
                .withPopulatedFormId(FormId.of(populatedFormId))
                .withLocalLab(localLab)
                .build()

        ReviewedProvenanceDataPointDocumentFactory reviewedProvenanceDataPointDocumentFactory = Mock()

        when: "converting to mongo snapshot"
        def mongoSnapshot = ReviewedFormMongoSnapshot.of(reviewedForm, reviewedProvenanceDataPointDocumentFactory)

        then: "name is converted to '#name'"
        mongoSnapshot.getName() == name
        and: "form definition ID is converted to '#formDefinitionId'"
        mongoSnapshot.getFormDefinitionId() == formDefinitionId
        and: "populated form Id is converted to '#populatedFormId'"
        mongoSnapshot.getPopulatedFormId() == populatedFormId
        and: "local lab is converted to '#expectedLocalLab'"
        mongoSnapshot.getLocalLab() == expectedLocalLab

        where:
        name        | formDefinitionId     | populatedFormId     | localLab            || expectedLocalLab
        "123-12309" | "form definition ID" | "populated form ID" | LabName.of("LAB_1") || "LAB_1"
        "123-12309" | "form definition ID" | "populated form ID" | null                || null
    }
}
