package com.custodix.insite.local.ehr2edc.vocabulary

import spock.lang.Unroll

class EventDefinitionIdSpec extends AbstractValidatorSpecification<EventDefinitionId> {

    def "Asserting equality between events"() {
        expect: "EventDefinitionId to equal itself"
        left == left
        left.hashCode() == left.hashCode()
        and: "EventDefinitionId to equal another EventDefinitionId with same id-value"
        left == right
        left.hashCode() == right.hashCode()
        and: "EventDefinitionId to not equal null"
        left != other
        and: "EventDefinitionId to not equal another type"
        left != StudyId.of("study")
        and: "EventDefinitionId to not equal another EventDefinitionId with different id-value"
        left != other
        left.hashCode() != other.hashCode()

        where:
        left = EventDefinitionId.of("id")
        right = EventDefinitionId.of("id")
        other = EventDefinitionId.of("other")
    }

    @Unroll
    def "Building a EventDefinitionId with an invalid id"() {
        when: "A EventDefinitionId is built with id: '#id'"
        EventDefinitionId eventDefinitionId = EventDefinitionId.of(id)

        then: "Expect a ConstraintViolation with message: #message"
        validate(eventDefinitionId, "id", message)

        where:
        id      | message
        null    | "must not be null"
        ""      | "must not be blank"
        "     " | "must not be blank"
    }
}
