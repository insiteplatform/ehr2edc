package com.custodix.insite.local.ehr2edc.query.fhir.laboratory

import ca.uhn.fhir.model.dstu2.resource.Observation
import ca.uhn.fhir.rest.api.Constants
import com.custodix.insite.local.ehr2edc.populator.EHRGateway
import com.custodix.insite.local.ehr2edc.query.executor.common.concept.ConceptCode
import com.custodix.insite.local.ehr2edc.query.executor.common.query.Query
import com.custodix.insite.local.ehr2edc.query.executor.common.query.criteria.OrdinalCriterion
import com.custodix.insite.local.ehr2edc.query.executor.common.query.dsl.LabValueQueryDsl
import com.custodix.insite.local.ehr2edc.query.executor.common.query.dsl.QueryDsl
import com.custodix.insite.local.ehr2edc.query.executor.laboratory.domain.LabValue
import com.custodix.insite.local.ehr2edc.query.executor.laboratory.query.LabValues
import com.custodix.insite.local.ehr2edc.query.fhir.AbstractFhirEHRGatewayWithWiremockSpec
import com.custodix.insite.local.ehr2edc.vocabulary.PatientCDWReference
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId
import spock.lang.Unroll

import java.time.*

import static com.custodix.insite.local.ehr2edc.query.executor.common.concept.ConceptCode.conceptFor
import static com.custodix.insite.local.ehr2edc.vocabulary.SubjectIdObjectMother.aRandomSubjectId
import static com.github.tomakehurst.wiremock.client.WireMock.*

class FhirDstu2LaboratoryGatewaySpec extends AbstractFhirEHRGatewayWithWiremockSpec {
    private static final String OBSERVATION_URL = "/baseDstu2/Observation"
    private static final String SPECIMEN_URL = "/baseDstu2/Specimen"
    private static final SubjectId SUSANNE_SUBJECT_ID = aRandomSubjectId()

    private PatientCDWReference susannePatient

    def setup() {
        susannePatient = patientSusanne()
    }

    def "Get a patient's lab values without observations in fhir"() {
        given: "A patient Susanne with zero observations"
        stubObservationForSusanne("with_0_observations")

        when: "I query the lab values for Susanne"
        Query<LabValues> query = buildLaboratoryQuery().getQuery()
        EHRGateway gateway = fhirEHRGatewayFactory.create(query, POPULATION_SPECIFICATION)
        LabValues labValues = (LabValues) gateway.execute(query, REFERENCE_DATE)

        then: "I expect exactly 0 lab values"
        labValues.getValues().size() == 0
    }

    def "Get a patient's lab value"() {
        given: "A patient Susanne with an observation"
        stubObservationForSusanne()

        when: "I query the lab values for Susanne"
        Query<LabValues> query = buildLaboratoryQuery().getQuery()
        def gateway = fhirEHRGatewayFactory.create(query, POPULATION_SPECIFICATION)
        LabValues labValues = (LabValues) gateway.execute(query, REFERENCE_DATE)

        then: "I expect exactly 1 lab value"
        labValues.getValues().size() == 1
        LabValue labValue = labValues.getValues().get(0)
        and: "the lab value has a subject id"
        labValue.subjectId == SUSANNE_SUBJECT_ID
        and: "the lab value has code 2339-0"
        labValue.concept.code == "2339-0"
    }

    def "Get a patient's lab value that has an attribute 'effectiveDateTime'"() {
        given: "A patient Susanne with an observation that has an attribute 'effectiveDateTime' with value 2008-07-10T10:38:49+01:00"
        stubObservationForSusanne("effective_datetime")

        when: "I query the lab value for Susanne"
        Query<LabValues> query = buildLaboratoryQuery().getQuery()
        def gateway = fhirEHRGatewayFactory.create(query, POPULATION_SPECIFICATION)
        LabValues labValues = (LabValues) gateway.execute(query, REFERENCE_DATE)

        then: "I expect exactly 1 lab value"
        labValues.getValues().size() == 1
        LabValue labValue = labValues.getValues().get(0)
        and: "the lab value start and end date have the same value 2008-07-10T10:38:49+01:00"
        def expectedDate = toLocalDateTime("2008-07-10T10:38:49+01:00")
        labValue.startDate == expectedDate
        labValue.endDate == expectedDate
    }

    def "Get a patient's lab value that has an attribute 'effectivePeriod'"() {
        given: "A patient Susanne with an observation that has an attribute 'effectivePeriod' with start 2008-07-10T10:38:49+01:00 and end 2008-07-11T10:38:49+01:00"
        stubObservationForSusanne("effective_period")

        when: "I query the lab value for Susanne"
        Query<LabValues> query = buildLaboratoryQuery().getQuery()
        def gateway = fhirEHRGatewayFactory.create(query, POPULATION_SPECIFICATION)
        LabValues labValues = (LabValues) gateway.execute(query, REFERENCE_DATE)

        then: "I expect exactly 1 lab value"
        labValues.getValues().size() == 1
        LabValue labValue = labValues.getValues().get(0)
        and: "the lab value has a start date 2008-07-10T10:38:49+01:00"
        labValue.startDate == toLocalDateTime("2008-07-10T10:38:49+01:00")
        and: "the lab value has an end date 2008-07-11T10:38:49+01:00"
        labValue.endDate == toLocalDateTime("2008-07-11T10:38:49+01:00")
    }

    def "Get a patient's lab value for which an attribute 'effective[x]' is missing"() {
        given: "A patient Susanne with an observation for which an attribute 'effective[x]' is missing"
        stubObservationForSusanne("effective_missing")

        when: "I query the lab value for Susanne"
        Query<LabValues> query = buildLaboratoryQuery().getQuery()
        def gateway = fhirEHRGatewayFactory.create(query, POPULATION_SPECIFICATION)
        LabValues labValues = (LabValues) gateway.execute(query, REFERENCE_DATE)

        then: "I expect exactly 1 lab value"
        labValues.getValues().size() == 1
        LabValue labValue = labValues.getValues().get(0)
        and: "the lab value has no start date"
        labValue.startDate == null
        and: "the lab value has no end date"
        labValue.endDate == null
    }

    def "Get a patient's lab value that has an attribute 'valueQuantity' with fields 'value', 'code' and 'unit'"() {
        given: "A patient Susanne with an observation that has an attribute 'valueQuantity' with value '66', unit 'milligrams per decilitre' and code 'mg/dL'"
        stubObservationForSusanne("value_quantity")

        when: "I query the lab value for Susanne"
        Query<LabValues> query = buildLaboratoryQuery().getQuery()
        def gateway = fhirEHRGatewayFactory.create(query, POPULATION_SPECIFICATION)
        LabValues labValues = (LabValues) gateway.execute(query, REFERENCE_DATE)

        then: "I expect exactly 1 lab value"
        labValues.getValues().size() == 1
        LabValue labValue = labValues.getValues().get(0)
        and: "the lab value has a measurement value '66'"
        labValue.quantitativeResult.value == new BigDecimal("66")
        and: "the lab value has a measurement unit 'mg/dL'"
        labValue.quantitativeResult.unit == "mg/dL"
    }

    def "Get a patient's lab value that has an attribute 'valueQuantity' with fields 'value' and 'unit' but missing 'code'"() {
        given: "A patient Susanne with an observation that has an attribute 'valueQuantity' with value '66' and unit 'milligrams per decilitre'"
        stubObservationForSusanne("value_quantity_code_missing")

        when: "I query the lab value for Susanne"
        Query<LabValues> query = buildLaboratoryQuery().getQuery()
        def gateway = fhirEHRGatewayFactory.create(query, POPULATION_SPECIFICATION)
        LabValues labValues = (LabValues) gateway.execute(query, REFERENCE_DATE)

        then: "I expect exactly 1 lab value"
        labValues.getValues().size() == 1
        LabValue labValue = labValues.getValues().get(0)
        and: "the lab value has a measurement value '66'"
        labValue.quantitativeResult.value == new BigDecimal("66")
        and: "the lab value has a measurement unit 'milligrams per decilitre'"
        labValue.quantitativeResult.unit == "milligrams per decilitre"
    }

    def "Get a patient's lab value for which an attribute 'value[x]' is missing"() {
        given: "A patient Susanne with an observation for which an attribute 'value[x]' is missing"
        stubObservationForSusanne("value_quantity_missing")

        when: "I query the lab value for Susanne"
        Query<LabValues> query = buildLaboratoryQuery().getQuery()
        def gateway = fhirEHRGatewayFactory.create(query, POPULATION_SPECIFICATION)
        LabValues labValues = (LabValues) gateway.execute(query, REFERENCE_DATE)

        then: "I expect exactly 1 lab value"
        labValues.getValues().size() == 1
        LabValue labValue = labValues.getValues().get(0)
        and: "the lab value has no measurement value"
        labValue.quantitativeResult.value == null
        and: "the lab value has no measurement unit"
        labValue.quantitativeResult.unit == null
    }

    def "Get a patient's lab value that has an attribute 'referenceRange' with fields 'low' and 'high'"() {
        given: "A patient Susanne with an observation that has an attribute 'referenceRange' with low value '50' and high value '75'"
        stubObservationForSusanne("reference_range")

        when: "I query the lab value for Susanne"
        Query<LabValues> query = buildLaboratoryQuery().getQuery()
        def gateway = fhirEHRGatewayFactory.create(query, POPULATION_SPECIFICATION)
        LabValues labValues = (LabValues) gateway.execute(query, REFERENCE_DATE)

        then: "I expect exactly 1 lab value"
        labValues.getValues().size() == 1
        LabValue labValue = labValues.getValues().get(0)
        and: "the lab value has a reference range lower limit '50'"
        labValue.quantitativeResult.lowerLimit == new BigDecimal("50")
        and: "the lab value has a reference range upper limit '75'"
        labValue.quantitativeResult.upperLimit == new BigDecimal("75")
    }

    def "Get a patient's lab value that has an attribute 'referenceRange' with field 'low' but missing 'high'"() {
        given: "A patient Susanne with an observation that has an attribute 'referenceRange' with low value '50'"
        stubObservationForSusanne("reference_range_high_missing")

        when: "I query the lab value for Susanne"
        Query<LabValues> query = buildLaboratoryQuery().getQuery()
        def gateway = fhirEHRGatewayFactory.create(query, POPULATION_SPECIFICATION)
        LabValues labValues = (LabValues) gateway.execute(query, REFERENCE_DATE)

        then: "I expect exactly 1 lab value"
        labValues.getValues().size() == 1
        LabValue labValue = labValues.getValues().get(0)
        and: "the lab value has a reference range lower limit '50'"
        labValue.quantitativeResult.lowerLimit == new BigDecimal("50")
        and: "the lab value has no reference range upper limit"
        labValue.quantitativeResult.upperLimit == null
    }

    def "Get a patient's lab value that has an attribute 'referenceRange' with field 'high' but missing 'low'"() {
        given: "A patient Susanne with an observation that has an attribute 'referenceRange' with high value '75'"
        stubObservationForSusanne("reference_range_low_missing")

        when: "I query the lab value for Susanne"
        Query<LabValues> query = buildLaboratoryQuery().getQuery()
        def gateway = fhirEHRGatewayFactory.create(query, POPULATION_SPECIFICATION)
        LabValues labValues = (LabValues) gateway.execute(query, REFERENCE_DATE)

        then: "I expect exactly 1 lab value"
        labValues.getValues().size() == 1
        LabValue labValue = labValues.getValues().get(0)
        and: "the lab value has no reference range lower limit"
        labValue.quantitativeResult.lowerLimit == null
        and: "the lab value has reference range upper limit '75'"
        labValue.quantitativeResult.upperLimit == new BigDecimal("75")
    }

    def "Get a patient's lab value for which an attribute 'referenceRange' is missing"() {
        given: "A patient Susanne with an observation for which an attribute 'referenceRange' is missing"
        stubObservationForSusanne("reference_range_missing")

        when: "I query the lab value for Susanne"
        Query<LabValues> query = buildLaboratoryQuery().getQuery()
        def gateway = fhirEHRGatewayFactory.create(query, POPULATION_SPECIFICATION)
        LabValues labValues = (LabValues) gateway.execute(query, REFERENCE_DATE)

        then: "I expect exactly 1 lab value"
        labValues.getValues().size() == 1
        LabValue labValue = labValues.getValues().get(0)
        and: "the lab value has no reference range lower limit"
        labValue.quantitativeResult.lowerLimit == null
        and: "the lab value has no reference range upper limit"
        labValue.quantitativeResult.upperLimit == null
    }

    def "Get a patient's lab value that has an attribute 'method' with a coding list containing a single coding with field 'code'"() {
        given: "A patient Susanne with an observation that has an attribute 'method' with a coding list containing a single coding with code '702660003'"
        stubObservationForSusanne("method")

        when: "I query the lab value for Susanne"
        Query<LabValues> query = buildLaboratoryQuery().getQuery()
        def gateway = fhirEHRGatewayFactory.create(query, POPULATION_SPECIFICATION)
        LabValues labValues = (LabValues) gateway.execute(query, REFERENCE_DATE)

        then: "I expect exactly 1 lab value"
        labValues.getValues().size() == 1
        LabValue labValue = labValues.getValues().get(0)
        and: "the lab value has method '702660003'"
        labValue.labConcept.method == "702660003"
    }

    def "Get a patient's lab value that for which an attribute 'method' is missing"() {
        given: "A patient Susanne with an observation for which an attribute 'method' is missing"
        stubObservationForSusanne("method_missing")

        when: "I query the lab value for Susanne"
        Query<LabValues> query = buildLaboratoryQuery().getQuery()
        def gateway = fhirEHRGatewayFactory.create(query, POPULATION_SPECIFICATION)
        LabValues labValues = (LabValues) gateway.execute(query, REFERENCE_DATE)

        then: "I expect exactly 1 lab value"
        labValues.getValues().size() == 1
        LabValue labValue = labValues.getValues().get(0)
        and: "the lab value has no method"
        labValue.labConcept.method == null
    }

    def "Get a patient's lab value that has an attribute 'specimen' that references an included Specimen resource with field 'type'"() {
        given: "A patient Susanne with an observation that has an attribute 'specimen' that references an included Specimen resource with type 'BLD'"
        stubObservationForSusanne("specimen_included")

        when: "I query the lab value for Susanne"
        Query<LabValues> query = buildLaboratoryQuery().getQuery()
        def gateway = fhirEHRGatewayFactory.create(query, POPULATION_SPECIFICATION)
        LabValues labValues = (LabValues) gateway.execute(query, REFERENCE_DATE)

        then: "I expect exactly 1 lab value"
        labValues.getValues().size() == 1
        LabValue labValue = labValues.getValues().get(0)
        and: "the lab value has specimen 'BLD'"
        labValue.labConcept.specimen == "BLD"
    }

    def "Get a patient's lab value that has an attribute 'specimen' that references a non-included Specimen resource with field 'type'"() {
        given: "A patient Susanne with an observation that has an attribute 'specimen' that references a non-included Specimen resource with type 'BLD'"
        stubObservationForSusanne("specimen_nonincluded")
        stubSpecimenForSusanne()

        when: "I query the lab value for Susanne"
        Query<LabValues> query = buildLaboratoryQuery().getQuery()
        def gateway = fhirEHRGatewayFactory.create(query, POPULATION_SPECIFICATION)
        LabValues labValues = (LabValues) gateway.execute(query, REFERENCE_DATE)

        then: "I expect exactly 1 lab value"
        labValues.getValues().size() == 1
        LabValue labValue = labValues.getValues().get(0)
        and: "the lab value has specimen 'BLD'"
        labValue.labConcept.specimen == "BLD"
    }

    def "Get a patient's lab value that has a contained Specimen resource with field 'type'"() {
        given: "A patient Susanne with an observation that has a contained Specimen resource with type 'BLD'"
        stubObservationForSusanne("specimen_contained")

        when: "I query the lab value for Susanne"
        Query<LabValues> query = buildLaboratoryQuery().getQuery()
        def gateway = fhirEHRGatewayFactory.create(query, POPULATION_SPECIFICATION)
        LabValues labValues = (LabValues) gateway.execute(query, REFERENCE_DATE)

        then: "I expect exactly 1 lab value"
        labValues.getValues().size() == 1
        LabValue labValue = labValues.getValues().get(0)
        and: "the lab value has specimen 'BLD'"
        labValue.labConcept.specimen == "BLD"
    }

    def "Get a patient's lab value for which an attribute 'specimen' is missing"() {
        given: "A patient Susanne with an observation for which an attribute 'specimen' is missing"
        stubObservationForSusanne("specimen_missing")

        when: "I query the lab value for Susanne"
        Query<LabValues> query = buildLaboratoryQuery().getQuery()
        def gateway = fhirEHRGatewayFactory.create(query, POPULATION_SPECIFICATION)
        LabValues labValues = (LabValues) gateway.execute(query, REFERENCE_DATE)

        then: "I expect exactly 1 lab value"
        labValues.getValues().size() == 1
        LabValue labValue = labValues.getValues().get(0)
        and: "the lab value has no specimen"
        labValue.labConcept.specimen == null
    }

    def "Get a patient's 2 lab values for which one references a specimen and the other doesn't"() {
        given: "A patient Susanne with an observation with concept '2339-0' that references a specimen with type 'BLD' and an observation with concept '4548-4' that doesn't reference a specimen"
        stubObservationForSusanne("specimen_missing_and_included")

        when: "I query the lab value for Susanne"
        Query<LabValues> query = buildLaboratoryQuery().getQuery()
        def gateway = fhirEHRGatewayFactory.create(query, POPULATION_SPECIFICATION)
        LabValues labValues = (LabValues) gateway.execute(query, REFERENCE_DATE)

        then: "I expect exactly 2 lab values"
        labValues.getValues().size() == 2
        and: "the lab value with concept '2339-0' has specimen 'BLD'"
        LabValue labValue1 = labValues.getValues().find({ it.concept.code == '2339-0' })
        labValue1.labConcept.specimen == "BLD"
        and: "the lab value with concept '4548-4' has no specimen"
        LabValue labValue2 = labValues.getValues().find({ it.concept.code == '4548-4' })
        labValue2.labConcept.specimen == null

    }

    @Unroll
    def "Get a patient's #ordinal lab value "(OrdinalCriterion.Ordinal ordinal, String sortParam, String sortValue, String countParam, String countValue) {
        given: "A patient Susanne with an observation"
        stubObservationForSusanne()

        when: "I query Susanne's #ordinal lab value"
        Query<LabValues> query = buildLaboratoryQuery().forOrdinal(ordinal).getQuery()
        def gateway = fhirEHRGatewayFactory.create(query, POPULATION_SPECIFICATION)
        gateway.execute(query, REFERENCE_DATE)

        then: "The fhir endpoint is called correctly"
        verify(getRequestedFor(urlPathEqualTo(OBSERVATION_URL))
                .withQueryParam(Observation.SP_PATIENT, equalTo(PATIENT_SUSANNE_RESOURCE_ID))
                .withQueryParam(URLEncoder.encode(sortParam, "UTF-8"), equalTo(sortValue))
                .withQueryParam(countParam, equalTo(countValue)))

        where:
        ordinal                        || sortParam                 | sortValue           | countParam            | countValue
        OrdinalCriterion.Ordinal.LAST  || Constants.PARAM_SORT_DESC | Observation.SP_DATE | Constants.PARAM_COUNT | "1"
        OrdinalCriterion.Ordinal.FIRST || Constants.PARAM_SORT_ASC  | Observation.SP_DATE | Constants.PARAM_COUNT | "1"
    }

    @Unroll
    def "Get a patient's #ordinal lab value when fhir does not respect the ordinal criterion"(OrdinalCriterion.Ordinal ordinal, String sortParam, String sortValue, String countParam, String countValue, String code) {
        given: "A patient Susanne with an observation"
        stubObservationForSusanne("glucose_and_hemoglobin")

        when: "I query Susanne's #ordinal lab value"
        Query<LabValues> query = buildLaboratoryQuery().forOrdinal(ordinal).getQuery()
        def gateway = fhirEHRGatewayFactory.create(query, POPULATION_SPECIFICATION)
        LabValues labValues = (LabValues) gateway.execute(query, REFERENCE_DATE)

        then: "I expect exactly 1 lab value"
        labValues.getValues().size() == 1
        LabValue labValue = labValues.getValues().get(0)
        and: "the lab value has concept code ${code}"
        labValue.concept.code == code

        where:
        ordinal                        || sortParam                 | sortValue           | countParam            | countValue | code
        OrdinalCriterion.Ordinal.LAST  || Constants.PARAM_SORT_DESC | Observation.SP_DATE | Constants.PARAM_COUNT | "1"        | "4548-4"
        OrdinalCriterion.Ordinal.FIRST || Constants.PARAM_SORT_ASC  | Observation.SP_DATE | Constants.PARAM_COUNT | "1"        | "2339-0"
    }

    @Unroll
    def "Get a patient's lab values which were recorded at most #maxAge before the reference date #referenceDate"(Period maxAge, LocalDate referenceDate, String expectedGreaterThan, String expectedLessThan) {
        given: "A patient Susanne with an observation"
        stubObservationForSusanne()

        when: "I query Susanne's lab values which were recorded at most #maxAge before the reference date #referenceDate"
        Query<LabValues> query = buildLaboratoryQuery().freshFor(maxAge).getQuery()
        def gateway = fhirEHRGatewayFactory.create(query, POPULATION_SPECIFICATION)
        gateway.execute(query, referenceDate)

        then: "The fhir endpoint is called correctly"
        verify(getRequestedFor(urlPathEqualTo(OBSERVATION_URL))
                .withQueryParam(Observation.SP_PATIENT, equalTo(PATIENT_SUSANNE_RESOURCE_ID))
                .withQueryParam(Observation.SP_DATE, equalTo("ge${expectedGreaterThan}"))
                .withQueryParam(Observation.SP_DATE, equalTo("lt${expectedLessThan}")))

        where:
        maxAge            | referenceDate                 || expectedGreaterThan         | expectedLessThan
        Period.ofDays(1)  | LocalDate.parse("2020-01-24") || "2020-01-24T00:00:00+01:00" | "2020-01-25T00:00:00+01:00"
        Period.ofWeeks(1) | LocalDate.parse("2020-01-01") || "2019-12-25T00:00:00+01:00" | "2020-01-02T00:00:00+01:00"
    }

    @Unroll
    def "Get a patient's lab values of which the concept code is one of #codes"(List<ConceptCode> codes, String expectedParamValue) {
        given: "A patient Susanne with an observation"
        stubObservationForSusanne()

        when: "I query Susanne's lab values of which the concept code is one of #codes"
        Query<LabValues> query = buildLaboratoryQuery().forConcepts(codes).getQuery()
        def gateway = fhirEHRGatewayFactory.create(query, POPULATION_SPECIFICATION)
        gateway.execute(query, REFERENCE_DATE)

        then: "The fhir endpoint is called correctly"
        verify(getRequestedFor(urlPathEqualTo(OBSERVATION_URL))
                .withQueryParam(Observation.SP_PATIENT, equalTo(PATIENT_SUSANNE_RESOURCE_ID))
                .withQueryParam(Observation.SP_CODE, equalTo(expectedParamValue)))

        where:
        codes                                        || expectedParamValue
        [conceptFor("2339-0")]                       || "2339-0"
        [conceptFor("2339-0"), conceptFor("4548-4")] || "2339-0,4548-4"
    }

    @Unroll
    def "Get a patient's lab values of which the concept code is one of #codes with includeMissing '#includeMissing'"(
            List<ConceptCode> codes, boolean includeMissing, List<ConceptCode> expectedCodes) {
        given: "A patient Susanne with an observation for code 2339-0"
        stubObservationForSusanne()

        when: "I query Susanne's lab values of which the concept code is one of #codes with includeMissing '#includeMissing'"
        Query<LabValues> query = buildLaboratoryQuery().forConcepts(codes, includeMissing).getQuery()
        def gateway = fhirEHRGatewayFactory.create(query, POPULATION_SPECIFICATION)
        LabValues labValues = gateway.execute(query, REFERENCE_DATE)

        then: "The retrieved lab value codes are #expectedCodes"
        labValues.getValues().collect({ l -> l.concept }) == expectedCodes

        where:
        codes                                        | includeMissing || expectedCodes
        [conceptFor("2339-0"), conceptFor("4548-4")] | false          || [conceptFor("2339-0")]
        [conceptFor("2339-0"), conceptFor("4548-4")] | true           || [conceptFor("2339-0"), conceptFor("4548-4")]
    }

    @Unroll
    def "Get a patient's lab values of which the concept code is not one of #codes"(List<ConceptCode> codes, List<ConceptCode> expectedCodes) {
        given: "A patient Susanne with an observation for code 2339-0 and an observation for code 4548-4"
        stubObservationForSusanne("glucose_and_hemoglobin")

        when: "I query Susanne's lab values of which the concept code is not one of #codes"
        Query<LabValues> query = buildLaboratoryQuery().excludingConcepts(codes).getQuery()
        def gateway = fhirEHRGatewayFactory.create(query, POPULATION_SPECIFICATION)
        LabValues labValues = (LabValues) gateway.execute(query, REFERENCE_DATE)

        then: "The fhir endpoint is called correctly"
        verify(getRequestedFor(urlPathEqualTo(OBSERVATION_URL))
                .withQueryParam("patient", equalTo(PATIENT_SUSANNE_RESOURCE_ID))
                .withQueryParam("category", equalTo("laboratory")))
        and: "The observations with an excluded code are filtered out"
        labValues.getValues().collect { l -> l.concept } == expectedCodes

        where:
        codes                                        || expectedCodes
        [conceptFor("2339-0")]                       || [conceptFor("4548-4")]
        [conceptFor("2339-0"), conceptFor("4548-4")] || []
    }

    private LabValueQueryDsl buildLaboratoryQuery() {
        QueryDsl.labValueQuery().forSubject(SUSANNE_SUBJECT_ID, susannePatient)
    }

    private void stubObservationForSusanne(String fileName = "glucose") {
        stubFor(get(urlPathEqualTo(OBSERVATION_URL))
                .withQueryParam(Observation.SP_PATIENT, equalTo(PATIENT_SUSANNE_RESOURCE_ID))
                .withQueryParam(Constants.PARAM_INCLUDE, equalTo(Observation.INCLUDE_SPECIMEN.value))
                .willReturn(getOkResponse(
                        PATIENT_SUSANNE_RESOURCE_ID, "/samples/observation/search/template_${fileName}.json")))
    }

    private void stubSpecimenForSusanne() {
        stubFor(get(urlPathEqualTo("${SPECIMEN_URL}/1"))
                .willReturn(getOkResponse(
                        PATIENT_SUSANNE_RESOURCE_ID, "/samples/specimen/get/blood.json")))
    }

    private static LocalDateTime toLocalDateTime(String string) {
        return LocalDateTime.ofInstant(OffsetDateTime.parse(string).toInstant(), ZoneId.systemDefault())
    }
}
