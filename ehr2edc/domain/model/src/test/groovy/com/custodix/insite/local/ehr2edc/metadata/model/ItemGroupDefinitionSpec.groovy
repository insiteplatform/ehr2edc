package com.custodix.insite.local.ehr2edc.metadata.model

import com.custodix.insite.local.ehr2edc.vocabulary.ItemDefinitionId
import spock.lang.Specification

import static ItemDefinitionObjectMother.aDefaultItemBuilder
import static ItemGroupDefinitionObjectMother.aDefaultItemGroupDefinitionBuilder
import static java.util.Collections.singletonList

class ItemGroupDefinitionSpec extends Specification {

    def "find a item definition by id returns the corresponding item definition."(String itemId) {
        given: "an item group having item with id '#itemId' "
        def item = aDefaultItemBuilder().withId(ItemDefinitionId.of(itemId)).build()
        def itemGroup = aDefaultItemGroupDefinitionBuilder().withItemDefinitions(singletonList(item)).build()

        when: "getting item by id"
        def actualItem= itemGroup.getItemBy(itemId)

        then: "the corresponding item  is returned"
        actualItem.id.id == itemId

        where:
        itemId      | _
        "987-653"   | _
    }
}
