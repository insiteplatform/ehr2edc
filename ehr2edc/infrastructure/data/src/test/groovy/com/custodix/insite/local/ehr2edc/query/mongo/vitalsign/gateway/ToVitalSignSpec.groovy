package com.custodix.insite.local.ehr2edc.query.mongo.vitalsign.gateway

import com.custodix.insite.local.ehr2edc.query.executor.common.concept.ConceptCode
import com.custodix.insite.local.ehr2edc.query.mongo.vitalsign.model.VitalSignConceptField
import com.custodix.insite.local.ehr2edc.query.mongo.vitalsign.model.VitalSignDocument
import com.custodix.insite.local.ehr2edc.query.mongo.vitalsign.model.VitalSignDocumentObjectMother
import com.custodix.insite.local.ehr2edc.query.mongo.vitalsign.model.VitalSignMeasurementField
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId
import spock.lang.Specification
import spock.lang.Title
import spock.lang.Unroll

import java.time.LocalDateTime

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME

@Title("ToVitalSign")
class ToVitalSignSpec extends Specification {

    private final ToVitalSign toVitalSign = new ToVitalSign()

    @Unroll
    def "Maps subject id and effective date correctly."(String subjectId, String effectiveDateTime ){
        given: "Vital sign document with subject id '#subjectId'and effective date '#effectiveDateTime' and concept is null and measurement is  null."
        def vitalSignDocument = VitalSignDocument.newBuilder()
                .withSubjectId(SubjectId.of(subjectId))
                .withEffectiveDateTime(LocalDateTime.parse(effectiveDateTime, ISO_LOCAL_DATE_TIME))
                .withConcept(null)
                .withMeasurement(null)
                .build()

        when: "mapping to vital sign"
        def vitalSign = toVitalSign.apply(vitalSignDocument)

        then: "vital sign subject id is '#subjectId'"
        vitalSign.subjectId.id == subjectId

        and: "vital sign effective date is '#effectiveDateTime'"
        ISO_LOCAL_DATE_TIME.format(vitalSign.effectiveDateTime) == effectiveDateTime

        and: "vital sign concept is null"
        vitalSign.vitalSignConcept == null

        and: "vital sign measurement is null"
        vitalSign.measurement == null

        where:
        subjectId           | effectiveDateTime     | _
        "MY_SUBJECT_ID"     | "2011-12-03T10:15:30" | _
    }

    @Unroll
    def "Maps concept correctly."(String conceptCode, String component, String location, String laterality, String position){
        given: "Vital sign with concept containing conceptCode '#conceptCode', component '#component', location '#location', laterality '#laterality', position '#position' "
        def vitalSignDocument = VitalSignDocumentObjectMother.aDefaultVitalSignDocument().toBuilder()
                .withConcept(VitalSignConceptField.newBuilder()
                    .withConcept(ConceptCode.conceptFor(conceptCode))
                    .withComponent(component)
                    .withLaterality(laterality)
                    .withLocation(location)
                    .withPosition(position)
                    .build()
                )
                .build()

        when: "mapping to vital sign"
        def vitalSign = toVitalSign.apply(vitalSignDocument)

        then: "concept code is '#conceptCode'"
        vitalSign.vitalSignConcept.concept.code == conceptCode

        and: "concept component is '#component'"
        vitalSign.vitalSignConcept.component == component

        and: "concept position is '#position'"
        vitalSign.vitalSignConcept.position == position

        and: "concept laterality is '#laterality'"
        vitalSign.vitalSignConcept.laterality == laterality

        and: "concept location is '#location'"
        vitalSign.vitalSignConcept.location == location

        where:
        conceptCode | component | location | laterality | position | _
        "1234-6"    | "BMI"     | "body"   | "right"    | "upper"  | _
    }

    @Unroll
    def "Maps measurement correctly"(String unit, Integer lowerLimit, Integer upperLimit, Integer value ) {
        given: "Vital sign with measurement containing "
        def vitalSignDocument = VitalSignDocumentObjectMother.aDefaultVitalSignDocument().toBuilder()
            .withMeasurement(VitalSignMeasurementField.newBuilder()
                    .withUnit(unit)
                    .withLowerLimit(BigDecimal.valueOf(lowerLimit))
                    .withUpperLimit(BigDecimal.valueOf(upperLimit))
                    .withValue(BigDecimal.valueOf(value))
                    .build())
            .build()

        when: "mapping to vital sign"
        def vitalSign = toVitalSign.apply(vitalSignDocument)

        then: "measurement value is '#value'"
        vitalSign.measurement.value == value

        and: "measurement upperLimit is '#upperLimit'"
        vitalSign.measurement.upperLimit == upperLimit

        and: "measurement lowerLimit is '#lowerLimit'"
        vitalSign.measurement.lowerLimit == lowerLimit

        and: "measurement value is '#value'"
        vitalSign.measurement.value == value

        where:
        unit    | lowerLimit  | upperLimit  | value | _
        "kg/m2" | 18          | 25          | 30    | _
    }
}
