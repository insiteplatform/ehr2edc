package com.custodix.insite.local.ehr2edc.ehr.mongo.command

import com.custodix.insite.local.ehr2edc.EHRConnectionRepository
import com.custodix.insite.local.ehr2edc.ehr.main.domain.event.DomainEventPublisher
import com.custodix.insite.local.ehr2edc.ehr.mongo.config.EHRMongoConfiguration
import com.custodix.insite.mongodb.export.patient.application.api.ExportPatient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import spock.lang.Specification

@SpringBootTest(
    classes = [
            SpecConfiguration.class,
            EHRMongoConfiguration.class
    ]
)
abstract class AbstractEHRMongoSpec extends Specification {

    @Autowired
    protected TestEventPublisher testEventPublisher
    @MockBean
    protected ExportPatient exportPatient
    @MockBean
    protected EHRConnectionRepository ehrConnectionRepository

    def setup() {
        testEventPublisher.clear()
        DomainEventPublisher.setPublisher(testEventPublisher)
    }

}
