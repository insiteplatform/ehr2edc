package com.custodix.insite.local.ehr2edc.query.mongo.laboratory

import com.custodix.insite.local.ehr2edc.query.executor.common.concept.ConceptExpander
import com.custodix.insite.local.ehr2edc.query.executor.common.projector.BooleanToNYProjector
import com.custodix.insite.local.ehr2edc.query.executor.common.projector.DateTimeISO8601Projector
import com.custodix.insite.local.ehr2edc.query.executor.common.projector.MapToStringProjector
import com.custodix.insite.local.ehr2edc.query.executor.common.projector.measurement.MeasurementToLowerLimitProjector
import com.custodix.insite.local.ehr2edc.query.executor.common.projector.measurement.MeasurementToUnitProjector
import com.custodix.insite.local.ehr2edc.query.executor.common.projector.measurement.MeasurementToUpperLimitProjector
import com.custodix.insite.local.ehr2edc.query.executor.common.projector.measurement.MeasurementToValueProjector
import com.custodix.insite.local.ehr2edc.query.executor.common.query.ProjectedDataPoint
import com.custodix.insite.local.ehr2edc.query.executor.common.query.ProjectionChain
import com.custodix.insite.local.ehr2edc.query.executor.common.query.ProjectionContext
import com.custodix.insite.local.ehr2edc.query.executor.common.query.criteria.OrdinalCriterion
import com.custodix.insite.local.ehr2edc.query.executor.domain.Measurement
import com.custodix.insite.local.ehr2edc.query.executor.laboratory.domain.LabConcept
import com.custodix.insite.local.ehr2edc.query.executor.laboratory.domain.LabValue
import com.custodix.insite.local.ehr2edc.query.executor.laboratory.projector.LabValueToEndDateProjector
import com.custodix.insite.local.ehr2edc.query.executor.laboratory.projector.LabValueToMeasurementProjector
import com.custodix.insite.local.ehr2edc.query.executor.laboratory.projector.LabValueToStartDateProjector
import com.custodix.insite.local.ehr2edc.query.executor.laboratory.projector.LastLabValueProjector
import com.custodix.insite.local.ehr2edc.query.executor.laboratory.projector.concept.LabConceptToComponentProjector
import com.custodix.insite.local.ehr2edc.query.executor.laboratory.projector.concept.LabConceptToFastingBooleanProjector
import com.custodix.insite.local.ehr2edc.query.executor.laboratory.projector.concept.LabConceptToSpecimenProjector
import com.custodix.insite.local.ehr2edc.query.executor.laboratory.projector.concept.LabValueToLabConceptProjector
import com.custodix.insite.local.ehr2edc.query.executor.laboratory.query.LabValues
import com.custodix.insite.local.ehr2edc.query.mongo.AbstractQueryExecutorSpec
import com.custodix.insite.local.ehr2edc.query.mongo.laboratory.gateway.LabValueDocumentGateway
import com.custodix.insite.local.ehr2edc.query.mongo.laboratory.model.LabConceptField
import com.custodix.insite.local.ehr2edc.query.mongo.laboratory.model.LabMeasurementField
import com.custodix.insite.local.ehr2edc.query.mongo.laboratory.model.LabValueDocument
import com.custodix.insite.local.ehr2edc.query.mongo.laboratory.repository.LabValueRepository
import com.custodix.insite.local.ehr2edc.shared.exceptions.DomainException
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Unroll

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Period

import static com.custodix.insite.local.ehr2edc.query.executor.common.concept.ConceptCode.conceptFor
import static com.custodix.insite.local.ehr2edc.query.executor.common.query.dsl.QueryDsl.labValueQuery
import static com.custodix.insite.local.ehr2edc.vocabulary.SubjectIdObjectMother.aRandomSubjectId
import static java.util.Arrays.asList

class LabValueQueryExecutorSpec extends AbstractQueryExecutorSpec {

    private static final LocalDateTime START_DATE = dateOf(2010, 1, 5).atTime(0, 0)
    private static final LocalDateTime END_DATE = dateOf(2010, 1, 10).atTime(0, 0)

    private static final String INSULIN_CODE = "60463-7"
    private static final String MORPHINE_CODE = "4337-2"

    private static final LocalDate REFERENCE_DATE = LocalDate.now()

    @Autowired
    protected LabValueRepository labValueRepository
    @Autowired
    protected LabValueDocumentGateway labValueQueryExecutor

    def setup() {
        labValueRepository.deleteAll()
    }

    def "cannot query without a subjectId"() {
        when: "I query for labvalues, without a subjectId"
        def query = labValueQuery()
                .getQuery()
        labValueQueryExecutor.execute(query, REFERENCE_DATE)

        then: "An exception is thrown"
        def ex = thrown(DomainException)
        ex.message == "The subject criterion is missing"
    }

    def "returns empty LabValues for a subject without data"() {
        when: "I query for labvalues, for a subjectId without data"
        def query = labValueQuery()
                .forSubject(aRandomSubjectId())
                .getQuery()
        def result = labValueQueryExecutor.execute(query, REFERENCE_DATE)

        then: "Empty labValues are returned"
        result
        result.values.empty
    }

    def "get observations for a specific concept"() {
        given: "A subject has an insulin measurement of 123.0"
        SubjectId subjectId = aRandomSubjectId()
        subjectWithInsulinObservation(subjectId, aMeasurement(123.0))
        and: "another insulin measurement of 456.0"
        subjectWithInsulinObservation(subjectId, aMeasurement(456.0))
        and: "a morphine measurement of 789"
        subjectWithMorphineObservation(subjectId, aMeasurement(789.0))

        when: "I query for the measurement values for insulin"
        def query = labValueQuery()
                .forSubject(subjectId)
                .forConcept(conceptFor(INSULIN_CODE))
                .getQuery()
        LabValues observations = labValueQueryExecutor.execute(query, REFERENCE_DATE)
        List<LabValue> labValues = observations.values

        then: "I receive the 2 insulin observations"
        assert labValues.size() == 2
        assert labValues.stream().anyMatch({ o -> labValueIsInsulinValueFor(o, subjectId, 123.0) })
        assert labValues.stream().anyMatch({ o -> labValueIsInsulinValueFor(o, subjectId, 456.0) })
        assert labValues.stream().allMatch({ o -> o.vendor == "VENDOR" })
    }

    def "get observations for multiple concepts"() {
        given: "A subject has an insulin measurement of 123.0"
        SubjectId subjectId = aRandomSubjectId()
        subjectWithInsulinObservation(subjectId, aMeasurement(123.0))
        and: "another insulin measurement of 456.0"
        subjectWithInsulinObservation(subjectId, aMeasurement(456.0))
        and: "a morphine measurement of 789"
        subjectWithMorphineObservation(subjectId, aMeasurement(789.0))
        and: "an 'unknown' component measurement"
        labValueRepository.save(LabValueDocument.newBuilder()
                .forSubject(subjectId)
                .withQuantitativeResult(aMeasurement(0.0))
                .withLabConcept(buildLabConcept("UNKNOWN"))
                .build())

        when: "I query for the measurement values for both insulin and morphine"
        def query = labValueQuery()
                .forSubject(subjectId)
                .forConcepts(conceptFor(INSULIN_CODE), conceptFor(MORPHINE_CODE))
                .getQuery()
        LabValues observations = labValueQueryExecutor.execute(query, REFERENCE_DATE)
        List<LabValue> labValues = observations.values

        then: "I receive all 3 observations"
        assert labValues.size() == 3
        assert labValues.stream().anyMatch({ o -> labValueIsInsulinValueFor(o, subjectId, 123.0) })
        assert labValues.stream().anyMatch({ o -> labValueIsInsulinValueFor(o, subjectId, 456.0) })
        assert labValues.stream().anyMatch({ o -> labValueIsMorphineValueFor(o, subjectId, 789.0) })
    }

    def "Get observations based on excluded concepts"() {
        given: "A subject has an insulin measurement of 123.0"
        SubjectId subjectId = aRandomSubjectId()
        subjectWithInsulinObservation(subjectId, aMeasurement(123.0))
        and: "another insulin measurement of 456.0"
        subjectWithInsulinObservation(subjectId, aMeasurement(456.0))
        and: "a morphine measurement of 789"
        subjectWithMorphineObservation(subjectId, aMeasurement(789.0))
        and: "an 'unknown' component measurement"
        labValueRepository.save(LabValueDocument.newBuilder()
                .forSubject(subjectId)
                .withQuantitativeResult(aMeasurement(0.0))
                .withLabConcept(buildLabConcept("UNKNOWN"))
                .build())

        when: "I query for the measurement values excluding both insulin and morphine"
        def query = labValueQuery()
                .forSubject(subjectId)
                .excludingConcepts(conceptFor(INSULIN_CODE), conceptFor(MORPHINE_CODE))
                .getQuery()
        LabValues observations = labValueQueryExecutor.execute(query, REFERENCE_DATE)
        List<LabValue> labValues = observations.values

        then: "I receive the 'unknown' observation"
        assert labValues.size() == 1
        labValues.get(0).labConcept.concept == conceptFor("UNKNOWN")
    }

    def "get observations for a concept with expansion"() {
        given: "A subject has an insulin measurement of 123.0"
        SubjectId subjectId = aRandomSubjectId()
        subjectWithInsulinObservation(subjectId, aMeasurement(123.0))
        and: "another insulin measurement of 456.0"
        subjectWithInsulinObservation(subjectId, aMeasurement(456.0))
        and: "a morphine measurement of 789"
        subjectWithMorphineObservation(subjectId, aMeasurement(789.0))
        and: "The insulin concept expands to the morphine concept"
        ConceptExpander conceptExpander = { concept -> asList(conceptFor(MORPHINE_CODE)) }

        when: "I query for the measurement values for insulin with the concept expansion enabled"
        def query = labValueQuery()
                .forSubject(subjectId)
                .forConceptsRelatedTo(conceptFor(INSULIN_CODE), conceptExpander)
                .getQuery()
        LabValues observations = labValueQueryExecutor.execute(query, REFERENCE_DATE)
        List<LabValue> labValues = observations.values

        then: "Only the morphine measurement is returned"
        assert labValues.size() == 1
        assert labValues.stream().anyMatch({ o -> labValueIsMorphineValueFor(o, subjectId, 789.0) })
    }

    def "get observations for a specific subject"() {
        given: "Subject 1 has an insulin measurement of 123.0 and a morphine measurement of 789.0"
        SubjectId subjectOne = aRandomSubjectId()
        subjectWithInsulinObservation(subjectOne, aMeasurement(123.0))
        subjectWithMorphineObservation(subjectOne, aMeasurement(789.0))
        and: "Subject 2 has an insulin measurement of 456.0"
        SubjectId subjectTwo = aRandomSubjectId()
        subjectWithInsulinObservation(subjectTwo, aMeasurement(456.0))

        when: "I get all measurement values for subject 1"
        def query = labValueQuery()
                .forSubject(subjectOne)
                .getQuery()
        LabValues observations = labValueQueryExecutor.execute(query, REFERENCE_DATE)
        List<LabValue> labValues = observations.values

        then: "Only the observations for subject 1 are returned"
        assert labValues.size() == 2
        assert labValues.stream().anyMatch({ o -> labValueIsInsulinValueFor(o, subjectOne, 123.0) })
        assert labValues.stream().anyMatch({ o -> labValueIsMorphineValueFor(o, subjectOne, 789.0) })
    }

    @Unroll("Get lab values dated #period before #referenceDate")
    def "Get lab values based on freshness criterion"(LocalDate referenceDate, Period period, int expectedAmountOfObservations) {
        given: "A subject with an insulin measurement that starts at 2010-01-05 and ends at 2010-01-10"
        SubjectId subjectId = aRandomSubjectId()
        subjectWithInsulinObservation(subjectId, aMeasurement(123.0))

        when: "I query for observations fits within #period before #referenceDate"
        def query = labValueQuery()
                .forSubject(subjectId)
                .freshFor(period)
                .getQuery()
        LabValues observations = labValueQueryExecutor.execute(query, referenceDate)
        List<LabValue> labValues = observations.values

        then: "I expect #expectedAmountOfObservations measurement(s) to be found"
        assert labValues.size() == expectedAmountOfObservations

        where:
        referenceDate       | period            || expectedAmountOfObservations
        dateOf(2010, 1, 3)  | Period.ofDays(3)  || 0
        dateOf(2010, 1, 4)  | Period.ofDays(3)  || 0
        dateOf(2010, 1, 5)  | Period.ofDays(0)  || 1
        dateOf(2010, 1, 7)  | Period.ofDays(5)  || 1
        dateOf(2010, 1, 9)  | Period.ofDays(2)  || 1
        dateOf(2010, 1, 10) | Period.ofDays(5)  || 1
        dateOf(2010, 1, 14) | Period.ofDays(5)  || 1
        dateOf(2010, 1, 14) | Period.ofDays(4)  || 0
        dateOf(2010, 1, 14) | Period.ofDays(3)  || 0
        dateOf(2010, 1, 14) | Period.ofDays(20) || 1
    }

    @Unroll("Get lab values without enddate, dated #period before #referenceDate")
    def "Get lab values without enddate, based on time constraints"(LocalDate referenceDate, Period period, int expectedAmountOfObservations) {
        given: "A subject with an insulin measurement that starts at 2010-01-05 without an enddate"
        SubjectId subjectId = aRandomSubjectId()
        subjectWithInsulinObservationWithoutEnddate(subjectId, aMeasurement(123.0))

        when: "I query for observations fits within #period before #referenceDate"
        def query = labValueQuery()
                .forSubject(subjectId)
                .freshFor(period)
                .getQuery()
        LabValues observations = labValueQueryExecutor.execute(query, referenceDate)
        List<LabValue> labValues = observations.values

        then: "I expect #expectedAmountOfObservations measurement(s) to be found"
        assert labValues.size() == expectedAmountOfObservations

        where:
        referenceDate       | period           || expectedAmountOfObservations
        dateOf(2010, 1, 3)  | Period.ofDays(3) || 0
        dateOf(2010, 1, 4)  | Period.ofDays(3) || 0
        dateOf(2010, 1, 5)  | Period.ofDays(1) || 1
        dateOf(2010, 1, 7)  | Period.ofDays(5) || 1
        dateOf(2010, 1, 9)  | Period.ofDays(2) || 0
        dateOf(2010, 1, 14) | Period.ofDays(4) || 0
    }

    def "Get labValue with insulin LOINC code and translate to lab test name based on sponsor's dictionary"() {
        given: "A subject with an insulin measurement"
        SubjectId subjectId = aRandomSubjectId()
        subjectWithInsulinObservation(subjectId, aMeasurement(123.0))
        and: "A dictionary containing the insulin component"
        def dictionary = [
                (INSULIN_CODE): "Sponsor name for insulin component",
        ]

        when: "I query for the subject"
        def query = labValueQuery()
                .forSubject(subjectId)
                .forConcept(conceptFor(INSULIN_CODE))
                .getQuery()
        def queryResult = labValueQueryExecutor.execute(query, REFERENCE_DATE)
        and: "Project the Result onto component and generic StringMapper"
        def projectors = [new LastLabValueProjector(),
                          new LabValueToLabConceptProjector(),
                          new LabConceptToComponentProjector(),
                          new MapToStringProjector(dictionary)]
        Optional<ProjectedDataPoint> projection = queryResult.findResult().map {
            ProjectionChain.of(projectors).project(it, ProjectionContext.newBuilder().build())
        }

        then: "I expect the sponsor-defined component name to be returned"
        projection.isPresent()
        with(projection.get()) {
            it.result == "Sponsor name for insulin component"
            it.projectionSteps.size() == projectors.size()
        }
    }

    def "Get labValue with insulin LOINC code and translate to lab test short name based on sponsor's dictionary"() {
        given: "A subject with an insulin measurement"
        SubjectId subjectId = aRandomSubjectId()
        subjectWithInsulinObservation(subjectId, aMeasurement(123.0))
        and: "A dictionary containing the insulin component"
        def dictionary = [
                (INSULIN_CODE): "INS",
        ]

        when: "I query for the subject"
        def query = labValueQuery()
                .forSubject(subjectId)
                .forConcept(conceptFor(INSULIN_CODE))
                .getQuery()
        def queryResult = labValueQueryExecutor.execute(query, REFERENCE_DATE)
        and: "Project onto component and generic StringMapper"
        def projectors = [new LastLabValueProjector(),
                          new LabValueToLabConceptProjector(),
                          new LabConceptToComponentProjector(),
                          new MapToStringProjector(dictionary)]
        Optional<ProjectedDataPoint> projection = queryResult.findResult().map {
            ProjectionChain.of(projectors).project(it, ProjectionContext.newBuilder().build())
        }

        then: "I expect the sponsor-defined component name to be returned"
        projection.isPresent()
        with(projection.get()) {
            it.result == "INS"
            it.projectionSteps.size() == projectors.size()
        }
    }

    def "Get labValue with insulin LOINC code and translate to code"() {
        given: "A subject with an insulin measurement"
        SubjectId subjectId = aRandomSubjectId()
        subjectWithInsulinObservation(subjectId, aMeasurement(123.0))

        when: "I query for the subject's insulin labvalue"
        def query = labValueQuery()
                .forSubject(subjectId)
                .forConcept(conceptFor(INSULIN_CODE))
                .getQuery()
        def queryResult = labValueQueryExecutor.execute(query, REFERENCE_DATE)
        and: "Project onto concept code"
        def projectors = [new LastLabValueProjector(),
                          new LabValueToLabConceptProjector(),
                          new LabConceptToComponentProjector()]
        Optional<ProjectedDataPoint> projection = queryResult.findResult().map {
            ProjectionChain.of(projectors).project(it, ProjectionContext.newBuilder().build())
        }

        then: "I expect #INSULIN_CODE to be returned"
        projection.isPresent()
        with(projection.get()) {
            it.result == INSULIN_CODE
            it.projectionSteps.size() == projectors.size()
        }
    }

    def "Get labValue with insulin LOINC code and translate to quantity"() {
        given: "A subject with an insulin measurement of #expected g/mmol"
        SubjectId subjectId = aRandomSubjectId()
        subjectWithInsulinObservationWithoutEnddate(subjectId, aMeasurement(expected))

        when: "I query for the subject's insulin labvalue"
        def query = labValueQuery()
                .forSubject(subjectId)
                .forConcept(conceptFor(INSULIN_CODE))
                .getQuery()
        def queryResult = labValueQueryExecutor.execute(query, REFERENCE_DATE)
        and: "Project onto measurement value and generic rounding projector"
        def projectors = [new LastLabValueProjector(), new LabValueToMeasurementProjector(), new MeasurementToValueProjector()]
        Optional<ProjectedDataPoint> projection = queryResult.findResult().map {
            ProjectionChain.of(projectors).project(it, ProjectionContext.newBuilder().build())
        }

        then: "I expect '#expected'  to be returned"
        projection.isPresent()
        with(projection.get()) {
            it.result == expected
            it.projectionSteps.size() == projectors.size()
        }

        where:
        expected = 123.0
    }

    def "Get labValue with insulin LOINC code and translate to sponsor's measurement uit definitions"() {
        given: "A subject with an insulin measurement in g/mmol"
        SubjectId subjectId = aRandomSubjectId()
        subjectWithInsulinObservationWithoutEnddate(subjectId, aMeasurement(123.0))
        and: "A dictionary containing the measurement unit"
        def dictionary = [
                "ml/1000ml": "ml/l",
                "g/mmol"   : "kg/mol",
        ]

        when: "I query for the subject's insulin labvalue"
        def query = labValueQuery()
                .forSubject(subjectId)
                .forConcept(conceptFor(INSULIN_CODE))
                .getQuery()
        def queryResult = labValueQueryExecutor.execute(query, REFERENCE_DATE)
        and: "Project onto measurement unit and generic StringMapper"
        def projectors = [new LastLabValueProjector(),
                          new LabValueToMeasurementProjector(),
                          new MeasurementToUnitProjector(),
                          new MapToStringProjector(dictionary)]
        Optional<ProjectedDataPoint> projection = queryResult.findResult().map {
            ProjectionChain.of(projectors).project(it, ProjectionContext.newBuilder().build())
        }

        then: "I expect the sponsor-defined measurement unit to be returned"
        projection.isPresent()
        with(projection.get()) {
            it.result == "kg/mol"
            it.projectionSteps.size() == projectors.size()
        }
    }

    def "Get labValue with insulin LOINC code and translate to reference range upper limit"() {
        given: "A subject with an insulin measurement in g/mmol, and reference range from 1.0 to 100.0"
        SubjectId subjectId = aRandomSubjectId()
        subjectWithInsulinObservationWithoutEnddate(subjectId, aMeasurement(123.0))

        when: "I query for the subject's insulin labvalue"
        def query = labValueQuery()
                .forSubject(subjectId)
                .forConcept(conceptFor(INSULIN_CODE))
                .getQuery()
        def queryResult = labValueQueryExecutor.execute(query, REFERENCE_DATE)
        and: "Project onto measurement value and then to reference range upper limit"
        def projectors = [new LastLabValueProjector(), new LabValueToMeasurementProjector(), new MeasurementToUpperLimitProjector()]
        def projection = queryResult.findResult().map {
            ProjectionChain.of(projectors).project(it, ProjectionContext.newBuilder().build())
        }

        then: "I expect the upper limit '100.0' to be returned"
        projection.isPresent()
        with(projection.get()) {
            it.result == 100.0
            it.projectionSteps.size() == projectors.size()
        }
    }

    def "Get labValue with insulin LOINC code and translate to reference range lower limit"() {
        given: "A subject with an insulin measurement in g/mmol, and reference range from 1.0 to 100.0"
        SubjectId subjectId = aRandomSubjectId()
        subjectWithInsulinObservationWithoutEnddate(subjectId, aMeasurement(123.0))

        when: "I query for the subject's insulin labvalue"
        def query = labValueQuery()
                .forSubject(subjectId)
                .forConcept(conceptFor(INSULIN_CODE))
                .getQuery()
        def queryResult = labValueQueryExecutor.execute(query, REFERENCE_DATE)
        and: "Project onto measurement value and reference range lower limit"
        def projectors = [new LastLabValueProjector(), new LabValueToMeasurementProjector(), new MeasurementToLowerLimitProjector()]
        Optional<ProjectedDataPoint> projection = queryResult.findResult().map() {
            ProjectionChain.of(projectors).project(it, ProjectionContext.newBuilder().build())
        }

        then: "I expect the lower limit '1.0' to be returned"
        projection.isPresent()
        with(projection.get()) {
            it.result == 1.0
            it.projectionSteps.size() == projectors.size()
        }
    }

    def "Get labValue with insulin LOINC code for 'Ser/Plas' specimen and translate to sponsor's specimen definitions"() {
        given: "A subject with an insulin measurement for a 'Ser/Pls'-specimen"
        SubjectId subjectId = aRandomSubjectId()
        subjectWithInsulinObservation(subjectId, aMeasurement(123.0))
        and: "A dictionary containing a mapping for the specimen type"
        def dictionary = [
                "Bld"         : "BLOOD",
                "Ser/Plas"    : "SERUM OR PLASMA",
                "Ser/Plas/Bld": "BLOOD",
        ]

        when: "I query for the subject's insulin labvalue"
        def query = labValueQuery()
                .forSubject(subjectId)
                .forConcept(conceptFor(INSULIN_CODE))
                .getQuery()
        def queryResult = labValueQueryExecutor.execute(query, REFERENCE_DATE)
        and: "Project onto specimen and string mapper with the given dictionary"
        def projectors = [new LastLabValueProjector(),
                          new LabValueToLabConceptProjector(),
                          new LabConceptToSpecimenProjector(),
                          new MapToStringProjector(dictionary)]
        Optional<ProjectedDataPoint> projection = queryResult.findResult().map() {
            ProjectionChain.of(projectors).project(it, ProjectionContext.newBuilder().build())
        }

        then: "I expect sponsor's specimen terminology to be returned"
        projection.isPresent()
        with(projection.get()) {
            it.result == "SERUM OR PLASMA"
            it.projectionSteps.size() == projectors.size()
        }
    }

    def "Get labValue with insulin LOINC code and fasting indicator and translate to NY-codelist"() {
        given: "A subject with an insulin measurement who was fasting"
        SubjectId subjectId = aRandomSubjectId()
        subjectWithInsulinObservation(subjectId, aMeasurement(123.0))

        when: "I query for the subject's insulin labvalue"
        def query = labValueQuery()
                .forSubject(subjectId)
                .forConcept(conceptFor(INSULIN_CODE))
                .getQuery()
        def queryResult = labValueQueryExecutor.execute(query, REFERENCE_DATE)
        and: "Project onto fasting boolean > toNY-codelist"
        def projectors = [new LastLabValueProjector(),
                          new LabValueToLabConceptProjector(),
                          new LabConceptToFastingBooleanProjector(),
                          new BooleanToNYProjector()]
        Optional<ProjectedDataPoint> projection = queryResult.findResult().map() {
            ProjectionChain.of(projectors).project(it, ProjectionContext.newBuilder().build())
        }

        then: "I expect code 'Y' to be returned"
        projection.isPresent()
        with(projection.get()) {
            it.result == "Y"
            it.projectionSteps.size() == projectors.size()
        }
    }

    def "Get labValue with insulin LOINC code and missing fasting indicator and translate to NY-codelist"() {
        given: "A subject with an insulin measurement who has no fasting status"
        SubjectId subjectId = aRandomSubjectId()
        subjectWithInsulinObservationWithEmptyFasting(subjectId, aMeasurement(123.0))

        when: "I query for the subject's insulin labvalue"
        def query = labValueQuery()
                .forSubject(subjectId)
                .forConcept(conceptFor(INSULIN_CODE))
                .getQuery()
        def queryResult = labValueQueryExecutor.execute(query, REFERENCE_DATE)
        and: "Project onto fasting boolean > toNY-codelist"
        def projectors = [new LastLabValueProjector(),
                          new LabValueToLabConceptProjector(),
                          new LabConceptToFastingBooleanProjector(),
                          new BooleanToNYProjector()]
        Optional<ProjectedDataPoint> projection = queryResult.findResult().map() {
            ProjectionChain.of(projectors).project(it, ProjectionContext.newBuilder().build())
        }

        then: "I expect code 'U' to be returned"
        projection.isPresent()
        with(projection.get()) {
            it.result == "U"
            it.projectionSteps.size() == projectors.size()
        }
    }

    def "Get labValue with insulin LOINC code and start date and translate to start date in ISO-8601 format"() {
        given: "A subject with an insulin measurement who was fasting"
        SubjectId subjectId = aRandomSubjectId()
        subjectWithInsulinObservation(subjectId, aMeasurement(123.0))

        when: "I query for the subject's insulin labvalue"
        def query = labValueQuery()
                .forSubject(subjectId)
                .forConcept(conceptFor(INSULIN_CODE))
                .getQuery()
        def queryResult = labValueQueryExecutor.execute(query, REFERENCE_DATE)
        and: "Project onto startDate and toISO8601"
        def projectors = [new LastLabValueProjector(),
                          new LabValueToStartDateProjector(),
                          new DateTimeISO8601Projector()]
        Optional<ProjectedDataPoint> projection = queryResult.findResult().map() {
            ProjectionChain.of(projectors).project(it, ProjectionContext.newBuilder().build())
        }

        then: "I expect '#expected' to be returned"
        projection.isPresent()
        with(projection.get()) {
            it.result == expected
            it.projectionSteps.size() == projectors.size()
        }

        where:
        expected = "2010-01-05T00:00:00"
    }

    def "Get labValue with insulin LOINC code and end date and translate to end date in ISO-8601 format"() {
        given: "A subject with an insulin measurement who was fasting"
        SubjectId subjectId = aRandomSubjectId()
        subjectWithInsulinObservation(subjectId, aMeasurement(123.0))

        when: "I query for the subject's insulin labvalue"
        def query = labValueQuery()
                .forSubject(subjectId)
                .forConcept(conceptFor(INSULIN_CODE))
                .getQuery()
        def queryResult = labValueQueryExecutor.execute(query, REFERENCE_DATE)
        and: "Project onto endDate and toISO8601"
        def projectors = [new LastLabValueProjector(),
                          new LabValueToEndDateProjector(),
                          new DateTimeISO8601Projector()]
        Optional<ProjectedDataPoint> projection = queryResult.findResult().map {
            ProjectionChain.of(projectors).project(it, ProjectionContext.newBuilder().build())
        }

        then: "I expect code '#expected' to be returned"
        projection.isPresent()
        with(projection.get()) {
            it.result == expected
            it.projectionSteps.size() == projectors.size()
        }

        where:
        expected = "2010-01-10T00:00:00"
    }

    @Unroll
    def "As an investigator, I can query the #ordinal labValue with subject id"(SubjectId subjectId, OrdinalCriterion.Ordinal ordinal, expectedDate) {
        given: "a query with a subject id '#subjectId' and ordinal '#ordinal'"
        def query = labValueQuery()
                .forSubject(subjectId)
                .forOrdinal(ordinal)
                .getQuery()

        and: "a labvalue for subject id '#subjectId', on 10/12/2018 at 09:10"
        LocalDateTime latest = LocalDateTime.of(2018, 12, 10, 9, 10)
        LocalDateTime first = LocalDateTime.of(2013, 4, 20, 10, 40)
        subjectWithInsulinObservation(subjectId, aMeasurement(1.0), latest)
        and: "a labvalue for subject id '#subjectId', on 20/04/2013 at 10:40"
        subjectWithInsulinObservation(subjectId, aMeasurement(2.0), first)

        when: "executing the query"
        LabValues labValues = labValueQueryExecutor.execute(query, REFERENCE_DATE)

        then: "the #ordinal labvalue at #expectedDate, for subject id '#subjectId' is returned"
        List<LabValue> listOfLabValues = labValues.getValues()
        listOfLabValues.size() == 1
        listOfLabValues.get(0).subjectId == subjectId
        listOfLabValues.get(0).startDate == expectedDate

        where:
        subjectId          | ordinal                        || expectedDate
        aRandomSubjectId() | OrdinalCriterion.Ordinal.LAST  || LocalDateTime.of(2018, 12, 10, 9, 10)
        aRandomSubjectId() | OrdinalCriterion.Ordinal.FIRST || LocalDateTime.of(2013, 4, 20, 10, 40)
    }

    @Unroll
    def "As an investigator, I can query the #ordinal labValue for a subject without labValues"(SubjectId subjectId, OrdinalCriterion.Ordinal ordinal, expectedDate) {
        given: "a query with a subject id '#subjectId' and ordinal '#ordinal'"
        def query = labValueQuery()
                .forSubject(subjectId)
                .forOrdinal(ordinal)
                .getQuery()

        when: "executing the query"
        LabValues labValues = labValueQueryExecutor.execute(query, REFERENCE_DATE)

        then: "An empty result is returned"
        List<LabValue> listOfLabValues = labValues.getValues()
        listOfLabValues.size() == 0

        where:
        subjectId          | ordinal                        || expectedDate
        aRandomSubjectId() | OrdinalCriterion.Ordinal.LAST  || LocalDateTime.of(2018, 12, 10, 9, 10)
        aRandomSubjectId() | OrdinalCriterion.Ordinal.FIRST || LocalDateTime.of(2013, 4, 20, 10, 40)
    }

    private static LabMeasurementField aMeasurement(BigDecimal value) {
        LabMeasurementField.newBuilder()
                .withValue(value)
                .withUnit("g/mmol")
                .withUpperLimit(100.0)
                .withLowerLimit(1.0)
                .build()
    }

    private static LocalDate dateOf(Integer year, Integer month, Integer day) {
        LocalDate.of(year, month, day)
    }

    private subjectWithInsulinObservation(SubjectId subjectId, LabMeasurementField observation, LocalDateTime startDate = START_DATE) {
        labValueRepository.save(LabValueDocument.newBuilder()
                .forSubject(subjectId)
                .withQuantitativeResult(observation)
                .withLabConcept(buildLabConcept(INSULIN_CODE))
                .withStartDate(startDate)
                .withEndDate(END_DATE)
                .withVendor("VENDOR")
                .build())
    }

    private subjectWithInsulinObservationWithEmptyFasting(SubjectId subjectId, LabMeasurementField observation) {
        labValueRepository.save(LabValueDocument.newBuilder()
                .forSubject(subjectId)
                .withQuantitativeResult(observation)
                .withLabConcept(buildLabConceptWithEmptyFastingStatus(INSULIN_CODE))
                .withStartDate(START_DATE)
                .withEndDate(END_DATE)
                .withVendor("VENDOR")
                .build())
    }

    private subjectWithInsulinObservationWithoutEnddate(SubjectId subjectId, LabMeasurementField observation) {
        labValueRepository.save(LabValueDocument.newBuilder()
                .forSubject(subjectId)
                .withQuantitativeResult(observation)
                .withLabConcept(buildLabConcept(INSULIN_CODE))
                .withStartDate(START_DATE)
                .build())
    }

    private subjectWithMorphineObservation(SubjectId subjectId, LabMeasurementField observation) {
        labValueRepository.save(LabValueDocument.newBuilder()
                .forSubject(subjectId)
                .withQuantitativeResult(observation)
                .withLabConcept(buildLabConcept(MORPHINE_CODE))
                .build())
    }

    private static LabConceptField buildLabConcept(String conceptCode) {
        LabConceptField.newBuilder()
                .withConcept(conceptFor(conceptCode))
                .withComponent(conceptCode)
                .withSpecimen("Ser/Plas")
                .withFastingStatus("FASTING")
                .build()
    }

    private static LabConceptField buildLabConceptWithEmptyFastingStatus(String conceptCode) {
        LabConceptField.newBuilder()
                .withConcept(conceptFor(conceptCode))
                .withComponent(conceptCode)
                .withSpecimen("Ser/Plas")
                .withFastingStatus("")
                .build()
    }

    private static boolean labValueIsInsulinValueFor(LabValue labValue, SubjectId subject, BigDecimal value) {
        Measurement measurement = new LabValueToMeasurementProjector().project(Optional.of(labValue), ProjectionContext.newBuilder().build())
                .orElse(null)
        BigDecimal observationValue = new MeasurementToValueProjector().project(Optional.of(measurement), ProjectionContext.newBuilder().build())
                .orElse(null)
        LabConcept labConcept = new LabValueToLabConceptProjector().project(Optional.of(labValue), ProjectionContext.newBuilder().build())
                .orElse(null)
        String conceptCode = new LabConceptToComponentProjector().project(Optional.of(labConcept), ProjectionContext.newBuilder().build())
                .orElse(null)

        return labValue.subjectId == subject && observationValue == value && conceptCode == INSULIN_CODE
    }

    private static boolean labValueIsMorphineValueFor(LabValue labValue, SubjectId subject, BigDecimal value) {
        Measurement measurement = new LabValueToMeasurementProjector().project(Optional.of(labValue), ProjectionContext.newBuilder().build())
                .orElse(null)
        BigDecimal observationValue = new MeasurementToValueProjector().project(Optional.of(measurement), ProjectionContext.newBuilder().build())
                .orElse(null)
        LabConcept labConcept = new LabValueToLabConceptProjector().project(Optional.of(labValue), ProjectionContext.newBuilder().build())
                .orElse(null)
        String conceptCode = new LabConceptToComponentProjector().project(Optional.of(labConcept), ProjectionContext.newBuilder().build())
                .orElse(null)

        return labValue.subjectId == subject && observationValue == value && conceptCode == MORPHINE_CODE
    }
}
