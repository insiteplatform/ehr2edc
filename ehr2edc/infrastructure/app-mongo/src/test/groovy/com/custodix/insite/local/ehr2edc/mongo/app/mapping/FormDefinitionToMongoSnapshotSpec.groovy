package com.custodix.insite.local.ehr2edc.mongo.app.mapping

import com.custodix.insite.local.ehr2edc.metadata.model.FormDefinitionObjectMother
import com.custodix.insite.local.ehr2edc.mongo.app.document.FormDefinitionDocument
import com.custodix.insite.local.ehr2edc.vocabulary.FormDefinitionId
import com.custodix.insite.local.ehr2edc.vocabulary.LabName
import spock.lang.Specification
import spock.lang.Unroll

class FormDefinitionToMongoSnapshotSpec extends Specification {
    @Unroll
    def "Mapping a form definition without item group definitions with id '#id', name '#name', localLab '#localLab' correctly"() {
        given: "a form definition with id '#id', name '#name', localLab '#localLab'"
        def formDefinition = FormDefinitionObjectMother.aDefaultFormDefinitionBuilder()
                .withId(FormDefinitionId.of(id))
                .withName(name)
                .withLocalLab(localLab)
                .build()

        when: "mapping to mongo snapshot"
        def snapshot = formDefinition.toSnapshot()
        def mongoSnapshot = FormDefinitionDocument.fromSnapshot([snapshot]).get(0)

        then: "id is mapped to '#id'"
        mongoSnapshot.id == id
        and: "name is mapped to '#name'"
        mongoSnapshot.name == name
        and: "localLab is mapped to '#expectedLocalLab'"
        mongoSnapshot.localLab == expectedLocalLab

        where:
        id    | name     | localLab            || expectedLocalLab
        "123" | "Form 1" | LabName.of("LAB_1") || "LAB_1"
        "123" | "Form 1" | null                || null
    }
}
