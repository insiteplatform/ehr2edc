package com.custodix.insite.mongodb.export.patient.application.command

import com.custodix.insite.mongodb.export.TestConfiguration
import com.custodix.insite.mongodb.export.patient.domain.model.DomainEventPublisher
import com.custodix.insite.mongodb.export.patient.domain.model.DomainTime
import com.custodix.insite.mongodb.export.patient.domain.model.TestEventPublisher
import com.custodix.insite.mongodb.export.patient.domain.service.TestTimeService
import com.custodix.insite.mongodb.export.patient.infrastructure.batch.exportpatient.ExportPatientRunnerConfiguration
import com.custodix.insite.mongodb.export.patient.infrastructure.mongo.EmbeddedMongoConfig
import com.custodix.insite.mongodb.export.patient.infrastructure.mongo.model.demographic.DemographicDocument
import com.custodix.insite.mongodb.export.patient.infrastructure.mongo.model.labvalue.LabValueDocument
import com.custodix.insite.mongodb.export.patient.infrastructure.mongo.model.medication.MedicationDocument
import com.custodix.insite.mongodb.export.patient.infrastructure.mongo.model.observationsummary.ObservationSummaryDocument
import com.custodix.insite.mongodb.export.patient.infrastructure.mongo.model.subjectmigration.SubjectMigrationDocument
import com.custodix.insite.mongodb.export.patient.infrastructure.mongo.model.vitalsign.VitalSignDocument
import com.custodix.insite.mongodb.export.patient.main.MigratorMongoDBConfiguration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Query
import org.springframework.test.context.TestPropertySource
import spock.lang.Specification

@SpringBootTest(
        classes = [
                MigratorMongoDBConfiguration,
                ExportPatientRunnerConfiguration,
                TestConfiguration,
                EmbeddedMongoAutoConfiguration,
                EmbeddedMongoConfig],
        properties = ["spring.main.allow-bean-definition-overriding=true"])
@TestPropertySource(
        properties = ["export.patient.maleCodes=M", "export.patient.femaleCodes=F"],
        locations = "classpath:ehr2edc-infra-mongo-migrator.properties")
abstract class AbstractExportPatientSpec extends Specification {

    @Autowired
    MongoTemplate mongoTemplate
    @Autowired
    TestEventPublisher eventPublisher

    TestTimeService testTimeService = new TestTimeService()

    def setup() {
        clearMongoDb()
        initDomainPublisher()
        initDomainTime()
    }

    private initDomainPublisher() {
        eventPublisher.clear()
        DomainEventPublisher.setPublisher(eventPublisher);
    }

    private void initDomainTime() {
        DomainTime.setTime(testTimeService)
    }

    private void clearMongoDb() {
        clearDocumentsFor(DemographicDocument.class)
        clearDocumentsFor(LabValueDocument.class)
        clearDocumentsFor(MedicationDocument.class)
        clearDocumentsFor(VitalSignDocument.class)
        clearDocumentsFor(SubjectMigrationDocument.class)
        clearDocumentsFor(ObservationSummaryDocument.class)
    }

    protected void clearDocumentsFor(Class<?> documentClass) {
        mongoTemplate.remove(new Query(), documentClass)
    }
}
