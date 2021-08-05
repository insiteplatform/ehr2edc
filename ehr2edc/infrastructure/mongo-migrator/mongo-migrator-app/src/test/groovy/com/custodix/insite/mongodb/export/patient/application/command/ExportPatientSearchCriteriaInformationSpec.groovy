package com.custodix.insite.mongodb.export.patient.application.command

import com.custodix.insite.mongodb.export.TestConfiguration
import com.custodix.insite.mongodb.export.patient.application.api.ExportPatientSearchCriteriaInformation
import com.custodix.insite.mongodb.export.patient.domain.exceptions.SystemException
import com.custodix.insite.mongodb.export.patient.domain.model.patientsearchcriteriainformation.PatientSearchCriteriaInformation
import com.custodix.insite.mongodb.export.patient.main.ExportPatientsIdentificationsConfiguration
import org.springframework.batch.core.Job
import org.springframework.batch.core.JobParameters
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException
import org.springframework.batch.item.support.ListItemWriter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.SpyBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.test.context.TestPropertySource
import spock.lang.Specification

import static com.custodix.insite.mongodb.export.patient.main.MongoMigratorBatchConfiguration.JOB_LAUNCHER_MONGO_MIGRATOR
import static org.hamcrest.Matchers.*
import static org.mockito.ArgumentMatchers.any
import static org.mockito.Mockito.doThrow
import static spock.util.matcher.HamcrestSupport.expect

@SpringBootTest(
        classes = [
                ExportPatientsIdentificationsConfiguration,
                Config,
                TestConfiguration
        ],
        properties = ["spring.main.allow-bean-definition-overriding=true"])
@TestPropertySource(locations = "classpath:ehr2edc-infra-mongo-migrator.properties")
class ExportPatientSearchCriteriaInformationSpec extends Specification {
    private static final List<String> EXPECTED_NAMESPACES = Arrays.asList("MASTER_PATIENT_INDEX", "OTHER_PATIENT_INDEX", "INACTIVE_PATIENT_INDEX")

    @Autowired
    ExportPatientSearchCriteriaInformation exportPatientsIdentifications
    @Autowired
    private ListItemWriter<PatientSearchCriteriaInformation> patientIdentifierWriter
    @SpyBean(name = JOB_LAUNCHER_MONGO_MIGRATOR)
    private JobLauncher jobLauncher

    def "Exporting patient identifications inserts all patient identifications from the data warehouse into the patient identifications collection"() {
        when: "I export patient identifications"
        exportPatientsIdentifications.export()

        then: "All patient identifications from the data warehouse are inserted into the patient identifications collection"
        expect patientIdentifierWriter.writtenItems, hasSize(217)
        patientIdentifierWriter.writtenItems.each { patientIdentification ->
            verifyAll(patientIdentification) {
                def patientIdentifier = patientIdentification.patientIdentifier
                patientIdentification.birthDate != null
                patientIdentifier != null
                !patientIdentifier.patientId.id.empty
                patientIdentifier.namespace != null
                EXPECTED_NAMESPACES.contains(patientIdentifier.namespace.name)
            }
        }
    }

    def "Exporting patient identifications with a faulty job launcher"() {
        given: "The export job launcher will throw an exception"
        doThrow(JobExecutionAlreadyRunningException.class)
                .when(jobLauncher).run(any(Job.class), any(JobParameters.class))

        when: "I export patient identifications"
        exportPatientsIdentifications.export()

        then: "No patient identifications are inserted into the patient identifications collection"
        expect patientIdentifierWriter.writtenItems, empty()
        and: "A system exception gets thrown"
        SystemException exception = thrown(SystemException.class)
        expect exception.cause, is(instanceOf(JobExecutionAlreadyRunningException.class))
    }

    void cleanup() {
        patientIdentifierWriter.writtenItems.clear()
    }

    @Configuration
    static class Config {
        @Primary
        @Bean
        ListItemWriter<PatientSearchCriteriaInformation> patientIdentificationItemWriter() {
            return new ListItemWriter<PatientSearchCriteriaInformation>()
        }
    }
}
