package com.custodix.insite.local.ehr2edc.rest.jackson

import com.custodix.insite.local.ehr2edc.query.executor.common.query.Query
import com.custodix.insite.local.ehr2edc.query.executor.common.query.criteria.*
import com.custodix.insite.local.ehr2edc.query.executor.demographic.domain.DemographicType
import com.custodix.insite.local.ehr2edc.query.executor.demographic.query.DemographicQuery
import com.custodix.insite.local.ehr2edc.query.executor.laboratory.query.LaboratoryQuery
import com.custodix.insite.local.ehr2edc.query.executor.medication.query.MedicationQuery
import com.custodix.insite.local.ehr2edc.query.executor.vitalsign.query.VitalSignQuery
import com.custodix.insite.local.ehr2edc.vocabulary.PatientCDWReference
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.core.io.ClassPathResource
import org.springframework.util.FileCopyUtils
import spock.lang.Specification
import spock.lang.Unroll

import java.time.Period

import static com.custodix.insite.local.ehr2edc.query.executor.common.concept.ConceptCode.conceptFor
import static com.custodix.insite.local.ehr2edc.query.executor.common.query.criteria.ConceptCriterion.*
import static com.custodix.insite.local.ehr2edc.query.executor.common.query.criteria.ExcludeConceptsCriterion.conceptIsNot
import static com.custodix.insite.local.ehr2edc.query.executor.common.query.criteria.ExcludeConceptsCriterion.conceptNotIn
import static java.util.Arrays.asList
import static org.assertj.core.api.Assertions.assertThat

class QueryMixinsTest extends Specification {
    private ObjectMapper objectMapper

    def setup() {
        objectMapper = new ObjectMapper()
        new JacksonWebConfiguration(objectMapper)
    }

    def "Serializing a demographic query"() {
        given: "A demographic query"
        Query query = new DemographicQuery()

        when: "The query gets serialized"
        def serializedQuery = objectMapper.writeValueAsString(query)

        then: "The serialized json contains the correct type"
        assertThat(serializedQuery).contains("\"type\":\"demographic\"")

        and: "The query can be de-serialized back correctly"
        def deSerializedValue = objectMapper.readValue(serializedQuery, query.getClass())
        assertThat(deSerializedValue).isEqualToComparingFieldByFieldRecursively(query)
    }

    @Unroll
    def "Serializing a demographic query with a type criterion"(DemographicType demographicType) {
        given: "A demographic query for #demographicType"
        Query query = new DemographicQuery()
        query.addCriterion(DemographicTypeCriterion.type(demographicType))

        when: "The query gets serialized"
        def serializedQuery = objectMapper.writeValueAsString(query)

        then: "The serialized json contains the correct type"
        assertThat(serializedQuery).contains(String.format("{\"type\":\"demographicType\",\"demographicType\":\"%s\"}", demographicType))

        and: "The query can be de-serialized back correctly"
        def deSerializedValue = objectMapper.readValue(serializedQuery, query.getClass())
        assertThat(deSerializedValue).isEqualToComparingFieldByFieldRecursively(query)

        where:
        demographicType << DemographicType.values()
    }

    @Unroll
    def "Serializing a laboratory query with concepts"(ConceptCriterion conceptCriterion) {
        given: "A laboratory query with concepts"
        Query query = new LaboratoryQuery()
        query.addCriterion(conceptCriterion)

        when: "The query gets serialized"
        def serializedQuery = objectMapper.writeValueAsString(query)

        then: "The serialized json contains the correct type"
        assertThat(serializedQuery).contains("\"type\":\"laboratory\"")

        and: "The query can be de-serialized back correctly"
        def deSerializedValue = objectMapper.readValue(serializedQuery, query.getClass())
        assertThat(deSerializedValue).isEqualToComparingFieldByFieldRecursively(query)

        where:
        conceptCriterion                                                                                               | _
        conceptIsExactly(conceptFor("ABC-123"))                                                                        | _
        conceptIn(asList(conceptFor("ABC-123"), conceptFor("ABC-456"), conceptFor("ABC-789")))                         | _
        conceptIsRelatedTo(conceptFor("ABC-123"), { concept -> asList(conceptFor("ABC-456"), conceptFor("ABC-789")) }) | _
        conceptIn(asList(conceptFor("ABC-123"), conceptFor("ABC-456"), conceptFor("ABC-789")), true)                   | _
    }

    @Unroll
    def "Serializing a laboratory query with excluded concepts"(ExcludeConceptsCriterion excludeConceptsCriterion) {
        given: "A laboratory query with excluded concepts"
        Query query = new LaboratoryQuery()
        query.addCriterion(excludeConceptsCriterion)

        when: "The query gets serialized"
        def serializedQuery = objectMapper.writeValueAsString(query)

        then: "The serialized json contains the correct type"
        assertThat(serializedQuery).contains("\"type\":\"laboratory\"")

        and: "The query can be de-serialized back correctly"
        def deSerializedValue = objectMapper.readValue(serializedQuery, query.getClass())
        assertThat(deSerializedValue).isEqualToComparingFieldByFieldRecursively(query)

        where:
        excludeConceptsCriterion                                                                  | _
        conceptIsNot(conceptFor("ABC-123"))                                                       | _
        conceptNotIn(asList(conceptFor("ABC-123"), conceptFor("ABC-456"), conceptFor("ABC-789"))) | _
    }

    def "Serializing a laboratory query with a freshness criterion"() {
        given: "A laboratory query with a freshness criterion"
        Query query = new LaboratoryQuery()
        query.addCriterion(FreshnessCriterion.maxAge(Period.parse("P1D")))

        when: "The query gets serialized"
        def serializedQuery = objectMapper.writeValueAsString(query)

        then: "The serialized json contains the correct type"
        assertThat(serializedQuery).contains("\"type\":\"laboratory\"")

        and: "The query can be de-serialized back correctly"
        def deSerializedValue = objectMapper.readValue(serializedQuery, query.getClass())
        assertThat(deSerializedValue).isEqualToComparingFieldByFieldRecursively(query)
    }

    @Unroll
    def "Serializing a query for a subject"(Query query, String queryType) {
        given: "A #queryType query for a subject"
        query.addCriterion(SubjectCriterion.subjectIs(SubjectId.of("subject-1")))

        when: "The query gets serialized"
        def serializedQuery = objectMapper.writeValueAsString(query)

        then: "The serialized json contains the correct type"
        assertThat(serializedQuery).contains("\"type\":\"" + queryType + "\"")

        and: "The query can be de-serialized back correctly"
        def deSerializedValue = objectMapper.readValue(serializedQuery, query.getClass())
        assertThat(deSerializedValue).isEqualToComparingFieldByFieldRecursively(query)

        where:
        query                  | queryType     | _
        new MedicationQuery()  | "medication"  | _
        new LaboratoryQuery()  | "laboratory"  | _
        new DemographicQuery() | "demographic" | _
        new VitalSignQuery()   | "vitalSign"   | _
    }

    @Unroll
    def "Deserializing a concept criterion"(String json, ConceptCriterion expectedConceptCriterion) {
        when: "Deserializing '#json' "
        ConceptCriterion criterion = objectMapper.readValue(json, Criterion.class)

        then: "concepts should match"
        criterion.concepts.size() == expectedConceptCriterion.concepts.size()
        criterion.concepts.containsAll(expectedConceptCriterion.concepts)
        and: "includeMissing should match"
        criterion.includeMissing == expectedConceptCriterion.includeMissing
        and: "Criterion equals should match"
        criterion == expectedConceptCriterion

        where:
        json                                                       || expectedConceptCriterion
        readFile("samples/criteria/conceptCriterion_full.json")    || conceptIn([conceptFor("386725007")], true)
        readFile("samples/criteria/conceptCriterion_null.json")    || conceptIn([conceptFor("386725007")], false)
        readFile("samples/criteria/conceptCriterion_missing.json") || conceptIn([conceptFor("386725007")], false)
    }

    @Unroll
    def "Deserializing an exclude concepts criterion"(String json, ExcludeConceptsCriterion expectedExcludeConceptsCriterion) {
        when: "Deserializing '#json' "
        ExcludeConceptsCriterion criterion = objectMapper.readValue(json, Criterion.class)

        then: "concepts should match"
        criterion.concepts.size() == expectedExcludeConceptsCriterion.concepts.size()
        criterion.concepts.containsAll(expectedExcludeConceptsCriterion.concepts)
        and: "Criterion equals should match"
        criterion == expectedExcludeConceptsCriterion

        where:
        json                                                       || expectedExcludeConceptsCriterion
        readFile("samples/criteria/excludeConceptsCriterion.json") || conceptIsNot(conceptFor("386725007"))
    }

    @Unroll
    def "Deserializing a ordinal criterion"(String json, OrdinalCriterion expectedOrdinalCriterion) {
        when: "Deserializing '#json'"
        OrdinalCriterion criterion = objectMapper.readValue(json, Criterion.class)

        then: "Ordinal should match"
        criterion.ordinal == expectedOrdinalCriterion.ordinal
        and: "Criterion equals should match"
        criterion == expectedOrdinalCriterion

        where:
        json                                                     || expectedOrdinalCriterion
        readFile("samples/criteria/ordinalCriterion_first.json") || OrdinalCriterion.ordinal(OrdinalCriterion.Ordinal.FIRST)
        readFile("samples/criteria/ordinalCriterion_last.json")  || OrdinalCriterion.ordinal(OrdinalCriterion.Ordinal.LAST)
    }

    @Unroll
    def "Deserializing a subject criterion"(String json, SubjectCriterion expectedSubjectCriterion) {
        when: "Deserializing '#json'"
        SubjectCriterion criterion = objectMapper.readValue(json, Criterion.class)

        then: "subjectId should match"
        criterion.subjectId == expectedSubjectCriterion.subjectId
        and: "patientCDWReference should match"
        criterion.patientCDWReference == expectedSubjectCriterion.patientCDWReference
        and: "Criterion equals should match"
        criterion == expectedSubjectCriterion

        where:
        json                                                                   || expectedSubjectCriterion
        readFile("samples/criteria/subjectCriterion_subjectId.json")           || SubjectCriterion.subjectIs(SubjectId.of("subject-1"))
        readFile("samples/criteria/subjectCriterion_patientCDWReference.json") || SubjectCriterion.subjectIs(PatientCDWReference.newBuilder().withSource("patient-source").withId("patient-1").build())
        readFile("samples/criteria/subjectCriterion_allFields.json")           || SubjectCriterion.subjectIs(SubjectId.of("subject-1"), PatientCDWReference.newBuilder().withSource("patient-source").withId("patient-1").build())
    }

    @Unroll
    def "Deserializing a demographic type criterion"(String json, DemographicTypeCriterion expectedDemographicTypeCriterion) {
        when: "Deserializing '#json'"
        DemographicTypeCriterion criterion = objectMapper.readValue(json, Criterion.class)

        then: "demographicType should match"
        criterion.demographicType == expectedDemographicTypeCriterion.demographicType
        and: "Criterion equals should match"
        criterion == expectedDemographicTypeCriterion

        where:
        json                                                       || expectedDemographicTypeCriterion
        readFile("samples/criteria/demographicTypeCriterion.json") || DemographicTypeCriterion.type(DemographicType.BIRTH_DATE)
    }

    @Unroll
    def "Deserializing a freshness criterion"(String json, FreshnessCriterion expectedFreshnessCriterion) {
        when: "Deserializing '#json'"
        FreshnessCriterion criterion = objectMapper.readValue(json, Criterion.class)

        then: "maxAge should match"
        criterion.maxAge == expectedFreshnessCriterion.maxAge
        and: "Criterion equals should match"
        criterion == expectedFreshnessCriterion

        where:
        json                                                 || expectedFreshnessCriterion
        readFile("samples/criteria/freshnessCriterion.json") || FreshnessCriterion.maxAge(Period.ofDays(1))
    }

    def readFile(String path) {
        return new String(FileCopyUtils.copyToByteArray(new ClassPathResource(path).getFile()))
    }
}