package com.custodix.insite.local.ehr2edc.mongo.app.document

import spock.lang.Specification

class StudyDocumentToItemQueryMappingsSpec extends Specification {

    def "returns empty map when item query mapping is null"() {
        given: "a study with item query mapping is null "
        def studyDocument = new StudyDocument("studyId", "name", "description", null, Collections.emptyList(),  Collections.emptyList(),Collections.emptyList())

        when: "mapping to map"
        def itemQueryMappings = studyDocument.toItemQueryMappings()

        then: "map is empty"
        itemQueryMappings.isEmpty()
    }
}
