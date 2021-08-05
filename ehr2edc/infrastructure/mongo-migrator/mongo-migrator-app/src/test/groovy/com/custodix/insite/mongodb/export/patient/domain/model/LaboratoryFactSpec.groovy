package com.custodix.insite.mongodb.export.patient.domain.model

import com.custodix.insite.mongodb.export.patient.domain.model.common.Concept
import com.custodix.insite.mongodb.export.patient.domain.model.common.Observation
import com.custodix.insite.mongodb.export.patient.domain.model.common.ObservationValue
import com.custodix.insite.mongodb.export.patient.domain.model.labvalue.LaboratoryConceptInfo
import com.custodix.insite.mongodb.export.patient.infrastructure.mongo.model.labvalue.LabValueDocument
import com.custodix.insite.mongodb.vocabulary.PatientIdentifier
import com.custodix.insite.mongodb.vocabulary.SubjectId
import spock.lang.Specification

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset

class LaboratoryFactSpec extends Specification {

    def "Can generate a Mongo Document for a LaboratoryFact"() {
        given: "A Laboratory fact"
        Instant start = LocalDateTime.of(2013, 4, 20, 10, 40).toInstant(ZoneOffset.ofHours(2))
        Instant end = LocalDateTime.of(2018, 12, 10, 9, 12).toInstant(ZoneOffset.ofHours(1))
        LaboratoryFact fact = LaboratoryFact.newBuilder()
                .withPatientIdentifier(PatientIdentifier.newBuilder().withSubjectId(SubjectId.of("SUBJ")).build())
                .withStartDate(start)
                .withEndDate(end)
                .withReferenceConcept(Concept.newBuilder()
                        .withCode("concept-code")
                        .build())
                .withConceptInfo(LaboratoryConceptInfo.newBuilder()
                        .withComponent("test component")
                        .withMethodValue("method")
                        .withMethodText("method text")
                        .withFastingStatus(FastingStatus.FASTING)
                        .withSpecimenValue("specimen")
                        .withSpecimenText("specimen text")
                        .build())
                .withLlnObservation(Observation.newBuilder()
                        .withValues([ObservationValue.newBuilder().withValue(1).build()])
                        .build())
                .withUlnObservation(Observation.newBuilder()
                        .withValues([ObservationValue.newBuilder().withValue(100).build()])
                        .build())
                .withValueObservation(Observation.newBuilder()
                        .withValues([ObservationValue.newBuilder().withValue(10).withUnit("kg").build()])
                        .build())
                .withVendor("LABO")
                .build()

        when: "I convert the fact to a Mongo document"
        LabValueDocument result = fact.toDocument()

        then: "I expect the result to be a valid DBObject"
        result != null
        and: "It should contain the correct subjectId"
        result.subjectId.id == "SUBJ"
        and: "It should contain the startdate"
        result.startDate.atZone(ZoneOffset.systemDefault()).toInstant() == start
        and: "It should contain the enddate"
        result.endDate.atZone(ZoneOffset.systemDefault()).toInstant() == end
        and: "It should contain the correct labConcept"
        result.labConcept.concept.code == "concept-code"
        result.labConcept.component == "test component"
        result.labConcept.method == "method"
        result.labConcept.specimen == "specimen"
        result.labConcept.fastingStatus == "FASTING"
        and: "It should contain the correct quantitative result"
        result.quantitativeResult.lowerLimit == BigDecimal.ONE
        result.quantitativeResult.upperLimit == BigDecimal.valueOf(100)
        result.quantitativeResult.value == BigDecimal.TEN
        result.quantitativeResult.unit == "kg"
        and: "It should contain a vendor"
        result.vendor == "LABO"
    }
}
