package com.custodix.insite.local.ehr2edc.mongo.app.document.provenance

import com.custodix.insite.local.ehr2edc.provenance.model.ProvenanceDataPoint
import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import org.jeasy.random.EasyRandom
import spock.lang.Specification
import spock.lang.Unroll

class ProvenanceDataPointFromDocumentSpec extends Specification {
    private EasyRandom easyRandom = new EasyRandom()
    private ObjectMapper objectMapper = createObjectMapper()

    def "A ProvenanceDemographicDocument is mapped to ProvenanceDemographic"() {
        given: "A ProvenanceDemographicDocument having all fields populated"
        ProvenanceDemographicDocument provenanceDemographicDocument = easyRandom.nextObject(ProvenanceDemographicDocument)

        when: "I map the ProvenanceDemographicDocument to a ProvenanceDemographic"
        ProvenanceDataPoint provenanceDemographic = provenanceDemographicDocument.restore()

        then: "All fields are mapped"
        assertMapped(provenanceDemographic, provenanceDemographicDocument)
    }

    def "A ProvenanceLabValueDocument is mapped to ProvenanceLabValue"() {
        given: "A ProvenanceLabValueDocument having all fields populated"
        ProvenanceLabValueDocument provenanceLabValueDocument = easyRandom.nextObject(ProvenanceLabValueDocument)

        when: "I map the ProvenanceLabValueDocument to a ProvenanceLabValue"
        ProvenanceDataPoint provenanceLabValue = provenanceLabValueDocument.restore()

        then: "All fields are mapped"
        assertMapped(provenanceLabValue, provenanceLabValueDocument)
    }

    def "A ProvenanceVitalSignDocument is mapped to ProvenanceVitalSign"() {
        given: "A ProvenanceVitalSignDocument having all fields populated"
        ProvenanceVitalSignDocument provenanceVitalSignDocument = easyRandom.nextObject(ProvenanceVitalSignDocument)

        when: "I map the ProvenanceVitalSignDocument to a ProvenanceVitalSign"
        ProvenanceDataPoint provenanceVitalSign = provenanceVitalSignDocument.restore()

        then: "All fields are mapped"
        assertMapped(provenanceVitalSign, provenanceVitalSignDocument)
    }

    def "A ProvenanceMedicationDocument is mapped to ProvenanceMedication"() {
        given: "A ProvenanceMedicationDocument having all fields populated"
        ProvenanceMedicationDocument provenanceMedicationDocument = easyRandom.nextObject(ProvenanceMedicationDocument)

        when: "I map the ProvenanceMedicationDocument to a ProvenanceMedication"
        ProvenanceDataPoint provenanceMedication = provenanceMedicationDocument.restore()

        then: "All fields are mapped"
        assertMapped(provenanceMedication, provenanceMedicationDocument)
    }

    def "An empty ProvenanceDemographicDocument is mapped to ProvenanceDemographic"() {
        given: "An empty ProvenanceDemographicDocument"
        ProvenanceDemographicDocument provenanceDemographicDocument = ProvenanceDemographicDocument.newBuilder().build()

        when: "I map the ProvenanceDemographicDocument to a ProvenanceDemographic"
        ProvenanceDataPoint provenanceDemographic = provenanceDemographicDocument.restore()

        then: "All fields are mapped"
        provenanceDemographic != null
    }

    @Unroll
    def "An empty ProvenanceLabValueDocument is mapped to ProvenanceLabValue"(ProvenanceLabValueDocument provenanceLabValueDocument) {
        given: "An empty ProvenanceLabValueDocument"

        when: "I map the ProvenanceLabValueDocument to a ProvenanceLabValue"
        ProvenanceDataPoint provenanceLabValue = provenanceLabValueDocument.restore()

        then: "The value is mapped"
        provenanceLabValue != null

        where:
        provenanceLabValueDocument                                          | _
        ProvenanceLabValueDocument.newBuilder().build()                          | _
        ProvenanceLabValueDocument.newBuilder()
                .withLabConcept(LabConceptDocument.newBuilder().build()).build() | _
    }

    @Unroll
    def "An empty ProvenanceVitalSignDocument is mapped to ProvenanceVitalSign"(ProvenanceVitalSignDocument provenanceVitalSignDocument) {
        given: "An empty ProvenanceVitalSignDocument"

        when: "I map the ProvenanceVitalSignDocument to a ProvenanceVitalSign"
        ProvenanceDataPoint provenanceVitalSign = provenanceVitalSignDocument.restore()

        then: "The value is mapped"
        provenanceVitalSign != null

        where:
        provenanceVitalSignDocument                                            | _
        ProvenanceVitalSignDocument.newBuilder().build()                            | _
        ProvenanceVitalSignDocument.newBuilder()
                .withConcept(VitalSignConceptDocument.newBuilder().build()).build() | _
    }

    @Unroll
    def "An empty ProvenanceMedicationDocument is mapped to ProvenanceMedication"(ProvenanceMedicationDocument provenanceMedicationDocument) {
        given: "An empty ProvenanceMedicationDocument"

        when: "I map the ProvenanceMedicationDocument to a ProvenanceMedication"
        ProvenanceDataPoint provenanceMedication = provenanceMedicationDocument.restore()

        then: "The value is mapped"
        provenanceMedication != null

        where:
        provenanceMedicationDocument                                            | _
        ProvenanceMedicationDocument.newBuilder().build()                            | _
        ProvenanceMedicationDocument.newBuilder()
                .withConcept(MedicationConceptDocument.newBuilder().build()).build() | _
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
