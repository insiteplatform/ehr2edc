package com.custodix.insite.local.ehr2edc.vocabulary

class PatientCDWReferenceSpec extends AbstractValidatorSpecification<PatientCDWReference> {

    def "Creation of a PatientId with an invalid source"() {
        when: "A PatientId is built with given parameters"
        PatientCDWReference patientId = PatientCDWReference.newBuilder().withSource(src).withId("VALID_ID").build()

        then: "Expect a PatientId"
        validate(patientId, "source", message)

        where: "The input is invalid"
        src      | message
        null     | "must not be null"
        ""       | "size must be between 1 and 50"
        'a' * 51 | "size must be between 1 and 50"
    }

    def "Creation of a PatientId with an invalid id"() {
        when: "A PatientId is built with given parameters"
        PatientCDWReference patientId = PatientCDWReference.newBuilder().withSource("VALID_SRC").withId(id).build()

        then: "Expect a PatientId"
        validate(patientId, "id", message)

        where: "The input is invalid"
        id        | message
        null      | "must not be null"
        ""        | "size must be between 1 and 200"
        'a' * 201 | "size must be between 1 and 200"
    }

    def "Creation of a PatientId from valid parameters"() {
        when: "A PatientId is built with given parameters"
        PatientCDWReference out = PatientCDWReference.newBuilder().withId(id).withSource(src).build()

        then: "Expect a PatientId"
        out != null
        out.id == id
        out.source == src

        where: "The input is invalid"
        id         | src
        "VALID_ID" | "VALID_SRC"
    }


}
