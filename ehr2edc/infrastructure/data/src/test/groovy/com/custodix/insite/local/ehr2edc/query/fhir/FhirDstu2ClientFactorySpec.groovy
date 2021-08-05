package com.custodix.insite.local.ehr2edc.query.fhir

import spock.lang.Specification

class FhirDstu2ClientFactorySpec extends Specification {

    private FhirDstu2ClientFactory fhirDstu2ClientFactory = new FhirDstu2ClientFactory(null)

    def "Fhir context is only loaded once"() {
        given: "Getting the fhir context"
        def fhirDstu2ContextFirst = fhirDstu2ClientFactory.getFhirDstu2Context()

        when: "getting the fhir context again"
        def fhirDstu2ContextSecond = fhirDstu2ClientFactory.getFhirDstu2Context()

        then: "fhir context should be the same"
        fhirDstu2ContextFirst.is(fhirDstu2ContextSecond)
    }
}
