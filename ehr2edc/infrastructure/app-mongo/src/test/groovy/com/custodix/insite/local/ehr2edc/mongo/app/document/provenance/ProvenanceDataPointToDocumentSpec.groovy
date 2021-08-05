package com.custodix.insite.local.ehr2edc.mongo.app.document.provenance

import com.custodix.insite.local.ehr2edc.provenance.model.*
import com.custodix.insite.local.ehr2edc.shared.exceptions.SystemException
import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import org.jeasy.random.EasyRandom
import spock.lang.Specification
import spock.lang.Unroll

import static com.custodix.insite.local.ehr2edc.provenance.model.ProvenanceDataPointObjectMother.aUnknownProvenanceDataPoint

class ProvenanceDataPointToDocumentSpec extends Specification {
    private EasyRandom easyRandom = new EasyRandom()
    private ObjectMapper objectMapper = createObjectMapper()

    def "A ProvenanceDemographic is mapped to ProvenanceDemographicDocument"() {
        given: "A ProvenanceDemographic having all fields populated"
        ProvenanceDemographic provenanceDemographic = easyRandom.nextObject(ProvenanceDemographic)

        when: "I map the ProvenanceDemographic to a ProvenanceDemographicDocument"
        ProvenanceDataPointDocument provenanceDemographicDocument = ProvenanceDataPointDocumentFactory.create(provenanceDemographic)

        then: "All fields are mapped"
        assertMapped(provenanceDemographic, provenanceDemographicDocument)
    }

    def "A ProvenanceLabValue is mapped to ProvenanceLabValueDocument"() {
        given: "A ProvenanceLabValue having all fields populated"
        ProvenanceLabValue provenanceLabValue = easyRandom.nextObject(ProvenanceLabValue)

        when: "I map the ProvenanceLabValue to a ProvenanceLabValueDocument"
        ProvenanceDataPointDocument provenanceLabValueDocument = ProvenanceDataPointDocumentFactory.create(provenanceLabValue)

        then: "All fields are mapped"
        assertMapped(provenanceLabValue, provenanceLabValueDocument)
    }

    def "A ProvenanceVitalSign is mapped to ProvenanceVitalSignDocument"() {
        given: "A ProvenanceVitalSign having all fields populated"
        ProvenanceVitalSign provenanceVitalSign = easyRandom.nextObject(ProvenanceVitalSign)

        when: "I map the ProvenanceVitalSign to a ProvenanceVitalSignDocument"
        ProvenanceDataPointDocument provenanceVitalSignDocument = ProvenanceDataPointDocumentFactory.create(provenanceVitalSign)

        then: "All fields are mapped"
        assertMapped(provenanceVitalSign, provenanceVitalSignDocument)
    }

    def "A ProvenanceMedication is mapped to ProvenanceMedicationDocument"() {
        given: "A ProvenanceMedication having all fields populated"
        ProvenanceMedication provenanceMedication = easyRandom.nextObject(ProvenanceMedication)

        when: "I map the ProvenanceMedication to a ProvenanceMedicationDocument"
        ProvenanceDataPointDocument provenanceMedicationDocument = ProvenanceDataPointDocumentFactory.create(provenanceMedication)

        then: "All fields are mapped"
        assertMapped(provenanceMedication, provenanceMedicationDocument)
    }

    def "An empty ProvenanceDemographic is mapped to ProvenanceDemographicDocument"() {
        given: "An empty ProvenanceDemographic"
        ProvenanceDemographic provenanceDemographic = ProvenanceDemographic.newBuilder().build()

        when: "I map the ProvenanceDemographic to a ProvenanceDemographicDocument"
        ProvenanceDataPointDocument provenanceDemographicDocument = ProvenanceDataPointDocumentFactory.create(provenanceDemographic)

        then: "The value is mapped"
        provenanceDemographicDocument != null
    }

    @Unroll
    def "An empty ProvenanceLabValue is mapped to ProvenanceLabValueDocument"(ProvenanceLabValue provenanceLabValue) {
        given: "An empty ProvenanceLabValue"

        when: "I map the ProvenanceLabValue to a ProvenanceLabValueDocument"
        ProvenanceDataPointDocument provenanceLabValueDocument = ProvenanceDataPointDocumentFactory.create(provenanceLabValue)

        then: "The value is mapped"
        provenanceLabValueDocument != null

        where:
        provenanceLabValue                                               | _
        ProvenanceLabValue.newBuilder().build()                          | _
        ProvenanceLabValue.newBuilder()
                .withLabConcept(LabConcept.newBuilder().build()).build() | _
    }

    @Unroll
    def "An empty ProvenanceVitalSign is mapped to ProvenanceVitalSignDocument"(ProvenanceVitalSign provenanceVitalSign) {
        given: "An empty ProvenanceVitalSign"

        when: "I map the ProvenanceVitalSign to a ProvenanceVitalSignDocument"
        ProvenanceDataPointDocument provenanceVitalSignDocument = ProvenanceDataPointDocumentFactory.create(provenanceVitalSign)

        then: "The value is mapped"
        provenanceVitalSignDocument != null

        where:
        provenanceVitalSign                                                 | _
        ProvenanceVitalSign.newBuilder().build()                            | _
        ProvenanceVitalSign.newBuilder()
                .withConcept(VitalSignConcept.newBuilder().build()).build() | _
    }

    @Unroll
    def "An empty ProvenanceMedication is mapped to ProvenanceMedicationDocument"(ProvenanceMedication provenanceMedication) {
        given: "An empty ProvenanceMedication"

        when: "I map the ProvenanceMedication to a ProvenanceMedicationDocument"
        ProvenanceDataPointDocument provenanceMedicationDocument = ProvenanceDataPointDocumentFactory.create(provenanceMedication)

        then: "The value is mapped"
        provenanceMedicationDocument != null

        where:
        provenanceMedication                                                 | _
        ProvenanceMedication.newBuilder().build()                            | _
        ProvenanceMedication.newBuilder()
                .withConcept(MedicationConcept.newBuilder().build()).build() | _
    }

    def "An empty ProvenanceDataPoint is mapped to an empty ProvenanceDataPointDocument"() {
        given: "An empty ProvenanceDataPoint"
        ProvenanceDataPoint provenanceDataPoint = null

        when: "I map the ProvenanceDataPoint to a ProvenanceDataPointDocument"
        ProvenanceDataPointDocument provenanceDataPointDocument = ProvenanceDataPointDocumentFactory.create(provenanceDataPoint)

        then: "The value is mapped"
        provenanceDataPointDocument == null
    }

    def "An unknown ProvenanceDataPoint cannot be mapped to a ProvenanceDataPointDocument"() {
        given: "An unknown ProvenanceDataPoint"
        ProvenanceDataPoint provenanceDataPoint = aUnknownProvenanceDataPoint()

        when: "I map the ProvenanceDataPoint to a ProvenanceDataPointDocument"
        ProvenanceDataPointDocumentFactory.create(provenanceDataPoint)

        then: "A SystemException is thrown"
        thrown SystemException
    }

    private assertMapped(ProvenanceDataPoint provenanceDataPoint, ProvenanceDataPointDocument provenanceDataPointDocument) {
        def flatDataPoint = flatten(objectMapper.valueToTree(provenanceDataPoint))
        def flatProvenanceDataPoint = flatten(objectMapper.valueToTree(provenanceDataPointDocument))
        assert flatProvenanceDataPoint.size() == flatDataPoint.size()
        flatDataPoint.keySet().each { k ->
            assert flatProvenanceDataPoint.keySet().contains(k) && flatProvenanceDataPoint.get(k) == flatDataPoint.get(k)
        }
    }

    private Map<String, Object> flatten(JsonNode json) {
        def flatMap = new HashMap<String, Object>()
        flatten(json, flatMap, null)
        return flatMap.sort()
    }

    private void flatten(JsonNode json, Map<String, Object> target, String path) {
        Iterator<Map.Entry<String, JsonNode>> it = json.fields()
        while (it.hasNext()) {
            Map.Entry<String, JsonNode> field = it.next()
            String key = field.key
            JsonNode value = field.value
            if (value instanceof ObjectNode) {
                flatten(value, target, (path == null ? key : (path + "." + key)))
            } else {
                target[(path == null ? key : (path + "." + key))] = value
            }
        }
    }

    private static ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper()
        mapper.setVisibility(mapper.getSerializationConfig().getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withCreatorVisibility(JsonAutoDetect.Visibility.NONE))
    }
}
