package com.custodix.insite.local.ehr2edc.mongo.app.jackson

import com.custodix.insite.local.ehr2edc.ItemQueryMappings
import com.custodix.insite.local.ehr2edc.SpecConfiguration
import com.custodix.insite.local.ehr2edc.metadata.model.EventDefinition
import com.custodix.insite.local.ehr2edc.snapshots.EventDefinitionSnapshotObjectMother
import com.custodix.insite.local.ehr2edc.snapshots.ItemQueryMappingSnapshot
import com.custodix.insite.local.ehr2edc.snapshots.ItemQueryMappingSnapshotObjectMother
import com.custodix.insite.local.ehr2edc.vocabulary.ItemDefinitionId
import groovy.json.JsonSlurper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

@SpringBootTest(classes = SpecConfiguration.class)
class ObjectToMongoJsonMappingServiceSpec extends Specification {

    @Autowired
    ObjectToMongoJsonMappingService mapper

    def "Can be autowired"() {
        expect:
        mapper != null
    }

    def "Can map a EventDefinition to JSON representing the object"() {
        given: "An eventDefinition"
        def snapshot = EventDefinitionSnapshotObjectMother.eventDefinitionWithSingleForm()
        def event = EventDefinition.restoreFrom(snapshot)

        when: "Mapping the eventDefinition to the Mongo JSON-representation"
        def json = mapper.map(event)

        then: "the JSON matches the original object"
        def slurped = new JsonSlurper().parseText(json)
        with(slurped) {
            id == event.id.id
            parentId == event.parentId
            name == event.name
            formDefinitions.size == 1
        }
    }

    def "Can map a ItemQueryMappings to JSON representing the object"() {
        given: "An eventDefinition"
        def snapshot = ItemQueryMappingSnapshotObjectMother.aDefaultItemQueryMappingSnapshot()
        Map<ItemDefinitionId, ItemQueryMappingSnapshot> map = [(ItemDefinitionId.of("ITEM")): snapshot]
        ItemQueryMappings mapping = ItemQueryMappings.restoreFrom(map)

        when: "Mapping the eventDefinition to the Mongo JSON-representation"
        def json = mapper.map(mapping)

        then: "the JSON matches the original object"
        def slurped = new JsonSlurper().parseText(json)
        with(slurped) {
            itemQueryMappingJsonMap
            itemQueryMappingJsonMap.ITEM
            itemQueryMappingJsonMap.ITEM.value
        }
    }
}
