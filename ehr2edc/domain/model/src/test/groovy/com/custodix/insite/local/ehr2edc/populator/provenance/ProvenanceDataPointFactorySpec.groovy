package com.custodix.insite.local.ehr2edc.populator.provenance

import com.custodix.insite.local.ehr2edc.provenance.model.ProvenanceDataPoint
import com.custodix.insite.local.ehr2edc.provenance.model.ProvenanceDataPointFactory
import com.custodix.insite.local.ehr2edc.query.executor.common.query.DataPoint
import com.custodix.insite.local.ehr2edc.query.executor.demographic.domain.Demographic
import com.custodix.insite.local.ehr2edc.query.executor.laboratory.domain.LabConcept
import com.custodix.insite.local.ehr2edc.query.executor.laboratory.domain.LabValue
import com.custodix.insite.local.ehr2edc.query.executor.medication.domain.Medication
import com.custodix.insite.local.ehr2edc.query.executor.medication.domain.MedicationConcept
import com.custodix.insite.local.ehr2edc.query.executor.vitalsign.domain.VitalSign
import com.custodix.insite.local.ehr2edc.query.executor.vitalsign.domain.VitalSignConcept
import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import org.jeasy.random.EasyRandom
import spock.lang.Specification
import spock.lang.Unroll

class ProvenanceDataPointFactorySpec extends Specification {
    private static final List<String> IGNORED_DEMOGRAPHIC = Arrays.asList("subjectId.id")
    private static final List<String> IGNORED_LAB_VALUE = Arrays.asList("subjectId.id")
    private static final List<String> IGNORED_VITAL_SIGN = Arrays.asList("subjectId.id")
    private static final List<String> IGNORED_MEDICATION = Arrays.asList("subjectId.id")

    private EasyRandom easyRandom = new EasyRandom()
    private ObjectMapper objectMapper = createObjectMapper()

    def "A Demographic is mapped to ProvenanceDemographic"() {
        given: "A Demographic having all fields populated"
        Demographic demographic = easyRandom.nextObject(Demographic)

        when: "I map the Demographic to a ProvenanceDemographic"
        ProvenanceDataPoint provenanceDemographic = ProvenanceDataPointFactory.create(demographic)

        then: "All fields are mapped except for the ignored fields"
        assertMapped(demographic, provenanceDemographic, IGNORED_DEMOGRAPHIC)
    }

    def "A LabValue is mapped to ProvenanceLabValue"() {
        given: "A LabValue having all fields populated"
        LabValue labValue = easyRandom.nextObject(LabValue)

        when: "I map the LabValue to a ProvenanceLabValue"
        ProvenanceDataPoint provenanceLabValue = ProvenanceDataPointFactory.create(labValue)

        then: "All fields are mapped except for the ignored fields"
        assertMapped(labValue, provenanceLabValue, IGNORED_LAB_VALUE)
    }

    def "A VitalSign is mapped to ProvenanceVitalSign"() {
        given: "A VitalSign having all fields populated"
        VitalSign vitalSign = easyRandom.nextObject(VitalSign)

        when: "I map the VitalSign to a ProvenanceVitalSign"
        ProvenanceDataPoint provenanceVitalSign = ProvenanceDataPointFactory.create(vitalSign)

        then: "All fields are mapped except for the ignored fields"
        assertMapped(vitalSign, provenanceVitalSign, IGNORED_VITAL_SIGN)
    }

    def "A Medication is mapped to ProvenanceMedication"() {
        given: "A Medication having all fields populated"
        Medication medication = easyRandom.nextObject(Medication)

        when: "I map the Medication to a ProvenanceMedication"
        ProvenanceDataPoint provenanceMedication = ProvenanceDataPointFactory.create(medication)

        then: "All fields are mapped except for the ignored fields"
        assertMapped(medication, provenanceMedication, IGNORED_MEDICATION)
    }

    def "An empty Demographic is mapped to ProvenanceDemographic"() {
        given: "An empty Demographic"
        Demographic demographic = Demographic.newBuilder().build()

        when: "I map the Demographic to a ProvenanceDemographic"
        ProvenanceDataPoint provenanceDemographic = ProvenanceDataPointFactory.create(demographic)

        then: "The value is mapped"
        provenanceDemographic != null
    }

    @Unroll
    def "An empty LabValue is mapped to ProvenanceLabValue"(LabValue labValue) {
        given: "An empty LabValue"

        when: "I map the LabValue to a ProvenanceLabValue"
        ProvenanceDataPoint provenanceLabValue = ProvenanceDataPointFactory.create(labValue)

        then: "The value is mapped"
        provenanceLabValue != null

        where:
        labValue                                                         | _
        LabValue.newBuilder().build()                                    | _
        LabValue.newBuilder()
                .withLabConcept(LabConcept.newBuilder().build()).build() | _
    }

    @Unroll
    def "An empty VitalSign is mapped to ProvenanceVitalSign"(VitalSign vitalSign) {
        given: "An empty VitalSign"

        when: "I map the VitalSign to a ProvenanceVitalSign"
        ProvenanceDataPoint provenanceVitalSign = ProvenanceDataPointFactory.create(vitalSign)

        then: "The value is mapped"
        provenanceVitalSign != null

        where:
        vitalSign                                                           | _
        VitalSign.newBuilder().build()                                      | _
        VitalSign.newBuilder()
                .withConcept(VitalSignConcept.newBuilder().build()).build() | _
    }

    @Unroll
    def "An empty Medication is mapped to ProvenanceMedication"(Medication medication) {
        given: "An empty Medication"

        when: "I map the Medication to a ProvenanceMedication"
        ProvenanceDataPoint provenanceMedication = ProvenanceDataPointFactory.create(medication)

        then: "The value is mapped"
        provenanceMedication != null

        where:
        medication                                                           | _
        Medication.newBuilder().build()                                      | _
        Medication.newBuilder()
                .withConcept(MedicationConcept.newBuilder().build()).build() | _
    }

    def "An empty DataPoint is mapped to an empty ProvenanceDataPoint"() {
        given: "An empty DataPoint"
        DataPoint dataPoint = null

        when: "I map the DataPoint to a ProvenanceDataPoint"
        ProvenanceDataPoint provenanceDataPoint = ProvenanceDataPointFactory.create(dataPoint)

        then: "The value is mapped"
        provenanceDataPoint == null
    }

    private assertMapped(DataPoint dataPoint, ProvenanceDataPoint provenanceDataPoint, List<String> ignoredFields) {
        def flatDataPoint = flatten(objectMapper.valueToTree(dataPoint))
        def flatProvenanceDataPoint = flatten(objectMapper.valueToTree(provenanceDataPoint))
        assert flatProvenanceDataPoint.size() == flatDataPoint.size() - ignoredFields.size()
        flatDataPoint.keySet().each { k ->
            assert ignoredFields.contains(k) ||
                    (flatProvenanceDataPoint.keySet().contains(k) && flatProvenanceDataPoint.get(k) == flatDataPoint.get(k))
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
