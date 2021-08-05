package com.custodix.insite.local.ehr2edc

import com.custodix.insite.local.ehr2edc.mongo.app.event.PopulatedEventMongoRepository
import com.custodix.insite.local.ehr2edc.mongo.app.study.EHRConnectionMongoRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

@SpringBootTest(classes = SpecConfiguration.class)
class RepositorySpec extends Specification {
    @Autowired
    PopulatedEventMongoRepository.EventMongoSnapshotRepository eventMongoSnapshotRepository
    @Autowired
    EHRConnectionMongoRepository.EHRConnectionDocumentRepository ehrConnectionDocumentRepository

    def setup() {
        eventMongoSnapshotRepository.deleteAll()
        ehrConnectionDocumentRepository.deleteAll()
    }
}
