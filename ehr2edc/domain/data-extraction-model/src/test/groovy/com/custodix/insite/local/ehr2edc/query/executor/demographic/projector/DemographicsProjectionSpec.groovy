package com.custodix.insite.local.ehr2edc.query.executor.demographic.projector

import com.custodix.insite.local.ehr2edc.query.executor.common.projector.BooleanToNYProjector
import com.custodix.insite.local.ehr2edc.query.executor.common.query.DataPoint
import com.custodix.insite.local.ehr2edc.query.executor.common.query.ProjectedDataPoint
import com.custodix.insite.local.ehr2edc.query.executor.common.query.ProjectionChain
import com.custodix.insite.local.ehr2edc.query.executor.common.query.ProjectionContext
import com.custodix.insite.local.ehr2edc.query.executor.demographic.domain.Demographic
import com.custodix.insite.local.ehr2edc.query.executor.demographic.domain.DemographicType
import com.custodix.insite.local.ehr2edc.query.executor.demographic.projector.birthdate.*
import com.custodix.insite.local.ehr2edc.query.executor.demographic.projector.deathdate.DateOfDeathProjector
import com.custodix.insite.local.ehr2edc.query.executor.demographic.projector.gender.Gender
import com.custodix.insite.local.ehr2edc.query.executor.demographic.projector.gender.GenderProjector
import com.custodix.insite.local.ehr2edc.query.executor.demographic.projector.gender.GenderToSDTMCodeProjector
import com.custodix.insite.local.ehr2edc.query.executor.demographic.projector.gender.GenderToStringProjector
import com.custodix.insite.local.ehr2edc.query.executor.demographic.projector.vitalstatus.VitalStatusProjector
import com.custodix.insite.local.ehr2edc.query.executor.demographic.projector.vitalstatus.VitalStatusToDeceasedBooleanProjector
import com.custodix.insite.local.ehr2edc.query.executor.demographic.query.Demographics
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId
import spock.lang.Specification
import spock.lang.Unroll

import java.time.LocalDate

import static com.custodix.insite.local.ehr2edc.query.executor.objectmother.ProjectionContexts.aProjectionContext

class DemographicsProjectionSpec extends Specification {

    def "correctly projects date of birth"() {
        given: "A subject has a birth date on 01/01/2000"
        def subjectId = aSubjectId()
        Demographics observations = new Demographics([Demographic.newBuilder()
                                                              .withDemographicType(DemographicType.BIRTH_DATE)
                                                              .withSubjectId(subjectId)
                                                              .withValue("2000-01-01")
                                                              .build()])
        Optional<Demographic> observation = observations.findResult()

        when: "I project the birth date for that subject"
        def projectors = [new DateOfBirthProjector()]
        ProjectedDataPoint projected = ProjectionChain.of(projectors).project(observation.get(), aProjectionContext())

        then: "The birth date matches 01/01/2000"
        with(projected) {
            it.result.toString("dd/MM/yyyy")  == "01/01/2000"
            it.projectionSteps.size() == projectors.size()
        }
    }

    def "correctly projects an empty date of birth"() {
        given: "A subject has an empty birth date"
        def subjectId = aSubjectId()
        Demographics observations = new Demographics([Demographic.newBuilder()
                                                              .withDemographicType(DemographicType.BIRTH_DATE)
                                                              .withSubjectId(subjectId)
                                                              .withValue("")
                                                              .build()])
        Optional<DataPoint> observation = observations.findResult()

        when: "I project the birth date for that subject"
        def projectors = [new DateOfBirthProjector()]
        ProjectedDataPoint projected = ProjectionChain.of(projectors).project(observation.get(), aProjectionContext())

        then: "An empty result is returned"
        with(projected) {
            !it.result
            it.projectionSteps.size() == projectors.size()
        }
    }

    def "correctly projects date of birth to date"() {
        given: "A subject has a birth date on 01/01/2000"
        def subjectId = aSubjectId()
        Demographics observations = new Demographics([Demographic.newBuilder()
                                                              .withDemographicType(DemographicType.BIRTH_DATE)
                                                              .withSubjectId(subjectId)
                                                              .withValue("2000-01-01")
                                                              .build()])
        Optional<DataPoint> observation = observations.findResult()

        when: "I project the birth date for that subject"
        def projectors = [new DateOfBirthProjector(), new DateOfBirthToDateProjector()]
        ProjectedDataPoint projected = ProjectionChain.of(projectors).project(observation.get(), aProjectionContext())

        then: "The birth date matches 01/01/2000"
        with(projected) {
            it.result == LocalDate.of(2000, 1, 1)
            it.projectionSteps.size() == projectors.size()
        }
    }

    def "correctly projects date of death"() {
        given: "A subject has a death date on 01/01/2010"
        def subjectId = aSubjectId()
        Demographics observations = new Demographics([Demographic.newBuilder()
                                                              .withDemographicType(DemographicType.DEATH_DATE)
                                                              .withSubjectId(subjectId)
                                                              .withValue("2010-01-01")
                                                              .build()])
        Optional<DataPoint> observation = observations.findResult()

        when: "I project the death date for that subject"
        def projectors = [new DateOfDeathProjector()]
        ProjectedDataPoint projected = ProjectionChain.of(projectors).project(observation.get(), aProjectionContext())

        then: "The death date matches 01/01/2010"
        with(projected) {
            it.result.toString("dd/MM/yyyy") == "01/01/2010"
            it.projectionSteps.size() == projectors.size()
        }
    }

    def "correctly projects an empty date of death"() {
        given: "A subject has an empty death date"
        def subjectId = aSubjectId()
        Demographics observations = new Demographics([Demographic.newBuilder()
                                                              .withDemographicType(DemographicType.DEATH_DATE)
                                                              .withSubjectId(subjectId)
                                                              .withValue("")
                                                              .build()])
        Optional<DataPoint> observation = observations.findResult()

        when: "I project the death date for that subject"
        def projectors = [new DateOfDeathProjector()]
        ProjectedDataPoint projected = ProjectionChain.of(projectors).project(observation.get(), aProjectionContext())

        then: "An empty result is returned"
        with(projected) {
            !it.result
            it.projectionSteps.size() == projectors.size()
        }
    }

    def "correctly projects date of death to date"() {
        given: "A subject has a death date on 01/01/2010"
        def subjectId = aSubjectId()
        Demographics observations = new Demographics([Demographic.newBuilder()
                                                              .withDemographicType(DemographicType.BIRTH_DATE)
                                                              .withSubjectId(subjectId)
                                                              .withValue("2010-01-01")
                                                              .build()])
        Optional<DataPoint> observation = observations.findResult()

        when: "I project the death date for that subject"
        def projectors = [new DateOfBirthProjector(), new DateOfBirthToDateProjector()]
        ProjectedDataPoint projected = ProjectionChain.of(projectors).project(observation.get(), aProjectionContext())

        then: "The death date matches 01/01/2010"
        with(projected) {
            it.result == LocalDate.of(2010, 1, 1)
            it.projectionSteps.size() == projectors.size()
        }
    }

    @Unroll
    def "For a subject that has vital status #status, when projecting onto an NY-code, following code is returned: #code"() {
        given: "A subject with known vital status #status"
        def subjectId = aSubjectId()
        Demographics observations = new Demographics([Demographic.newBuilder()
                                                              .withDemographicType(DemographicType.VITAL_STATUS)
                                                              .withSubjectId(subjectId)
                                                              .withValue(status)
                                                              .build()])
        Optional<DataPoint> observation = observations.findResult()

        when: "I project onto an NY-code"
        def projectors = [new VitalStatusProjector(), new VitalStatusToDeceasedBooleanProjector(), new BooleanToNYProjector()]
        ProjectedDataPoint projected = ProjectionChain.of(projectors).project(observation.get(), ProjectionContext.newBuilder().build())

        then: "#expected value should be returned"
        with(projected) {
            it.result == code
            it.projectionSteps.size() == projectors.size()
        }

        where:
        status << ["ALIVE", "DECEASED", "UNKNOWN"]
        code << ["N", "Y", "U"]
    }

    @Unroll("Projecting a #value subject to GenderProjector() > GenderToSDTMCodeProjector() returns '#expected'")
    def "Correctly projects gender to SDTM code"() {
        given: "A #value subject"
        def subjectId = aSubjectId()
        Demographics observations = new Demographics([Demographic.newBuilder()
                                                              .withDemographicType(DemographicType.GENDER)
                                                              .withSubjectId(subjectId)
                                                              .withValue(value)
                                                              .build()])
        Optional<DataPoint> observation = observations.findResult()

        when: "I apply the necessary projections"
        def projectors = [new GenderProjector(), new GenderToSDTMCodeProjector()]
        ProjectedDataPoint projected = ProjectionChain.of(projectors).project(observation.get(), ProjectionContext.newBuilder().build())

        then: "#expected value should be returned"
        with(projected) {
            it.result == expected
            it.projectionSteps.size() == projectors.size()
        }

        where:
        value     | expected
        "male"    | Gender.MALE.sdtmCode
        "female"  | Gender.FEMALE.sdtmCode
        "unknown" | Gender.UNKNOWN.sdtmCode
        ""        | Gender.UNKNOWN.sdtmCode
    }

    @Unroll("Projecting a #value subject to GenderProjector() > toString() returns '#expected'")
    def "Correctly projects gender to mapped string"() {
        given: "A #value subject"
        def subjectId = aSubjectId()
        Demographics observations = new Demographics([Demographic.newBuilder()
                                                              .withDemographicType(DemographicType.GENDER)
                                                              .withSubjectId(subjectId)
                                                              .withValue(value)
                                                              .build()])
        Optional<DataPoint> observation = observations.findResult()
        and: "Mappings from gender to string"
        def mapping = [
                (Gender.MALE)   : "m",
                (Gender.FEMALE) : "v",
                (Gender.UNKNOWN): "x"
        ]

        when: "I apply the necessary projections"
        def projectors = [new GenderProjector(), new GenderToStringProjector(mapping)]
        ProjectedDataPoint projected = ProjectionChain.of(projectors).project(observation.get(), ProjectionContext.newBuilder().build())

        then: "#expected value should be returned"
        with(projected) {
            it.result == expected
            it.projectionSteps.size() == projectors.size()
        }

        where:
        value     | expected
        "male"    | "m"
        "female"  | "v"
        "unknown" | "x"
        ""        | "x"
    }

    @Unroll("A person born on 1/1/2000, on reference date 5/12/2018 in unit #unit, has a numerical age of: #expected")
    def "correctly projects age to numerical in a given unit"() {
        given: "A subject has a birth date on 01/01/2000"
        def subjectId = aSubjectId()
        Demographics observations = new Demographics([Demographic.newBuilder()
                                                              .withDemographicType(DemographicType.BIRTH_DATE)
                                                              .withSubjectId(subjectId)
                                                              .withValue("2000-01-01")
                                                              .build()])
        Optional<DataPoint> observation = observations.findResult()
        and: "ProjectionContext for projection contains reference date 5/12/2018"
        def context = ProjectionContext.newBuilder()
                .withReferenceDate(LocalDate.of(2018, 12, 5))
                .build()


        when: "I project the retrieved age to a numerical value"
        def projectors = [new DateOfBirthProjector(), new DateOfBirthToAgeProjector(unit), new AgeToNumericalProjector()]
        ProjectedDataPoint projected = ProjectionChain.of(projectors).project(observation.get(), context)

        then: "#expected value should be returned"
        with(projected) {
            it.result == expected
            it.projectionSteps.size() == projectors.size()
        }

        where:
        unit           | expected
        AgeUnit.HOURS  | 165912
        AgeUnit.DAYS   | 6913
        AgeUnit.WEEKS  | 987
        AgeUnit.MONTHS | 227
        AgeUnit.YEARS  | 18
    }

    @Unroll("A person born on 1/1/2000, on reference date 5/12/2018 in unit #unit, has his age projected to age units as: #expected")
    def "correctly projects age to unit"() {
        given: "A subject has a birth date on 01/01/2000"
        def subjectId = aSubjectId()
        Demographics observations = new Demographics([Demographic.newBuilder()
                                                              .withDemographicType(DemographicType.BIRTH_DATE)
                                                              .withSubjectId(subjectId)
                                                              .withValue("2000-01-01")
                                                              .build()])
        Optional<DataPoint> observation = observations.findResult()
        and: "ProjectionContext for projection contains reference date 5/12/2018"
        def context = ProjectionContext.newBuilder()
                .withReferenceDate(LocalDate.of(2018, 12, 5))
                .build()
        when: "I  project the retrieved age to age units"
        def projectors = [new DateOfBirthProjector(), new DateOfBirthToAgeProjector(unit), new AgeToUnitProjector()]
        ProjectedDataPoint projected = ProjectionChain.of(projectors).project(observation.get(), context)

        then: "#expected value should be returned"
        with(projected) {
            it.result == expected
            it.projectionSteps.size() == projectors.size()
        }

        where:
        unit           | expected
        AgeUnit.HOURS  | "HOURS"
        AgeUnit.DAYS   | "DAYS"
        AgeUnit.WEEKS  | "WEEKS"
        AgeUnit.MONTHS | "MONTHS"
        AgeUnit.YEARS  | "YEARS"
    }

    SubjectId aSubjectId() {
        return SubjectId.of(UUID.randomUUID().toString())
    }
}