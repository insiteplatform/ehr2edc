package com.custodix.insite.local.ehr2edc.query.mongo.laboratory.gateway

import com.custodix.insite.local.ehr2edc.query.mongo.AbstractQueryExecutorSpec
import com.custodix.insite.local.ehr2edc.query.mongo.laboratory.model.LabConceptField
import com.custodix.insite.local.ehr2edc.query.mongo.laboratory.model.LabMeasurementField
import com.custodix.insite.local.ehr2edc.query.mongo.laboratory.model.LabValueDocument
import com.custodix.insite.local.ehr2edc.query.mongo.laboratory.repository.LabValueRepository
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId
import groovy.json.JsonSlurper
import org.springframework.beans.factory.annotation.Autowired

import java.time.LocalDateTime

import static com.custodix.insite.local.ehr2edc.query.executor.common.concept.ConceptCode.conceptFor
import static com.custodix.insite.local.ehr2edc.vocabulary.SubjectIdObjectMother.aRandomSubjectId

class LabValueGatewaySpec extends AbstractQueryExecutorSpec {
    @Autowired
    LabValueRepository repository
    @Autowired
    LabValueDocumentGateway gateway

    def "returns empty list when no data is known for subject"() {
        given: "No data is present"
        repository.deleteAll()

        when: "Retrieving all documents for a subject"
        List result = gateway.findAllForSubject(aRandomSubjectId())

        then: "No results are returned"
        result.empty
    }

    def "returns json representation for known data for subject"() {
        given: "A subject"
        SubjectId subjectId = aRandomSubjectId()
        and: "A LabValue for the subject"
        aLabValueFor(subjectId)

        when: "Retrieving all documents for a subject"
        List<String> result = gateway.findAllForSubject(subjectId)

        then: "A result is returned"
        result.size() == 1
        and: "The result matches the original Document's JSON"
        def json = new JsonSlurper().parseText(result.get(0))
        with(json) {
            it.subjectId == subjectId.id
            startDate
            endDate
            quantitativeResult
            vendor
        }
    }

    def "returns all known data for a subject"() {
        given: "a subject"
        SubjectId subjectId = aRandomSubjectId()
        and: "3 labValues for the subject"
        aLabValueFor(subjectId)
        aLabValueFor(subjectId)
        aLabValueFor(subjectId)

        when: "Retrieving all documents for a subject"
        List<String> result = gateway.findAllForSubject(subjectId)

        then: "3 results are returned for the subject"
        result.size() == 3
    }

    def "returns only data for the requested subject"() {
        given: "Two subjects"
        SubjectId one = aRandomSubjectId()
        SubjectId two = aRandomSubjectId()
        and: "A labValue for subject one"
        aLabValueFor(one)
        and: "Multiple labValues for subject two"
        aLabValueFor(two)
        aLabValueFor(two)
        aLabValueFor(two)

        when: "Retrieving all documents for subject one"
        List<String> result = gateway.findAllForSubject(one)

        then: "Only subject one's labvalue is returned"
        result.size() == 1
        def json = new JsonSlurper().parseText(result.get(0))
        json.subjectId == one.id
    }

    private aLabValueFor(SubjectId subjectId) {
        repository.save(LabValueDocument.newBuilder()
                .forSubject(subjectId)
                .withQuantitativeResult(LabMeasurementField.newBuilder()
                        .withValue(1.0)
                        .withUnit("g/mmol")
                        .withUpperLimit(100.0)
                        .withLowerLimit(1.0)
                        .build())
                .withLabConcept(LabConceptField.newBuilder()
                        .withConcept(conceptFor("CONCEPT_CODE"))
                        .withComponent("COMPONENT")
                        .withSpecimen("Ser/Plas")
                        .withFastingStatus("FASTING")
                        .build())
                .withStartDate(LocalDateTime.of(2013, 4, 20, 10, 40))
                .withEndDate(LocalDateTime.of(2018, 12, 10, 9, 12))
                .withVendor("VENDOR")
                .build())
    }

}
