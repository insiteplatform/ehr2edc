package com.custodix.insite.mongodb.export.patient.application.command

import com.custodix.insite.mongodb.export.TestConfiguration
import com.custodix.insite.mongodb.export.patient.application.api.ExportPatient
import com.custodix.insite.mongodb.export.patient.domain.exceptions.DomainException
import com.custodix.insite.mongodb.export.patient.domain.exceptions.SystemException
import com.custodix.insite.mongodb.export.patient.domain.model.ClinicalFindingFact
import com.custodix.insite.mongodb.export.patient.domain.model.LaboratoryFact
import com.custodix.insite.mongodb.export.patient.domain.model.PatientFact
import com.custodix.insite.mongodb.export.patient.domain.model.demographic.BirthInformation
import com.custodix.insite.mongodb.export.patient.domain.model.demographic.DeathInformation
import com.custodix.insite.mongodb.export.patient.domain.model.demographic.PatientGender
import com.custodix.insite.mongodb.export.patient.domain.model.demographic.VitalStatus
import com.custodix.insite.mongodb.export.patient.domain.model.medication.MedicationFact
import com.custodix.insite.mongodb.export.patient.infrastructure.batch.exportpatient.ExportPatientRunnerConfiguration
import com.custodix.insite.mongodb.export.patient.main.MigratorMongoDBConfiguration
import com.custodix.insite.mongodb.export.patient.main.SubjectMigrationConfiguration
import com.custodix.insite.mongodb.vocabulary.Namespace
import com.custodix.insite.mongodb.vocabulary.PatientId
import com.custodix.insite.mongodb.vocabulary.PatientIdentifier
import com.custodix.insite.mongodb.vocabulary.SubjectId
import org.springframework.batch.core.Job
import org.springframework.batch.core.JobExecution
import org.springframework.batch.core.JobExecutionException
import org.springframework.batch.core.JobParameters
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException
import org.springframework.batch.item.support.ListItemWriter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.SpyBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.test.context.TestPropertySource
import spock.lang.Shared

import java.time.Instant

import static com.custodix.insite.mongodb.export.patient.domain.model.demographic.BirthInformation.Accuracy.UNSPECIFIED
import static com.custodix.insite.mongodb.export.patient.domain.model.demographic.DeathInformation.Accuracy.PATIENT_ALIVE
import static com.custodix.insite.mongodb.export.patient.domain.model.demographic.PatientGender.Gender.MALE
import static com.custodix.insite.mongodb.export.patient.main.MongoMigratorBatchConfiguration.JOB_LAUNCHER_MONGO_MIGRATOR
import static org.hamcrest.Matchers.*
import static org.mockito.ArgumentMatchers.any
import static org.mockito.Mockito.doReturn
import static org.mockito.Mockito.doThrow
import static spock.util.matcher.HamcrestSupport.expect

@SpringBootTest(classes = [
        MigratorMongoDBConfiguration,
        ExportPatientRunnerConfiguration,
        SubjectMigrationConfiguration,
        Config,
        TestConfiguration,
        EmbeddedMongoAutoConfiguration]
        , properties = ["spring.main.allow-bean-definition-overriding=true"])
@TestPropertySource(
        properties = ["export.patient.maleCodes=M", "export.patient.femaleCodes=F"],
        locations = "classpath:ehr2edc-infra-mongo-migrator.properties")
class ExportPatientSpec extends AbstractExportPatientSpec {
    private static final String PATIENT_ID = "7"
    private static final String NAMESPACE = "MASTER_PATIENT_INDEX"
    private static final String SUBJECT_ID = "SUBJ_1"
    private static final String UNKNOWN_PATIENT_ID = "UNKNOWN"
    private static final String UNKNOWN_NAMESPACE = "UNKNOWN"
    private static final String OBSERVATION_UNIT = "%"

    @Autowired
    private ExportPatient exportPatient

    @Autowired
    private ListItemWriter<PatientFact> patientFactWriter
    @Autowired
    private ListItemWriter<LaboratoryFact> laboratoryFactWriter
    @Autowired
    private ListItemWriter<ClinicalFindingFact> clinicalFindingFactWriter
    @Autowired
    private ListItemWriter<MedicationFact> medicationFactWriter

    @SpyBean(name = JOB_LAUNCHER_MONGO_MIGRATOR)
    private JobLauncher jobLauncher

    @Shared
    ExportPatient.Request request = ExportPatient.Request.newBuilder()
            .withPatientIdentifier(PatientIdentifier.of(
                    PatientId.of(PATIENT_ID),
                    Namespace.of(NAMESPACE),
                    SubjectId.of(SUBJECT_ID)))
            .build()

    void cleanup() {
        patientFactWriter.writtenItems.clear()
        laboratoryFactWriter.writtenItems.clear()
        clinicalFindingFactWriter.writtenItems.clear()
        medicationFactWriter.writtenItems.clear()
    }

    def "Exporting a patient by the given id results in the correct patient records inserted"() {
        when: "I run the export job with the given patient id"
        exportPatient.export(request)

        then: "The correct patient fact record gets inserted in the fact collection"
        expect patientFactWriter.writtenItems, hasSize(1)
        verifyAll(patientFactWriter.writtenItems[0]) {
            identifier.patientId.id == PATIENT_ID
            identifier.namespace.name == NAMESPACE
            identifier.subjectId.id == SUBJECT_ID
            patientIsMale(gender)
            patientIsBorn(birthInformation)
            patientIsNotDeceased(deathInformation)
            patientIsAlive(vitalStatus)
        }
    }

    def "Exporting a patient by the given id results in the correct laboratory records inserted"() {
        when: "I run the export job with the given patient id"
        exportPatient.export(request)

        then: "The correct laboratory fact records get inserted into the fact collection"
        expect laboratoryFactWriter.writtenItems, hasSize(7)
        laboratoryFactWriter.writtenItems.each { fact ->
            verifyAll(fact) {
                patientIdentifier != null
                patientIdentifier.patientId.id == PATIENT_ID
                patientIdentifier.namespace.name == NAMESPACE
                patientIdentifier.subjectId.id == SUBJECT_ID
                !conceptPaths.empty
                referenceConcept != null
                localConcept != null
                valueObservation != null
                ulnObservation != null
                llnObservation != null
                label == "HbA1c"
                vendor == "SOURCE_7"
            }
        }
    }

    def "Exporting a patient by the given id results in the correct medication records inserted"() {
        when: "I run the export job with the given patient id"
        exportPatient.export(request)

        then: "The correct medication fact records get inserted into the fact collection"
        expect medicationFactWriter.writtenItems, hasSize(4)
        medicationFactWriter.writtenItems.each { fact ->
            verifyAll(fact) {
                patientIdentifier.patientId.id == PATIENT_ID
                patientIdentifier.namespace.name == NAMESPACE
                patientIdentifier.subjectId.id == SUBJECT_ID
                startDate == Instant.parse("2016-03-01T23:00:00.00Z")
                endDate == Instant.parse("2016-03-01T23:00:00.00Z")
                localConcept != null
                verifyAll(localConcept) {
                    code == "((BNF 0601012X0BBACAB))"
                    schema == ""
                }
                referenceConcept != null
                verifyAll(referenceConcept) {
                    code == "A10A"
                    schema == "ATC"
                }
                conceptPaths.size() == 1
                verifyAll(conceptPaths.get(0)) {
                    path == "\\insite\\410942007\\ATC_A\\ATC_A10\\ATC_A10A\\((BNF 0601012X0BBACAB))\\"
                    concepts.size() == 6
                    concepts.get(0).code == "insite"
                    concepts.get(0).schema == ""
                    concepts.get(1).code == "410942007"
                    concepts.get(1).schema == ""
                    concepts.get(2).code == "A"
                    concepts.get(2).schema == "ATC"
                    concepts.get(3).code == "A10"
                    concepts.get(3).schema == "ATC"
                    concepts.get(4).code == "A10A"
                    concepts.get(4).schema == "ATC"
                    concepts.get(5).code == "((BNF 0601012X0BBACAB))"
                    concepts.get(5).schema == ""
                }
            }
        }
    }

    def "Exporting a patient with an unknown patient id"() {
        when: "I run the export job with an unknown patient id"
        ExportPatient.Request request = ExportPatient.Request.newBuilder()
                .withPatientIdentifier(PatientIdentifier.of(PatientId.of(UNKNOWN_PATIENT_ID), Namespace.of(NAMESPACE), SubjectId.of(SUBJECT_ID)))
                .build()
        exportPatient.export(request)

        then: "No fact records get inserted in the fact collection"
        expect patientFactWriter.writtenItems, empty()
        expect laboratoryFactWriter.writtenItems, empty()
        expect clinicalFindingFactWriter.writtenItems, empty()
        expect medicationFactWriter.writtenItems, empty()
        and: "An exception gets thrown"
        DomainException exception = thrown(DomainException.class)
        and: "a cause exception has correct message"
        expect exception.cause.message, equalTo("Unable to export a patient with identifier UNKNOWN and namespace MASTER_PATIENT_INDEX")
    }

    def "Exporting a patient with an unknown namespace"() {
        when: "I run the export job with an unknown namespace"
        ExportPatient.Request request = ExportPatient.Request.newBuilder()
                .withPatientIdentifier(PatientIdentifier.of(PatientId.of(PATIENT_ID), Namespace.of(UNKNOWN_NAMESPACE), SubjectId.of(SUBJECT_ID)))
                .build()
        exportPatient.export(request)

        then: "No fact records get inserted in the fact collection"
        expect patientFactWriter.writtenItems, empty()
        expect laboratoryFactWriter.writtenItems, empty()
        expect clinicalFindingFactWriter.writtenItems, empty()
        expect medicationFactWriter.writtenItems, empty()
        and: "An exception gets thrown"
        DomainException exception = thrown(DomainException.class)
        and: "a cause exception has correct message"
        expect exception.cause.message, equalTo("Unable to export a patient with identifier 7 and namespace UNKNOWN")
    }

    def "Exporting a patient with a faulty job launcher"() {
        given: "the export job launcher will throw an exception"
        doThrow(JobExecutionAlreadyRunningException.class)
                .when(jobLauncher).run(any(Job.class), any(JobParameters.class))
        when: "I run the export job"
        exportPatient.export(request)
        then: "No patient fact records get inserted in the fact collection"
        expect patientFactWriter.writtenItems, empty()
        and: "A domain exception gets thrown"
        DomainException exception = thrown(DomainException.class)
        and: "a cause exception has correct message"
        expect exception.cause.cause, is(instanceOf(JobExecutionAlreadyRunningException.class))
    }

    def "Exporting a patient with failureExceptions" () {
        given: "the jobExecution contains failureExceptions"
        JobExecution jobExecution = new JobExecution(1L);
        jobExecution.addFailureException(new Throwable("ERROR"))
        jobExecution.addFailureException(new Throwable("ERROR"))
        doReturn(jobExecution).when(jobLauncher).run(any(Job.class), any(JobParameters.class));

        when: "Running the export job"
        exportPatient.export(request)

        then: "A domain is thrown containing a JobExecutionException"
        DomainException ex = thrown()
        and: "the root cause exception is JobExecutionException "
        expect ex.cause.cause, is(instanceOf(JobExecutionException.class))
        and: "The JobExecutionException suppresses the original exceptions"
        JobExecutionException jee = ex.cause.cause as JobExecutionException
        jee.suppressed.size() == 2
        jee.suppressed.detailMessage.every { it == "ERROR"}
    }

    private void patientIsMale(PatientGender gender) {
        verifyAll(gender) {
            sourceValue == "M"
            interpretedValue == MALE
        }
    }

    private void patientIsBorn(BirthInformation birthInformation) {
        verifyAll(birthInformation) {
            accuracy == UNSPECIFIED
            birthDate != null
        }
    }

    private void patientIsAlive(VitalStatus vitalStatus) {
        verifyAll(vitalStatus) {
            status.ALIVE
        }
    }

    private void patientIsNotDeceased(DeathInformation deathInformation) {
        verifyAll(deathInformation) {
            accuracy == PATIENT_ALIVE
            deathDate == null
        }
    }

    @Configuration
    static class Config {
        @Bean
        ListItemWriter<PatientFact> patientFactWriter() {
            return new ListItemWriter<PatientFact>()
        }

        @Bean
        ListItemWriter<LaboratoryFact> laboratoryFactWriter() {
            return new ListItemWriter<LaboratoryFact>()
        }

        @Bean
        ListItemWriter<ClinicalFindingFact> clinicalFindingFactWriter() {
            return new ListItemWriter<ClinicalFindingFact>()
        }

        @Bean
        ListItemWriter<MedicationFact> medicationFactWriter() {
            return new ListItemWriter<MedicationFact>()
        }
    }
}