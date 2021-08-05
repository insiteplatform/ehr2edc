package com.custodix.insite.local.ehr2edc.vocabulary

import spock.lang.Unroll

class FormDefinitionIdSpec extends AbstractValidatorSpecification<FormDefinitionId> {

    def "Asserting equality between form ids"() {
        expect: "FormDefinitionId to equal itself"
        left == left
        left.hashCode() == left.hashCode()
        and: "FormDefinitionId to equal another FormDefinitionId with same id-value"
        left == right
        left.hashCode() == right.hashCode()
        and: "FormDefinitionId to not equal null"
        left != other
        and: "FormDefinitionId to not equal another type"
        left != StudyId.of("study")
        and: "FormDefinitionId to not equal another EventDefinitionId with different id-value"
        left != other
        left.hashCode() != other.hashCode()

        where:
        left = FormDefinitionId.of("id")
        right = FormDefinitionId.of("id")
        other = FormDefinitionId.of("other")
    }

    @Unroll
    def "Building a FormDefinitionId with an invalid id"() {
        when: "A FormDefinitionId is built with id: '#id'"
        FormDefinitionId formDefinitionId = FormDefinitionId.of(id)

        then: "Expect a ConstraintViolation with message: #message"
        validate(formDefinitionId, "id", message)

        where:
        id      | message
        null    | "must not be null"
        ""      | "must not be blank"
        "     " | "must not be blank"
    }
}
