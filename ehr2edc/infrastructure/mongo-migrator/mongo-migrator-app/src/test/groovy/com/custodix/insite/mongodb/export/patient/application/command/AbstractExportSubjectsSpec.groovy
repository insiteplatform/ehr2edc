package com.custodix.insite.mongodb.export.patient.application.command

import com.custodix.insite.mongodb.export.TestConfiguration
import com.custodix.insite.mongodb.export.patient.application.api.ExportPatient
import com.custodix.insite.mongodb.export.patient.application.api.ExportSubjects
import com.custodix.insite.mongodb.export.patient.domain.model.DomainTime
import com.custodix.insite.mongodb.export.patient.domain.repository.PatientNamespaceRepository
import com.custodix.insite.mongodb.export.patient.domain.service.TestTimeService
import com.custodix.insite.mongodb.export.patient.infrastructure.mongo.model.activesubject.ActiveSubjectDocument
import com.custodix.insite.mongodb.export.patient.infrastructure.mongo.repository.ActiveSubjectDocumentRepository
import com.custodix.insite.mongodb.export.patient.main.ActiveSubjectConfiguration
import com.custodix.insite.mongodb.export.patient.main.ExportSubjectsConfiguration
import com.custodix.insite.mongodb.export.patient.main.MigratorMongoDBConfiguration
import com.custodix.insite.mongodb.export.patient.main.MongoMigratorBatchConfiguration
import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Query
import org.springframework.test.context.TestPropertySource
import spock.lang.Specification

@SpringBootTest(classes = [
        MigratorMongoDBConfiguration,
        ActiveSubjectConfiguration,
        MongoMigratorBatchConfiguration,
        ExportSubjectsConfiguration,
        TestConfiguration,
        EmbeddedMongoAutoConfiguration
])
@TestPropertySource(
        properties = [
           "spring.flyway.enabled=false",
           "export.subjects.enable=false",
        ],
        locations = ["classpath:ehr2edc-infra-mongo-migrator.properties"])
abstract class AbstractExportSubjectsSpec extends Specification{
    @SpringBean
    ExportPatient exportPatientMock = Mock()
    @SpringBean
    PatientNamespaceRepository patientNamespaceRepository = Mock()
    @Autowired
    ExportSubjects exportSubjects
    @Autowired
    MongoTemplate mongoTemplate
    @Autowired
    ActiveSubjectDocumentRepository activeSubjectDocumentRepository

    TestTimeService testTimeService = new TestTimeService()

    def setup() {
        clearMongoDb()
        initDomainTime()
    }

    private void initDomainTime() {
        DomainTime.setTime(testTimeService)
    }

    private void clearMongoDb() {
        clearDocumentsFor(ActiveSubjectDocument.class)
    }

    protected void clearDocumentsFor(Class<?> documentClass) {
        mongoTemplate.remove(new Query(), documentClass)
    }
}
