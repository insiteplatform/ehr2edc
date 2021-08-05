package com.custodix.insite.local.ehr2edc.submitted

import com.custodix.insite.local.ehr2edc.vocabulary.SubmittedEventId
import spock.lang.Specification

class SubmissionContextTest extends Specification {

    def "Creation of SubmissionContext set the submission id with the value of submitted id"() {
        given: "Submitted event id"
        def submittedEventId = SubmittedEventId.of("submittedEventId-098")

        when: "Creating submissionContext"
        def submissionContext = SubmissionContextObjectMother.aDefaultSubmissionContextBuilder().withSubmittedEventId(submittedEventId).build()

        then: "submitted event id and submission context id value are the same"
        submissionContext.id.id == submittedEventId.id
    }
}