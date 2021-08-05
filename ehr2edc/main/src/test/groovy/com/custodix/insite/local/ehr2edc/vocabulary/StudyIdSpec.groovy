package com.custodix.insite.local.ehr2edc.vocabulary

class StudyIdSpec extends AbstractValidatorSpecification<StudyId> {

    def "Building a StudyId with an invalid Id"() {
        when: "A StudyId is built with given parameters"
        StudyId studyId = StudyId.of(id)

        then: "Expect a Constraint Violation"
        validate(studyId, "id", message)

        where: "The input is invalid"
        id       | message
        null     | "must not be blank"
        ""       | "must not be blank"
        ""       | "size must be between 1 and 50"
        "   "    | "must not be blank"
        'a' * 51 | "size must be between 1 and 50"
    }

    def "Building a StudyId with a valid Id"() {
        given: "Valid parameters"
        def param = "valid_1"

        when: "A StudyId is built with given parameters"
        StudyId id = StudyId.of(param)

        then: "Expect a StudyId to be created"
        id == StudyId.of("valid_1")
    }
}
