package com.custodix.insite.local.ehr2edc.mongo.app.study

import com.custodix.insite.local.ehr2edc.ItemQueryMappingJson
import com.custodix.insite.local.ehr2edc.ItemQueryMappings
import com.custodix.insite.local.ehr2edc.mongo.app.jackson.AppMongoJacksonConfiguration
import com.custodix.insite.local.ehr2edc.query.populator.ItemQueryMappingServiceImpl
import com.custodix.insite.local.ehr2edc.query.executor.demographic.projector.gender.GenderProjector
import com.custodix.insite.local.ehr2edc.query.executor.demographic.projector.gender.GenderToSDTMCodeProjector
import com.custodix.insite.local.ehr2edc.shared.exceptions.SystemException
import spock.lang.Specification
import spock.lang.Unroll

class ItemQueryMappingServiceImplToItemQueryMappingSpec extends Specification {

    def itemQueryMappingService = new ItemQueryMappingServiceImpl(AppMongoJacksonConfiguration.createObjectMapper())

    @Unroll
    def "throw a system exception when ItemQueryMappings contains an invalid json" (String itemId, String itemQueryMappingJson) {
        given: "an invalid ItemQueryMapping"
        ItemQueryMappings itemQueryMappings =
                ItemQueryMappings.newBuilder()
                        .withItemQueryMappings(Collections.singletonMap(itemId, ItemQueryMappingJson.create(itemQueryMappingJson))).build()

        when: "when converting"
        itemQueryMappingService.toItemQueryMappingMap(itemQueryMappings)

        then: "a system exception is thrown"
        def exception = thrown SystemException
        exception.message == "Cannot convert json string '" + itemQueryMappingJson + "' to an ItemQueryMapping"

        where:
        itemId | itemQueryMappingJson
        "123"       | "invalidJson"
    }

    def "returns demographic ItemQueryMapping correctly"(String itemId, String itemQueryMappingJson) {
        given: "a valid  ItemQueryMapping"
        ItemQueryMappings itemQueryMappings =
                ItemQueryMappings.newBuilder()
                        .withItemQueryMappings(Collections.singletonMap(itemId, ItemQueryMappingJson.create(itemQueryMappingJson))).build()

        when: "when converting"
        def itemQueryMappingMap = itemQueryMappingService.toItemQueryMappingMap(itemQueryMappings)

        then: "the ItemQueryMapping is returned "
        itemQueryMappingMap.size() == 1
        itemQueryMappingMap.containsKey(itemId)
        itemQueryMappingMap.get(itemId).query.criteria.criteria.size() == 0
        itemQueryMappingMap.get(itemId).projectors.size() == 2
        itemQueryMappingMap.get(itemId).projectors[0].getClass() ==  GenderProjector.class
        itemQueryMappingMap.get(itemId).projectors[1].getClass() ==  GenderToSDTMCodeProjector.class


        where:
        itemId | itemQueryMappingJson
        "DM_DOB.SEX"       | "{\"query\":{\"type\":\"demographic\",\"criteria\":{\"criteria\":[]}},\"projectors\":[{\"type\":\"gender\"},{\"type\":\"genderToSDTMCode\"}]}"
    }
}
