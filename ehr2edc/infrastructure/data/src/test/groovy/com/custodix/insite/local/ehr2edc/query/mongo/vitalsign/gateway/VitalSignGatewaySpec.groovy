package com.custodix.insite.local.ehr2edc.query.mongo.vitalsign.gateway


import com.custodix.insite.local.ehr2edc.query.mongo.AbstractQueryExecutorSpec
import com.custodix.insite.local.ehr2edc.query.mongo.vitalsign.repository.VitalSignRepository
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId
import groovy.json.JsonSlurper
import org.springframework.beans.factory.annotation.Autowired

import static com.custodix.insite.local.ehr2edc.query.mongo.vitalsign.model.VitalSignDocumentObjectMother.aDefaultVitalSignDocument
import static com.custodix.insite.local.ehr2edc.vocabulary.SubjectIdObjectMother.aRandomSubjectId

class VitalSignGatewaySpec extends AbstractQueryExecutorSpec {
    @Autowired
    VitalSignRepository repository
    @Autowired
    VitalSignDocumentGateway gateway

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
        and: "A vitalSign for the subject"
        vitalSignFor(subjectId)

        when: "Retrieving all documents for a subject"
        List<String> result = gateway.findAllForSubject(subjectId)

        then: "A result is returned"
        result.size() == 1
        and: "The result matches the original Document's JSON"
        def json = new JsonSlurper().parseText(result.get(0))
        with(json) {
            it.subjectId == subjectId.id
            concept
            measurement
            effectiveDateTime
        }
    }

    def "returns all known data for a subject"() {
        given: "a subject"
        SubjectId subjectId = aRandomSubjectId()
        and: "3 vitalSigns for the subject"
        vitalSignFor(subjectId)
        vitalSignFor(subjectId)
        vitalSignFor(subjectId)

        when: "Retrieving all documents for a subject"
        List<String> result = gateway.findAllForSubject(subjectId)

        then: "3 results are returned for the subject"
        result.size() == 3
    }

    def "returns only data for the requested subject"() {
        given: "Two subjects"
        SubjectId one = aRandomSubjectId()
        SubjectId two = aRandomSubjectId()
        and: "A vitalSign for subject one"
        vitalSignFor(one)
        and: "Multiple vitalSigns for subject two"
        vitalSignFor(two)
        vitalSignFor(two)
        vitalSignFor(two)

        when: "Retrieving all documents for subject one"
        List<String> result = gateway.findAllForSubject(one)

        then: "Only subject one's vitalSign is returned"
        result.size() == 1
        def json = new JsonSlurper().parseText(result.get(0))
        json.subjectId == one.id
    }

    def vitalSignFor(SubjectId subjectId) {
        def aVitalSign = aDefaultVitalSignDocument().toBuilder()
                .withSubjectId(subjectId)
                .build()
        repository.save(aVitalSign)
    }
}
