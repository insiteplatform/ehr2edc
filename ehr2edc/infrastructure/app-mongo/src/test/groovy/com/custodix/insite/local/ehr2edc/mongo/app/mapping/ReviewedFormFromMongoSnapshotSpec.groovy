package com.custodix.insite.local.ehr2edc.mongo.app.mapping

import com.custodix.insite.local.ehr2edc.mongo.app.review.ReviewedFormMongoSnapshotObjectMother
import com.custodix.insite.local.ehr2edc.vocabulary.*
import spock.lang.Specification
import spock.lang.Unroll

class ReviewedFormFromMongoSnapshotSpec extends Specification {
    @Unroll
    def "Converting reviewed form mongo snapshot with name '#name', form definition id '#formDefinitionId', populated form id '#populatedFormId', localLab '#localLab' correctly"() {
        given: "a reviewed form mongo snapshot with name '#name', form definition id '#formDefinitionId', populated form id '#populatedFormId', localLab '#localLab'"
        def reviewedFormMongoSnapshot = ReviewedFormMongoSnapshotObjectMother.aDefaultReviewedFormMongoSnapshotBuilder()
                .withName(name)
                .withFormDefinitionId(formDefinitionId)
                .withPopulatedFormId(populatedFormId)
                .withLocalLab(localLab)
                .build()

        when: "converting to reviewed form"
        def reviewedForm = reviewedFormMongoSnapshot.toReviewedForm()

        then: "name is converted to '#name'"
        reviewedForm.getName() == name
        and: "form definition ID is converted to '#formDefinitionId'"
        reviewedForm.getFormDefinitionId() == FormDefinitionId.of(formDefinitionId)
        and: "populated form Id is converted to '#populatedFormId'"
        reviewedForm.getPopulatedFormId() == FormId.of(populatedFormId)
        and: "local lab is converted to '#expectedLocalLab'"
        reviewedForm.getLocalLab() == expectedLocalLab

        where:
        name        | formDefinitionId     | populatedFormId     | localLab || expectedLocalLab
        "123-12309" | "form definition ID" | "populated form ID" | "LAB_1"  || Optional.of(LabName.of("LAB_1"))
        "123-12309" | "form definition ID" | "populated form ID" | null     || Optional.empty()
    }
}
