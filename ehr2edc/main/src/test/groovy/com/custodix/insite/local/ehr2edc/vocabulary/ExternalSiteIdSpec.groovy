package com.custodix.insite.local.ehr2edc.vocabulary

class ExternalSiteIdSpec extends AbstractValidatorSpecification<ExternalSiteId>{

    def "Building a SiteReference with an invalid value"() {
        when: "A ExternalSiteId is built with given parameters"
        ExternalSiteId siteReference = ExternalSiteId.of(id)

        then: "Expect a Constraint Violation"
        validate(siteReference, "id", message)

        where: "The input is invalid"
        id       | message
        null     | "must not be blank"
        ""       | "must not be blank"
        "   "    | "must not be blank"
    }

    def "Building a SiteReference with a valid value"() {
        given: "Valid parameters"
        def param = "valid_1"

        when: "A ExternalSiteId is built with given parameters"
        ExternalSiteId id = ExternalSiteId.of(param)

        then: "Expect a ExternalSiteId to be created"
        id == ExternalSiteId.of("valid_1")
    }
}
