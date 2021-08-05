package com.custodix.insite.local.ehr2edc.ehr.mongo.command

import com.custodix.insite.local.ehr2edc.ehr.domain.event.EHRSubjectRegistrationStatus
import com.custodix.insite.local.ehr2edc.ehr.domain.event.EHRSubjectRegistrationStatusUpdated
import com.custodix.insite.local.ehr2edc.vocabulary.PatientCDWReference
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId
import org.springframework.beans.factory.annotation.Autowired

import javax.validation.ConstraintViolationException

import static com.custodix.insite.local.ehr2edc.ehr.mongo.command.FailSubjectRegistrationRequestObjectMother.aDefaultFailSubjectRegistrationRequestBuilder

class FailSubjectRegistrationSpec extends AbstractEHRMongoSpec {

    public static final String PATIENT_ID = "patientId-456"
    public static final String PATIENT_ID_SOURCE = "patientIdSource"
    public static final String SUBJECT_ID = "subjectId-123"

    @Autowired
    FailSubjectRegistration failSubjectRegistration

    def "When a subject Registration fails an EHRSubjectRegistrationUpdated event is published "() {
        given: "a request "
        def request = FailSubjectRegistration.Request.newBuilder()
                .withPatientCDWReference(PatientCDWReference.newBuilder().withId(PATIENT_ID).withSource(PATIENT_ID_SOURCE).build())
                .withSubjectId(SubjectId.of(SUBJECT_ID))
                .build()

        when: "executing fail subject registration"
        failSubjectRegistration.fail(request)

        then: "an EHRSubjectRegistrationStatusUpdated is published correctly"
        def ehrSubjectRegistrationUpdated = testEventPublisher.poll() as EHRSubjectRegistrationStatusUpdated
        ehrSubjectRegistrationUpdated.subjectId == SUBJECT_ID
        ehrSubjectRegistrationUpdated.namespace == PATIENT_ID_SOURCE
        ehrSubjectRegistrationUpdated.patientId == PATIENT_ID
        ehrSubjectRegistrationUpdated.status == EHRSubjectRegistrationStatus.FAILED
    }

    def " FailSubjectRegistration fails when request is null"() {
        given: "a null request "
        def request = null

        when: "executing fail subject registration"
        failSubjectRegistration.fail(request)

        then: "return an validation error that request cannot be null"
        def ex = thrown(ConstraintViolationException)
        ex.constraintViolations.first().propertyPath.toString() == "fail.arg0"
        ex.constraintViolations.first().message == "must not be null"
    }

    def " FailSubjectRegistration fails when patient reference request is null"() {
        given: "a request with patient reference null"
        def request = aDefaultFailSubjectRegistrationRequestBuilder()
            .withPatientCDWReference(null)
            .build()

        when: "executing fail subject registration"
        failSubjectRegistration.fail(request)

        then: "return an validation error that patient reference request cannot be null"
        def ex = thrown(ConstraintViolationException)
        ex.constraintViolations.first().propertyPath.toString() == "fail.arg0.patientCDWReference"
        ex.constraintViolations.first().message == "must not be null"
    }

    def " FailSubjectRegistration fails when subject id  request is null"() {
        given: "a request with subject id null"
        def request = aDefaultFailSubjectRegistrationRequestBuilder()
                .withSubjectId(null)
                .build()

        when: "executing fail subject registration"
        failSubjectRegistration.fail(request)

        then: "return an validation error that subject id  request cannot be null"
        def ex = thrown(ConstraintViolationException)
        ex.constraintViolations.first().propertyPath.toString() == "fail.arg0.subjectId"
        ex.constraintViolations.first().message == "must not be null"
    }

}