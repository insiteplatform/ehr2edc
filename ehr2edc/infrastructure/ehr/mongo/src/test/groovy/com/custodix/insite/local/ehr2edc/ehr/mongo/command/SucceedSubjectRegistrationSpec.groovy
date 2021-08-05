package com.custodix.insite.local.ehr2edc.ehr.mongo.command

import com.custodix.insite.local.ehr2edc.ehr.domain.event.EHRSubjectRegistrationStatus
import com.custodix.insite.local.ehr2edc.ehr.domain.event.EHRSubjectRegistrationStatusUpdated
import com.custodix.insite.local.ehr2edc.vocabulary.PatientCDWReference
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId
import org.springframework.beans.factory.annotation.Autowired

import javax.validation.ConstraintViolationException

import static SucceedSubjectRegistrationRequestObjectMother.aDefaultSucceedSubjectRegistrationRequestBuilder

class SucceedSubjectRegistrationSpec extends AbstractEHRMongoSpec {

    public static final String PATIENT_ID = "patientId-456"
    public static final String PATIENT_ID_SOURCE = "patientIdSource"
    public static final String SUBJECT_ID = "subjectId-123"

    @Autowired
    SucceedSubjectRegistration succeedSubjectRegistration

    def "When a EHR subject Registration succeed an EHRSubjectRegistrationStatusUpdated event is published "() {
        given: "a request "
        def request = SucceedSubjectRegistration.Request.newBuilder()
                .withPatientCDWReference(PatientCDWReference.newBuilder().withId(PATIENT_ID).withSource(PATIENT_ID_SOURCE).build())
                .withSubjectId(SubjectId.of(SUBJECT_ID))
                .build()

        when: "executing succeed ehr registration"
        succeedSubjectRegistration.succeed(request)

        then: "an EHRSubjectRegistrationStatusUpdated is published correctly"
        def eHRSubjectRegistrationStatusUpdated = testEventPublisher.poll() as EHRSubjectRegistrationStatusUpdated
        eHRSubjectRegistrationStatusUpdated.subjectId == SUBJECT_ID
        eHRSubjectRegistrationStatusUpdated.namespace == PATIENT_ID_SOURCE
        eHRSubjectRegistrationStatusUpdated.patientId == PATIENT_ID
        eHRSubjectRegistrationStatusUpdated.status == EHRSubjectRegistrationStatus.REGISTERED
    }

    def "SucceedSubjectRegistration fails when request is null"() {
        given: "a null request "
        def request = null

        when: "executing succeed ehr registration"
        succeedSubjectRegistration.succeed(request)

        then: "return an validation error that request cannot be null"
        def ex = thrown(ConstraintViolationException)
        ex.constraintViolations.first().propertyPath.toString() == "succeed.arg0"
        ex.constraintViolations.first().message == "must not be null"
    }

    def "SucceedSubjectRegistration fails when patient reference request is null"() {
        given: "a request with patient reference null"
        def request = aDefaultSucceedSubjectRegistrationRequestBuilder()
            .withPatientCDWReference(null)
            .build()

        when: "executing succeed ehr registration"
        succeedSubjectRegistration.succeed(request)

        then: "return an validation error that patient reference request cannot be null"
        def ex = thrown(ConstraintViolationException)
        ex.constraintViolations.first().propertyPath.toString() == "succeed.arg0.patientCDWReference"
        ex.constraintViolations.first().message == "must not be null"
    }

    def "SucceedSubjectRegistration fails when subject id  request is null"() {
        given: "a request with subject id null"
        def request = aDefaultSucceedSubjectRegistrationRequestBuilder()
                .withSubjectId(null)
                .build()

        when: "executing succeed ehr registration"
        succeedSubjectRegistration.succeed(request)

        then: "return an validation error that subject id  request cannot be null"
        def ex = thrown(ConstraintViolationException)
        ex.constraintViolations.first().propertyPath.toString() == "succeed.arg0.subjectId"
        ex.constraintViolations.first().message == "must not be null"
    }

}