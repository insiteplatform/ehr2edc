package com.custodix.insite.local.ehr2edc.mongo.app.mapping

import com.custodix.insite.local.ehr2edc.metadata.model.FormDefinition
import com.custodix.insite.local.ehr2edc.vocabulary.FormDefinitionId
import com.custodix.insite.local.ehr2edc.vocabulary.LabName
import spock.lang.Specification
import spock.lang.Unroll

import static com.custodix.insite.local.ehr2edc.mongo.app.document.FormDefinitionDocumentObjectMother.aDefaultFormDefinitionDocumentBuilder

class FormDefinitionFromMongoSnapshotSpec extends Specification {
    @Unroll
    def "Mapping a form definition mongo snapshot without item group definitions with id '#id', name '#name', localLab '#localLab' correctly"() {
        given: "a form definition mongo snapshot with id '#id', name '#name', localLab '#localLab'"
        def formDefinitionMongoSnapshot = aDefaultFormDefinitionDocumentBuilder()
                .withId(id)
                .withName(name)
                .withLocalLab(localLab)
                .build()

        when: "mapping to form definition"
        def snapshot = formDefinitionMongoSnapshot.toSnapshot()
        def formDefinition = FormDefinition.restoreFrom(snapshot)

        then: "id is mapped to '#id'"
        formDefinition.id == FormDefinitionId.of(id)
        and: "name is mapped to '#name'"
        formDefinition.name == name
        and: "localLab is mapped to '#expectedLocalLab'"
        formDefinition.localLab == expectedLocalLab

        where:
        id    | name     | localLab || expectedLocalLab
        "123" | "Form 1" | "LAB_1"  || LabName.of("LAB_1")
        "123" | "Form 1" | null     || null
    }
}
