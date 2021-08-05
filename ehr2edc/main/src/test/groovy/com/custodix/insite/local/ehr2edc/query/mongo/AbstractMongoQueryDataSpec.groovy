package com.custodix.insite.local.ehr2edc.query.mongo


import com.custodix.insite.mongodb.export.TestConfiguration
import com.custodix.insite.mongodb.export.patient.application.api.ExportPatient
import com.custodix.insite.mongodb.export.patient.domain.model.DomainEventPublisher
import com.custodix.insite.mongodb.export.patient.domain.model.TestEventPublisher
import com.custodix.insite.mongodb.export.patient.infrastructure.batch.exportpatient.ExportPatientRunnerConfiguration
import com.custodix.insite.mongodb.export.patient.main.MigratorMongoDBConfiguration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource
import spock.lang.Specification

@SpringBootTest(
        classes = [
                ExportPatientRunnerConfiguration,
                TestConfiguration,
                EmbeddedMongoAutoConfiguration,
                MigratorMongoDBConfiguration,
                MongoQueryDataConfiguration
        ]
        , properties = ["spring.main.allow-bean-definition-overriding=true"])
@TestPropertySource(
        properties = [
                "export.patient.maleCodes=M",
                "export.patient.femaleCodes=F",
                "spring.flyway.enabled=false"
        ],
        locations = ["classpath:ehr2edc-infra-mongo-migrator.properties", "classpath:ehr2edc-infra-mongo-query.properties"])
abstract class AbstractMongoQueryDataSpec extends Specification {

    @Autowired
    ExportPatient exportPatient

    TestEventPublisher testEventPublisher = new TestEventPublisher()

    def setup() {
        DomainEventPublisher.setPublisher(testEventPublisher)
    }

}
