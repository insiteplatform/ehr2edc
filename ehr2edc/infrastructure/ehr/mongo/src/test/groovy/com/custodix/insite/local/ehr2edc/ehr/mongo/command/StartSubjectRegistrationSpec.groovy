package com.custodix.insite.local.ehr2edc.ehr.mongo.command

import com.custodix.insite.local.ehr2edc.ehr.domain.event.EHRSubjectRegistrationStatus
import com.custodix.insite.local.ehr2edc.ehr.domain.event.EHRSubjectRegistrationStatusUpdated
import com.custodix.insite.local.ehr2edc.vocabulary.PatientCDWReference
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId
import org.springframework.beans.factory.annotation.Autowired

import javax.validation.ConstraintViolationException

import static com.custodix.insite.local.ehr2edc.ehr.mongo.command.StartSubjectRegistrationRequestObjectMother.aDefaultStartSubjectRegistrationRequestBuilder

class StartSubjectRegistrationSpec extends AbstractEHRMongoSpec {

    public static final String PATIENT_ID = "patientId-456"
    public static final String PATIENT_ID_SOURCE = "patientIdSource"
    public static final String SUBJECT_ID = "subjectId-123"

    @Autowired
    StartSubjectRegistration startSubjectRegistration

    def "When a subject Registration starts an EHRSubjectRegistrationUpdated event is published "() {
        given: "a request "
        def request = StartSubjectRegistration.Request.newBuilder()
                .withPatientCDWReference(PatientCDWReference.newBuilder().withId(PATIENT_ID).withSource(PATIENT_ID_SOURCE).build())
                .withSubjectId(SubjectId.of(SUBJECT_ID))
                .build()

        when: "executing start subject registration"
        startSubjectRegistration.start(request)

        then: "an EHRSubjectRegistrationStatusUpdated is published correctly"
        def ehrSubjectRegistrationUpdated = testEventPublisher.poll() as EHRSubjectRegistrationStatusUpdated
        ehrSubjectRegistrationUpdated.subjectId == SUBJECT_ID
        ehrSubjectRegistrationUpdated.namespace == PATIENT_ID_SOURCE
        ehrSubjectRegistrationUpdated.patientId == PATIENT_ID
        ehrSubjectRegistrationUpdated.status == EHRSubjectRegistrationStatus.STARTED
    }

    def " StartSubjectRegistration starts when request is null"() {
        given: "a null request "
        def request = null

        when: "executing start subject registration"
        startSubjectRegistration.start(request)

        then: "return an validation error that request cannot be null"
        def ex = thrown(ConstraintViolationException)
        ex.constraintViolations.first().propertyPath.toString() == "start.arg0"
        ex.constraintViolations.first().message == "must not be null"
    }

    def " StartSubjectRegistration starts when patient reference request is null"() {
        given: "a request with patient reference null"
        def request = aDefaultStartSubjectRegistrationRequestBuilder()
            .withPatientCDWReference(null)
            .build()

        when: "executing start subject registration"
        startSubjectRegistration.start(request)

        then: "return an validation error that patient reference request cannot be null"
        def ex = thrown(ConstraintViolationException)
        ex.constraintViolations.first().propertyPath.toString() == "start.arg0.patientCDWReference"
        ex.constraintViolations.first().message == "must not be null"
    }

    def " StartSubjectRegistration starts when subject id  request is null"() {
        given: "a request with subject id null"
        def request = aDefaultStartSubjectRegistrationRequestBuilder()
                .withSubjectId(null)
                .build()

        when: "executing start subject registration"
        startSubjectRegistration.start(request)

        then: "return an validation error that subject id  request cannot be null"
        def ex = thrown(ConstraintViolationException)
        ex.constraintViolations.first().propertyPath.toString() == "start.arg0.subjectId"
        ex.constraintViolations.first().message == "must not be null"
    }

}