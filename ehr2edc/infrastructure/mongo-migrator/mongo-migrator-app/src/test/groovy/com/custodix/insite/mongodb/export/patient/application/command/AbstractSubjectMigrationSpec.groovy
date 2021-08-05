package com.custodix.insite.mongodb.export.patient.application.command

import com.custodix.insite.mongodb.export.TestConfiguration
import com.custodix.insite.mongodb.export.patient.domain.model.DomainTime
import com.custodix.insite.mongodb.export.patient.domain.service.TestTimeService
import com.custodix.insite.mongodb.export.patient.infrastructure.mongo.model.subjectmigration.SubjectMigrationDocument
import com.custodix.insite.mongodb.export.patient.main.MigratorMongoDBConfiguration
import com.custodix.insite.mongodb.export.patient.main.SubjectMigrationConfiguration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Query
import org.springframework.test.context.TestPropertySource
import spock.lang.Specification

@SpringBootTest(classes = [
        MigratorMongoDBConfiguration,
        SubjectMigrationConfiguration,
        ValidationAutoConfiguration,
        TestConfiguration,
        EmbeddedMongoAutoConfiguration],
        properties = ["spring.main.allow-bean-definition-overriding=true"])
@TestPropertySource(
        properties = [
           "spring.flyway.enabled=false",
        ],
        locations = ["classpath:ehr2edc-infra-mongo-migrator.properties"])
abstract class AbstractSubjectMigrationSpec extends Specification{

    @Autowired
    MongoTemplate mongoTemplate

    static {
        initDomainTime()
    }

    def setup() {
        clearMongoDb()
    }

    private static void initDomainTime() {
        TestTimeService testTimeService = new TestTimeService()
        DomainTime.setTime(testTimeService)
    }

    private void clearMongoDb() {
        clearDocumentsFor(SubjectMigrationDocument.class)
    }

    protected void clearDocumentsFor(Class<?> documentClass) {
        mongoTemplate.remove(new Query(), documentClass)
    }
}
