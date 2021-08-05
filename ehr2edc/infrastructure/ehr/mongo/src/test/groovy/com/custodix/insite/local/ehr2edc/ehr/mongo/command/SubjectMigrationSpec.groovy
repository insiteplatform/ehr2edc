package com.custodix.insite.local.ehr2edc.ehr.mongo.command


import com.custodix.insite.local.ehr2edc.vocabulary.EHRConnectionObjectMother
import com.custodix.insite.local.ehr2edc.vocabulary.PatientCDWReference
import com.custodix.insite.local.ehr2edc.vocabulary.StudyId
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId
import org.springframework.beans.factory.annotation.Autowired

import javax.validation.ConstraintViolationException

import static com.custodix.insite.local.ehr2edc.ehr.mongo.command.SubjectMigrationRequestObjectMother.aDefaultSubjectMigrationRequestBuilder
import static org.mockito.ArgumentMatchers.argThat
import static org.mockito.Mockito.verify
import static org.mockito.Mockito.when

class SubjectMigrationSpec extends AbstractEHRMongoSpec {

    public static final String STUDY_ID = "studyId-123"
    public static final String PATIENT_ID = "patientId-456"
    public static final String PATIENT_ID_SOURCE = "patientIdSource"
    public static final String SUBJECT_ID = "subjectId-123"

    @Autowired
    private SubjectMigration subjectMigration

    def "Subject migration executes the export patient when the study is not fhir"() {
        given: "a subject migration request for a non fhir study"
        def aNonFhirStudyId = aNonFhirStudy()
        def request = SubjectMigration.Request.newBuilder()
                .withStudyId(aNonFhirStudyId)
                .withPatientCDWReference(PatientCDWReference.newBuilder().withId(PATIENT_ID).withSource(PATIENT_ID_SOURCE).build())
                .withSubjectId(SubjectId.of(SUBJECT_ID))
                .build()

        when: "executing subject migration"
        subjectMigration.migrate(request)

        then: "export patient is called correctly"
        verify(exportPatient).export(argThat({
            exportPatientRequest ->
                        exportPatientRequest.patientIdentifier.namespace.name == PATIENT_ID_SOURCE &&
                        exportPatientRequest.patientIdentifier.patientId.id == PATIENT_ID &&
                        exportPatientRequest.patientIdentifier.subjectId.id == SUBJECT_ID
        }))
    }

    def "Subject migration does not execute the export patient if the study not a fhir study"() {
        given: "a request with a FHIR study"
        def fhirStudyId = aFhirStudy()
        def request = aDefaultSubjectMigrationRequestBuilder()
                .withStudyId(fhirStudyId)
                .build()

        when: "executing the subject migration"
        subjectMigration.migrate(request)

        then: "the export patient is not executed"
        0 * exportPatient.export(_)
    }

    def "SubjectMigration fails when request is null"() {
        given: "a null request "
        def request = null

        when: "executing subject migration"
        subjectMigration.migrate(request)

        then: "return an validation error that request cannot be null"
        def ex = thrown(ConstraintViolationException)
        ex.constraintViolations.first().propertyPath.toString() == "migrate.arg0"
        ex.constraintViolations.first().message == "must not be null"
    }

    def "SubjectMigration fails when study id is null"() {
        given: "a subject migration request with study id null "
        def request = aDefaultSubjectMigrationRequestBuilder()
                .withStudyId(null)
                .build()

        when: "executing subject migration"
        subjectMigration.migrate(request)

        then: "return an validation error that study id cannot be null"
        def ex = thrown(ConstraintViolationException)
        ex.constraintViolations.first().propertyPath.toString() == "migrate.arg0.studyId"
        ex.constraintViolations.first().message == "must not be null"
    }

    def "SubjectMigration fails when subject id is null"() {
        given: "a subject migration request with study id null "
        def request = aDefaultSubjectMigrationRequestBuilder()
                .withSubjectId(null)
                .build()

        when: "executing subject migration"
        subjectMigration.migrate(request)

        then: "return an validation error that subject id cannot be null"
        def ex = thrown(ConstraintViolationException)
        ex.constraintViolations.first().propertyPath.toString() == "migrate.arg0.subjectId"
        ex.constraintViolations.first().message == "must not be null"
    }

    def "SubjectMigration fails when patient reference is null"() {
        given: "a subject migration request with study id null "
        def request = aDefaultSubjectMigrationRequestBuilder()
                .withPatientCDWReference(null)
                .build()

        when: "executing subject migration"
        subjectMigration.migrate(request)

        then: "return an validation error that subject id cannot be null"
        def ex = thrown(ConstraintViolationException)
        ex.constraintViolations.first().propertyPath.toString() == "migrate.arg0.patientCDWReference"
        ex.constraintViolations.first().message == "must not be null"
    }

    StudyId aFhirStudy() {
        def fhirStudyId = StudyId.of("fhirStudyId")
        def connection = EHRConnectionObjectMother.aFhirConnection(fhirStudyId)
        when(ehrConnectionRepository.findByStudyId(fhirStudyId)).thenReturn(Optional.of(connection))
        fhirStudyId
    }

    StudyId aNonFhirStudy() {
        def nonFhirStudyId = StudyId.of(STUDY_ID)
        when(ehrConnectionRepository.findByStudyId(nonFhirStudyId)).thenReturn(Optional.empty())
        nonFhirStudyId
    }
}
