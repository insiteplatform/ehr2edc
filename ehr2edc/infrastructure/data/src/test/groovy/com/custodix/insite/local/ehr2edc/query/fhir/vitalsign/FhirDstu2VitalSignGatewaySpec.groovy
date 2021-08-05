package com.custodix.insite.local.ehr2edc.query.fhir.vitalsign

import com.custodix.insite.local.ehr2edc.populator.EHRGateway
import com.custodix.insite.local.ehr2edc.query.executor.common.concept.ConceptCode
import com.custodix.insite.local.ehr2edc.query.executor.common.query.Query
import com.custodix.insite.local.ehr2edc.query.executor.common.query.criteria.OrdinalCriterion
import com.custodix.insite.local.ehr2edc.query.executor.common.query.dsl.QueryDsl
import com.custodix.insite.local.ehr2edc.query.executor.common.query.dsl.VitalSignQueryDsl
import com.custodix.insite.local.ehr2edc.query.executor.vitalsign.domain.VitalSign
import com.custodix.insite.local.ehr2edc.query.executor.vitalsign.query.VitalSigns
import com.custodix.insite.local.ehr2edc.query.fhir.AbstractFhirEHRGatewayWithWiremockSpec
import com.custodix.insite.local.ehr2edc.vocabulary.PatientCDWReference
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId
import spock.lang.Unroll

import java.time.*

import static com.custodix.insite.local.ehr2edc.query.executor.common.concept.ConceptCode.conceptFor
import static com.custodix.insite.local.ehr2edc.vocabulary.SubjectIdObjectMother.aRandomSubjectId
import static com.github.tomakehurst.wiremock.client.WireMock.*

class FhirDstu2VitalSignGatewaySpec extends AbstractFhirEHRGatewayWithWiremockSpec {
    private static final String OBSERVATION_URL = "/baseDstu2/Observation"
    private static final SubjectId SUSANNE_SUBJECT_ID = aRandomSubjectId()

    private PatientCDWReference susannePatient

    def setup() {
        susannePatient = patientSusanne()
    }

    def "Get a patient's vital signs without observations in fhir"() {
        given: "A patient Susanne with zero observations"
        stubObservationForSusanne("with_0_observations")

        when: "I query the vital signs for Susanne"
        Query<VitalSigns> query = buildVitalSignQuery().getQuery()
        EHRGateway gateway = fhirEHRGatewayFactory.create(query, POPULATION_SPECIFICATION)
        VitalSigns vitalSigns = (VitalSigns) gateway.execute(query, REFERENCE_DATE)

        then: "I expect exactly 0 vital signs"
        vitalSigns.getValues().size() == 0
    }

    def "Get a patient's vital sign"() {
        given: "A patient Susanne with an observation"
        stubObservationForSusanne()

        when: "I query the vital signs for Susanne"
        Query<VitalSigns> query = buildVitalSignQuery().getQuery()
        def gateway = fhirEHRGatewayFactory.create(query, POPULATION_SPECIFICATION)
        VitalSigns vitalSigns = (VitalSigns) gateway.execute(query, REFERENCE_DATE)

        then: "I expect exactly 1 vital sign"
        vitalSigns.getValues().size() == 1
        VitalSign vitalSign = vitalSigns.getValues().get(0)
        and: "the vital sign has a subject id"
        vitalSign.subjectId == SUSANNE_SUBJECT_ID
        and: "the vital sign has code 47625008"
        vitalSign.concept.code == "47625008"
    }

    def "Get a patient's vital sign that has an attribute 'effectiveDateTime'"() {
        given: "A patient Susanne with an observation that has an attribute 'effectiveDateTime' with value 2008-07-10T10:38:49+01:00"
        stubObservationForSusanne("effective_datetime")

        when: "I query the vital signs for Susanne"
        Query<VitalSigns> query = buildVitalSignQuery().getQuery()
        def gateway = fhirEHRGatewayFactory.create(query, POPULATION_SPECIFICATION)
        VitalSigns vitalSigns = (VitalSigns) gateway.execute(query, REFERENCE_DATE)

        then: "I expect exactly 1 vital sign"
        vitalSigns.getValues().size() == 1
        VitalSign vitalSign = vitalSigns.getValues().get(0)
        and: "the vital sign has an effective date 2008-07-10T10:38:49+01:00"
        vitalSign.effectiveDateTime == toLocalDateTime("2008-07-10T10:38:49+01:00")
    }

    def "Get a patient's vital sign that has an attribute 'effectivePeriod' with start and end date"() {
        given: "A patient Susanne with an observation that has an attribute 'effectivePeriod' with start 2008-07-10T10:38:49+01:00 and end 2008-07-11T10:38:49+01:00"
        stubObservationForSusanne("effective_period")

        when: "I query the vital signs for Susanne"
        Query<VitalSigns> query = buildVitalSignQuery().getQuery()
        def gateway = fhirEHRGatewayFactory.create(query, POPULATION_SPECIFICATION)
        VitalSigns vitalSigns = (VitalSigns) gateway.execute(query, REFERENCE_DATE)

        then: "I expect exactly 1 vital sign"
        vitalSigns.getValues().size() == 1
        VitalSign vitalSign = vitalSigns.getValues().get(0)
        and: "the vital sign has an effective date 2008-07-10T10:38:49+01:00"
        vitalSign.effectiveDateTime == toLocalDateTime("2008-07-10T10:38:49+01:00")
    }

    def "Get a patient's vital sign that has an attribute 'effectivePeriod' with start date but missing end date"() {
        given: "A patient Susanne with an observation that has an attribute 'effectivePeriod' with start 2008-07-10T10:38:49+01:00 and no end"
        stubObservationForSusanne("effective_period_end_missing")

        when: "I query the vital signs for Susanne"
        Query<VitalSigns> query = buildVitalSignQuery().getQuery()
        def gateway = fhirEHRGatewayFactory.create(query, POPULATION_SPECIFICATION)
        VitalSigns vitalSigns = (VitalSigns) gateway.execute(query, REFERENCE_DATE)

        then: "I expect exactly 1 vital sign"
        vitalSigns.getValues().size() == 1
        VitalSign vitalSign = vitalSigns.getValues().get(0)
        and: "the vital sign has an effective date 2008-07-10T10:38:49+01:00"
        vitalSign.effectiveDateTime == toLocalDateTime("2008-07-10T10:38:49+01:00")
    }

    def "Get a patient's vital sign that has an attribute 'effectivePeriod' with end date but missing start date"() {
        given: "A patient Susanne with an observation that has an attribute 'effectivePeriod' with no start and end 2008-07-11T10:38:49+01:00"
        stubObservationForSusanne("effective_period_start_missing")

        when: "I query the vital signs for Susanne"
        Query<VitalSigns> query = buildVitalSignQuery().getQuery()
        def gateway = fhirEHRGatewayFactory.create(query, POPULATION_SPECIFICATION)
        VitalSigns vitalSigns = (VitalSigns) gateway.execute(query, REFERENCE_DATE)

        then: "I expect exactly 1 vital sign"
        vitalSigns.getValues().size() == 1
        VitalSign vitalSign = vitalSigns.getValues().get(0)
        and: "the vital sign has an effective date 2008-07-11T10:38:49+01:00"
        vitalSign.effectiveDateTime == toLocalDateTime("2008-07-11T10:38:49+01:00")
    }

    def "Get a patient's vital sign that has an attribute 'valueQuantity' with fields 'value', 'code' and 'unit'"() {
        given: "A patient Susanne with an observation that has an attribute 'valueQuantity' with value '66', unit 'milligrams per decilitre' and code 'mg/dL'"
        stubObservationForSusanne("value_quantity")

        when: "I query the vital signs for Susanne"
        Query<VitalSigns> query = buildVitalSignQuery().getQuery()
        def gateway = fhirEHRGatewayFactory.create(query, POPULATION_SPECIFICATION)
        VitalSigns vitalSigns = (VitalSigns) gateway.execute(query, REFERENCE_DATE)

        then: "I expect exactly 1 vital sign"
        vitalSigns.getValues().size() == 1
        VitalSign vitalSign = vitalSigns.getValues().get(0)
        and: "the vital sign has a measurement value '66'"
        vitalSign.measurement.value == new BigDecimal("66")
        and: "the vital sign has a measurement unit 'mg/dL'"
        vitalSign.measurement.unit == "mg/dL"
    }

    def "Get a patient's vital sign that has an attribute 'valueQuantity' with fields 'value' and 'unit' but missing 'code'"() {
        given: "A patient Susanne with an observation that has an attribute 'valueQuantity' with value '66' and unit 'milligrams per decilitre'"
        stubObservationForSusanne("value_quantity_code_missing")

        when: "I query the vital signs for Susanne"
        Query<VitalSigns> query = buildVitalSignQuery().getQuery()
        def gateway = fhirEHRGatewayFactory.create(query, POPULATION_SPECIFICATION)
        VitalSigns vitalSigns = (VitalSigns) gateway.execute(query, REFERENCE_DATE)

        then: "I expect exactly 1 vital sign"
        vitalSigns.getValues().size() == 1
        VitalSign vitalSign = vitalSigns.getValues().get(0)
        and: "the vital sign has a measurement value '66'"
        vitalSign.measurement.value == new BigDecimal("66")
        and: "the vital sign has a measurement unit 'milligrams per decilitre'"
        vitalSign.measurement.unit == "milligrams per decilitre"
    }

    def "Get a patient's vital sign for which an attribute 'value[x]' is missing"() {
        given: "A patient Susanne with an observation for which an attribute 'value[x]' is missing"
        stubObservationForSusanne("value_quantity_missing")

        when: "I query the vital signs for Susanne"
        Query<VitalSigns> query = buildVitalSignQuery().getQuery()
        def gateway = fhirEHRGatewayFactory.create(query, POPULATION_SPECIFICATION)
        VitalSigns vitalSigns = (VitalSigns) gateway.execute(query, REFERENCE_DATE)

        then: "I expect exactly 1 vital sign"
        vitalSigns.getValues().size() == 1
        VitalSign vitalSign = vitalSigns.getValues().get(0)
        and: "the vital sign has no measurement value"
        vitalSign.measurement.value == null
        and: "the vital sign has no measurement unit"
        vitalSign.measurement.unit == null
    }

    def "Get a patient's vital sign that has an attribute 'referenceRange' with fields 'low' and 'high'"() {
        given: "A patient Susanne with an observation that has an attribute 'referenceRange' with low value '50' and high value '75'"
        stubObservationForSusanne("reference_range")

        when: "I query the vital signs for Susanne"
        Query<VitalSigns> query = buildVitalSignQuery().getQuery()
        def gateway = fhirEHRGatewayFactory.create(query, POPULATION_SPECIFICATION)
        VitalSigns vitalSigns = (VitalSigns) gateway.execute(query, REFERENCE_DATE)

        then: "I expect exactly 1 vital sign"
        vitalSigns.getValues().size() == 1
        VitalSign vitalSign = vitalSigns.getValues().get(0)
        and: "the vital sign has a reference range lower limit '50'"
        vitalSign.measurement.lowerLimit == new BigDecimal("50")
        and: "the vital sign has a reference range upper limit '75'"
        vitalSign.measurement.upperLimit == new BigDecimal("75")
    }

    def "Get a patient's vital sign that has an attribute 'referenceRange' with field 'low' but missing 'high'"() {
        given: "A patient Susanne with an observation that has an attribute 'referenceRange' with low value '50'"
        stubObservationForSusanne("reference_range_high_missing")

        when: "I query the vital signs for Susanne"
        Query<VitalSigns> query = buildVitalSignQuery().getQuery()
        def gateway = fhirEHRGatewayFactory.create(query, POPULATION_SPECIFICATION)
        VitalSigns vitalSigns = (VitalSigns) gateway.execute(query, REFERENCE_DATE)

        then: "I expect exactly 1 vital sign"
        vitalSigns.getValues().size() == 1
        VitalSign vitalSign = vitalSigns.getValues().get(0)
        and: "the vital sign has a reference range lower limit '50'"
        vitalSign.measurement.lowerLimit == new BigDecimal("50")
        and: "the vital sign has no reference range upper limit"
        vitalSign.measurement.upperLimit == null
    }

    def "Get a patient's vital sign that has an attribute 'referenceRange' with field 'high' but missing 'low'"() {
        given: "A patient Susanne with an observation that has an attribute 'referenceRange' with high value '75'"
        stubObservationForSusanne("reference_range_low_missing")

        when: "I query the vital signs for Susanne"
        Query<VitalSigns> query = buildVitalSignQuery().getQuery()
        def gateway = fhirEHRGatewayFactory.create(query, POPULATION_SPECIFICATION)
        VitalSigns vitalSigns = (VitalSigns) gateway.execute(query, REFERENCE_DATE)

        then: "I expect exactly 1 vital sign"
        vitalSigns.getValues().size() == 1
        VitalSign vitalSign = vitalSigns.getValues().get(0)
        and: "the vital sign has no reference range lower limit"
        vitalSign.measurement.lowerLimit == null
        and: "the vital sign has reference range upper limit '75'"
        vitalSign.measurement.upperLimit == new BigDecimal("75")
    }

    def "Get a patient's vital sign for which an attribute 'referenceRange' is missing"() {
        given: "A patient Susanne with an observation for which an attribute 'referenceRange' is missing"
        stubObservationForSusanne("reference_range_missing")

        when: "I query the vital signs for Susanne"
        Query<VitalSigns> query = buildVitalSignQuery().getQuery()
        def gateway = fhirEHRGatewayFactory.create(query, POPULATION_SPECIFICATION)
        VitalSigns vitalSigns = (VitalSigns) gateway.execute(query, REFERENCE_DATE)

        then: "I expect exactly 1 vital sign"
        vitalSigns.getValues().size() == 1
        VitalSign vitalSign = vitalSigns.getValues().get(0)
        and: "the vital sign has no reference range lower limit"
        vitalSign.measurement.lowerLimit == null
        and: "the vital sign has no reference range upper limit"
        vitalSign.measurement.upperLimit == null
    }

    def "Get a patient's vital sign that has an attribute 'bodySite' with a non-laterality code"() {
        given: "A patient Susanne with an observation that has an attribute 'bodySite' with code '40983000'"
        stubObservationForSusanne("body_site_location")

        when: "I query the vital signs for Susanne"
        Query<VitalSigns> query = buildVitalSignQuery().getQuery()
        def gateway = fhirEHRGatewayFactory.create(query, POPULATION_SPECIFICATION)
        VitalSigns vitalSigns = (VitalSigns) gateway.execute(query, REFERENCE_DATE)

        then: "I expect exactly 1 vital sign"
        vitalSigns.getValues().size() == 1
        VitalSign vitalSign = vitalSigns.getValues().get(0)
        and: "the vital sign has location '40983000'"
        vitalSign.vitalSignConcept.location == "40983000"
    }

    def "Get a patient's vital sign that has an attribute 'bodySite' with a laterality code"() {
        given: "A patient Susanne with an observation that has an attribute 'bodySite' with code '24028007'"
        stubObservationForSusanne("body_site_laterality")

        when: "I query the vital signs for Susanne"
        Query<VitalSigns> query = buildVitalSignQuery().getQuery()
        def gateway = fhirEHRGatewayFactory.create(query, POPULATION_SPECIFICATION)
        VitalSigns vitalSigns = (VitalSigns) gateway.execute(query, REFERENCE_DATE)

        then: "I expect exactly 1 vital sign"
        vitalSigns.getValues().size() == 1
        VitalSign vitalSign = vitalSigns.getValues().get(0)
        and: "the vital sign has laterality '24028007'"
        vitalSign.vitalSignConcept.laterality == "24028007"
    }

    def "Get a patient's vital sign that has an attribute 'bodySite' with a non-laterality code and a laterality code"() {
        given: "A patient Susanne with an observation that has an attribute 'bodySite' with codes '40983000' and '24028007'"
        stubObservationForSusanne("body_site_location_and_laterality")

        when: "I query the vital signs for Susanne"
        Query<VitalSigns> query = buildVitalSignQuery().getQuery()
        def gateway = fhirEHRGatewayFactory.create(query, POPULATION_SPECIFICATION)
        VitalSigns vitalSigns = (VitalSigns) gateway.execute(query, REFERENCE_DATE)

        then: "I expect exactly 1 vital sign"
        vitalSigns.getValues().size() == 1
        VitalSign vitalSign = vitalSigns.getValues().get(0)
        and: "the vital sign has location '40983000'"
        vitalSign.vitalSignConcept.location == "40983000"
        and: "the vital sign has laterality '24028007'"
        vitalSign.vitalSignConcept.laterality == "24028007"
    }

    def "Get a patient's vital sign that has an attribute 'components'"() {
        given: "A patient Susanne with an observation that has components for systolic and diastolic blood pressure with values 107/65 mm[Hg] and reference ranges 90-120/60-80"
        stubObservationForSusanne("components")

        when: "I query the vital signs for Susanne"
        Query<VitalSigns> query = buildVitalSignQuery().getQuery()
        def gateway = fhirEHRGatewayFactory.create(query, POPULATION_SPECIFICATION)
        VitalSigns vitalSigns = gateway.execute(query, REFERENCE_DATE)

        then: "I expect exactly 2 vital signs"
        vitalSigns.getValues().size() == 2
        and: "the systolic blood pressure with code 271649006 has value 107 mm[Hg] and reference range 90-120"
        VitalSign systolic = vitalSigns.values.find { it.concept.code == "271649006" }
        systolic.measurement.value == new BigDecimal("107")
        systolic.measurement.unit == "mm[Hg]"
        systolic.measurement.lowerLimit == new BigDecimal("90")
        systolic.measurement.upperLimit == new BigDecimal("120")
        and: "the diastolic blood pressure with code 271650006 has value 65 mm[Hg] and reference range 60-80"
        VitalSign diastolic = vitalSigns.values.find { it.concept.code == "271650006" }
        diastolic.measurement.value == new BigDecimal("65")
        diastolic.measurement.unit == "mm[Hg]"
        diastolic.measurement.lowerLimit == new BigDecimal("60")
        diastolic.measurement.upperLimit == new BigDecimal("80")
    }

    @Unroll
    def "Get a patient's #ordinal vital sign "(OrdinalCriterion.Ordinal ordinal, String sortParam, String sortValue, String countParam, String countValue) {
        given: "A patient Susanne with an observation"
        def expectedParams = [(sortParam): [sortValue], (countParam): [countValue]]
        stubObservationForSusanne("bmi", expectedParams)

        when: "I query Susanne's #ordinal vital sign"
        Query<VitalSigns> query = buildVitalSignQuery().forOrdinal(ordinal).getQuery()
        def gateway = fhirEHRGatewayFactory.create(query, POPULATION_SPECIFICATION)
        gateway.execute(query, REFERENCE_DATE)

        then: "The fhir endpoint is called correctly"
        verifyRESTCall(expectedParams)

        where:
        ordinal                        || sortParam    | sortValue | countParam | countValue
        OrdinalCriterion.Ordinal.LAST  || "_sort:desc" | "date"    | "_count"   | "1"
        OrdinalCriterion.Ordinal.FIRST || "_sort:asc"  | "date"    | "_count"   | "1"
    }

    @Unroll
    def "Get a patient's #ordinal vital sign when fhir does not respect the ordinal criterion"(OrdinalCriterion.Ordinal ordinal, ConceptCode expectedCode) {
        given: "A patient Susanne with an observation"
        stubObservationForSusanne("bmi_and_heartrate")

        when: "I query Susanne's #ordinal vital sign"
        Query<VitalSigns> query = buildVitalSignQuery().forOrdinal(ordinal).getQuery()
        def gateway = fhirEHRGatewayFactory.create(query, POPULATION_SPECIFICATION)
        VitalSigns vitalSigns = (VitalSigns) gateway.execute(query, REFERENCE_DATE)

        then: "The observation matches the expected value"
        vitalSigns.getValues().size() == 1
        vitalSigns.getValues().first().concept == expectedCode

        where:
        ordinal                        || expectedCode
        OrdinalCriterion.Ordinal.LAST  || conceptFor("364075005")
        OrdinalCriterion.Ordinal.FIRST || conceptFor("47625008")
    }

    @Unroll
    def "Get a patient's #ordinal vital sign when the #ordinal Observation contains multiple components"(OrdinalCriterion.Ordinal ordinal) {
        given: "A patient Susanne with an observation that has components for 271649006 and 271650006"
        stubObservationForSusanne("components")

        when: "I query Susanne's #ordinal vital sign"
        Query<VitalSigns> query = buildVitalSignQuery().forOrdinal(ordinal).getQuery()
        def gateway = fhirEHRGatewayFactory.create(query, POPULATION_SPECIFICATION)
        VitalSigns vitalSigns = (VitalSigns) gateway.execute(query, REFERENCE_DATE)

        then: "I receive only 1 vital sign"
        vitalSigns.getValues().size() == 1

        where:
        ordinal << OrdinalCriterion.Ordinal.values()
    }

    @Unroll
    def "Get a patient's vital signs which were recorded at most #maxAge before the reference date #referenceDate"(Period maxAge, LocalDate referenceDate, String expectedGreaterThan, String expectedLessThan) {
        given: "A patient Susanne with an observation"
        def expectedParams = ["date": ["ge${expectedGreaterThan}", "lt${expectedLessThan}"]]
        stubObservationForSusanne("bmi", expectedParams)

        when: "I query Susanne's vital signs which were recorded at most #maxAge before the reference date #referenceDate"
        Query<VitalSigns> query = buildVitalSignQuery().freshFor(maxAge).getQuery()
        def gateway = fhirEHRGatewayFactory.create(query, POPULATION_SPECIFICATION)
        gateway.execute(query, referenceDate)

        then: "The fhir endpoint is called correctly"
        verifyRESTCall(expectedParams)

        where:
        maxAge            | referenceDate                 || expectedGreaterThan         | expectedLessThan
        Period.ofDays(1)  | LocalDate.parse("2020-01-24") || "2020-01-24T00:00:00+01:00" | "2020-01-25T00:00:00+01:00"
        Period.ofWeeks(1) | LocalDate.parse("2020-01-01") || "2019-12-25T00:00:00+01:00" | "2020-01-02T00:00:00+01:00"
    }

    @Unroll
    def "Get a patient's vital signs of which the concept code is one of #codes with includeMissing '#includeMissing'"(
            List<ConceptCode> codes, boolean includeMissing, List<ConceptCode> expectedCodes) {
        given: "A patient Susanne with an observation for code 47625008"
        stubObservationForSusanne()

        when: "I query Susanne's vital signs of which the concept code is one of #codes with includeMissing '#includeMissing'"
        Query<VitalSigns> query = buildVitalSignQuery().forConcepts(codes, includeMissing).getQuery()
        def gateway = fhirEHRGatewayFactory.create(query, POPULATION_SPECIFICATION)
        VitalSigns vitalSigns = (VitalSigns) gateway.execute(query, REFERENCE_DATE)

        then: "The retrieved vital sign codes are #expectedCodes"
        vitalSigns.getValues().collect({ l -> l.concept }) == expectedCodes

        where:
        codes                                             | includeMissing || expectedCodes
        [conceptFor("47625008"), conceptFor("364075005")] | false          || [conceptFor("47625008")]
        [conceptFor("47625008"), conceptFor("364075005")] | true           || [conceptFor("47625008"), conceptFor("364075005")]
    }

    @Unroll
    def "Get a patient's vital signs of which the concept code is one of #codes"(List<ConceptCode> codes, String expectedParamValue) {
        given: "A patient Susanne with an observation for code 47625008 and an observation for code 364075005"
        def expectedParamsCoreQuery = ["code": [expectedParamValue]]
        stubObservationForSusanne("bmi_and_heartrate", expectedParamsCoreQuery)
        def expectedParamsComponentCodeQuery = ["component-code": [expectedParamValue]]
        stubObservationForSusanne("with_0_observations", expectedParamsComponentCodeQuery)

        when: "I query Susanne's vital signs of which the concept code is one of #codes"
        Query<VitalSigns> query = buildVitalSignQuery().forConcepts(codes).getQuery()
        def gateway = fhirEHRGatewayFactory.create(query, POPULATION_SPECIFICATION)
        gateway.execute(query, REFERENCE_DATE)

        then: "The fhir endpoint is called correctly"
        verifyRESTCall(expectedParamsCoreQuery)
        verifyRESTCall(expectedParamsComponentCodeQuery)

        where:
        codes                                             || expectedParamValue
        [conceptFor("47625008")]                          || "47625008"
        [conceptFor("47625008"), conceptFor("364075005")] || "47625008,364075005"
    }

    @Unroll
    def "Get a patient's vital signs of which the concept code is one of #codes when FHIR does not respect the concept criterion"(List<ConceptCode> codes) {
        given: "A patient Susanne with an observation for code 47625008 and an observation for code 364075005"
        stubObservationForSusanne("bmi_and_heartrate")

        when: "I query Susanne's vital signs of which the concept code is one of #codes"
        Query<VitalSigns> query = buildVitalSignQuery().forConcepts(codes).getQuery()
        def gateway = fhirEHRGatewayFactory.create(query, POPULATION_SPECIFICATION)
        VitalSigns vitalSigns = (VitalSigns) gateway.execute(query, REFERENCE_DATE)

        then: "The retrieved vital sign codes are #codes"
        vitalSigns.getValues().collect({ l -> l.concept }) == codes

        where:
        codes                                             | _
        [conceptFor("47625008")]                          | _
        [conceptFor("47625008"), conceptFor("364075005")] | _
    }

    @Unroll
    def "Get a patient's vital signs of which the concept code is one of #codes filters out any components that are not relevant"(List<ConceptCode> codes) {
        given: "A patient Susanne with an observation that has components for 271649006 and 271650006"
        stubObservationForSusanne("components")

        when: "I query Susanne's vital signs of which the concept code is one of #codes"
        Query<VitalSigns> query = buildVitalSignQuery().forConcepts(codes).getQuery()
        def gateway = fhirEHRGatewayFactory.create(query, POPULATION_SPECIFICATION)
        VitalSigns vitalSigns = (VitalSigns) gateway.execute(query, REFERENCE_DATE)

        then: "The retrieved vital sign codes are #codes"
        vitalSigns.getValues().collect({ l -> l.concept }) == codes

        where:
        codes                                              | _
        [conceptFor("271649006")]                          | _
        [conceptFor("271649006"), conceptFor("271650006")] | _
    }

    @Unroll
    def "Get a patient's vital signs of which the concept code is not one of #codes"(List<ConceptCode> codes, List<ConceptCode> expectedCodes) {
        given: "A patient Susanne with an observation for code 47625008 and an observation for code 364075005"
        def expectedParams = ["category": ["vital-signs"]]
        stubObservationForSusanne("bmi_and_heartrate", expectedParams)

        when: "I query Susanne's vital signs of which the concept code is not one of #codes"
        Query<VitalSigns> query = buildVitalSignQuery().excludingConcepts(codes).getQuery()
        def gateway = fhirEHRGatewayFactory.create(query, POPULATION_SPECIFICATION)
        VitalSigns vitalSigns = (VitalSigns) gateway.execute(query, REFERENCE_DATE)

        then: "The fhir endpoint is called correctly"
        verifyRESTCall(expectedParams)
        and: "The observations with an excluded code are filtered out"
        vitalSigns.getValues().collect { l -> l.concept } == expectedCodes

        where:
        codes                                             || expectedCodes
        [conceptFor("47625008")]                          || [conceptFor("364075005")]
        [conceptFor("47625008"), conceptFor("364075005")] || []
    }

    @Unroll
    def "Get a patient's vital signs of which the concept code is not one of #codes retains any components that aren't excluded"(List<ConceptCode> codes, List<ConceptCode> expectedCodes) {
        given: "A patient Susanne with an observation that has components for 271649006 and 271650006"
        stubObservationForSusanne("components")

        when: "I query Susanne's vital signs of which the concept code is not one of #codes"
        Query<VitalSigns> query = buildVitalSignQuery().excludingConcepts(codes).getQuery()
        def gateway = fhirEHRGatewayFactory.create(query, POPULATION_SPECIFICATION)
        VitalSigns vitalSigns = (VitalSigns) gateway.execute(query, REFERENCE_DATE)

        then: "The retrieved vital sign codes are #expectedCodes"
        vitalSigns.getValues().collect({ l -> l.concept }) == expectedCodes

        where:
        codes                                              || expectedCodes
        [conceptFor("271649006")]                          || [conceptFor("271650006")]
        [conceptFor("271649006"), conceptFor("271650006")] || []
    }

    private VitalSignQueryDsl buildVitalSignQuery() {
        QueryDsl.vitalSignQueryDsl().forSubject(SUSANNE_SUBJECT_ID, susannePatient)
    }

    private void stubObservationForSusanne(String fileName = "bmi", Map<String, List<String>> params = new HashMap<>()) {
        def builder = get(urlPathEqualTo(OBSERVATION_URL))
        builder.withQueryParam("patient", equalTo(PATIENT_SUSANNE_RESOURCE_ID))
        params.each { param ->
            param.value.each { value ->
                builder.withQueryParam(URLEncoder.encode(param.key, "UTF-8"), equalTo(value))
            }
        }
        builder.willReturn(getOkResponse(
                PATIENT_SUSANNE_RESOURCE_ID, "/samples/observation/search/template_${fileName}.json"))
        stubFor(builder)
    }

    private void verifyRESTCall(Map<String, List<String>> params = new HashMap<>()) {
        def requestedFor = getRequestedFor(urlPathEqualTo(OBSERVATION_URL))
        params.each { param ->
            param.value.each { value ->
                requestedFor.withQueryParam(URLEncoder.encode(param.key, "UTF-8"), equalTo(value))
            }
        }
        verify(requestedFor)
    }

    private static LocalDateTime toLocalDateTime(String string) {
        return LocalDateTime.ofInstant(OffsetDateTime.parse(string).toInstant(), ZoneId.systemDefault())
    }
}
