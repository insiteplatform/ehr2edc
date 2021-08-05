package com.custodix.insite.local.ehr2edc.vocabulary

class SubjectIdSpec extends AbstractValidatorSpecification<SubjectId> {

    def "Building a SubjectId with an invalid Id"() {
        when: "A SubjectId is built with given parameters"
        SubjectId subjectId = SubjectId.of(id)

        then: "Expect a Constraint Violation"
        validate(subjectId, "id", message)

        where: "The input is invalid"
        id        | message
        null      | "must not be blank"
        ""        | "size must be between 1 and 200"
        'a' * 201 | "size must be between 1 and 200"
    }

    def "Building a SubjectId with a valid Id"() {
        when: "A SubjectId is built with given parameters"
        SubjectId id = SubjectId.of("valid_1")

        then: "Expect a DomainException"
        id == SubjectId.of("valid_1")
    }
}
