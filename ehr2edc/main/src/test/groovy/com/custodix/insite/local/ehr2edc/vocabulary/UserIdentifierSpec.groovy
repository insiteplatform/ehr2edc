package com.custodix.insite.local.ehr2edc.vocabulary

class UserIdentifierSpec extends AbstractValidatorSpecification<UserIdentifier> {

    def "Building a User Id without Id"() {
        when:
        UserIdentifier userId = UserIdentifier.of(null)

        then: "Expect a Constraint Violation"
        validate(userId, "id", "must not be null")
    }

}
