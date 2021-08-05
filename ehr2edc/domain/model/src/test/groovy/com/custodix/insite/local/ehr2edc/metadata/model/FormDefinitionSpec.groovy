package com.custodix.insite.local.ehr2edc.metadata.model

import com.custodix.insite.local.ehr2edc.vocabulary.ItemGroupDefinitionId
import spock.lang.Specification

import static ItemGroupDefinitionObjectMother.aDefaultItemGroupDefinitionBuilder
import static com.custodix.insite.local.ehr2edc.metadata.model.FormDefinitionObjectMother.aDefaultFormDefinitionBuilder

class FormDefinitionSpec extends Specification {

    def "find an item group definition by id returns the corresponding item group definition."(String itemGroupId) {
        given: "a form definition having item group with id '#itemGroupId' "
        def itemGroup = aDefaultItemGroupDefinitionBuilder().withId(ItemGroupDefinitionId.of(itemGroupId)).build()
        def formDefinition = aDefaultFormDefinitionBuilder()
                .withItemGroupDefinitions(Collections.singletonList(itemGroup))
                .build()

        when: "getting item group by item group id"
        def actualItemGroup = formDefinition.getItemGroupBy(itemGroupId)

        then: "the corresponding item group is returned"
        actualItemGroup.id.id == itemGroupId

        where:
        itemGroupId | _
        "987-653" | _
    }
}
