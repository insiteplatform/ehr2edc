package com.custodix.insite.local.ehr2edc.mongo.app.document

import com.custodix.insite.local.ehr2edc.snapshots.EventDefinitionSnapshotObjectMother
import spock.lang.Specification

class EventDefinitionDocumentSpec extends Specification {

    def "maps EventDefinitionSnapshot without forms to EventDefinitionSnapshotMongoSnapshot"() {
        given: "An eventDefinitionsnapshot"
        def snapshot = EventDefinitionSnapshotObjectMother.eventDefinitionWithoutForms()

        when: "Mapping the eventDefinitionSnapshot to the Mongo-representation"
        def mongoSnapshot = EventDefinitionDocument.fromSnapshots([snapshot])[0]

        then: "The event's properties are copied over"
        with(mongoSnapshot) {
            id == snapshot.id.id
            parentId == snapshot.parentId
            name == snapshot.name
            formDefinitionSnapshots.empty
        }
    }
}
