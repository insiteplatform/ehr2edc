package com.custodix.insite.local.ehr2edc.query.mongo.demographic.gateway

import com.custodix.insite.local.ehr2edc.query.mongo.AbstractQueryExecutorSpec
import com.custodix.insite.local.ehr2edc.query.mongo.demographic.model.DemographicDocument
import com.custodix.insite.local.ehr2edc.query.mongo.demographic.repository.DemographicRepository
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId
import groovy.json.JsonSlurper
import org.springframework.beans.factory.annotation.Autowired

import static com.custodix.insite.local.ehr2edc.query.executor.demographic.domain.DemographicType.BIRTH_DATE
import static com.custodix.insite.local.ehr2edc.vocabulary.SubjectIdObjectMother.aRandomSubjectId

class DemographicGatewaySpec extends AbstractQueryExecutorSpec {
    @Autowired
    DemographicRepository demographicRepository
    @Autowired
    DemographicDocumentGateway gateway

    def "returns empty list when no data is known for subject"() {
        given: "No data present"
        demographicRepository.deleteAll()

        when: "Retrieving all Documents for a subject"
        List result = gateway.findAllForSubject(aRandomSubjectId())

        then: "No results are returned"
        result.empty
    }

    def "returns json representation for known data for subject"() {
        given: "A subject"
        SubjectId subject = aRandomSubjectId()
        and: "A DemographicDocument for the subject"
        aDemographic(subject)

        when: "Retrieving all Documents for a subject"
        List<String> result = gateway.findAllForSubject(subject)

        then: "A result is returned"
        result.size == 1
        and: "The result matched the original document's JSON"
        def json = new JsonSlurper().parseText(result.get(0))
        json
        with(json) {
            _id
            demographicType == BIRTH_DATE.name()
            subjectId == subject.id
            value == "2018-12-10T09:12:00.000Z"
        }
    }

    def "returns all known data for a subject"() {
        given: "A subject"
        SubjectId subject = aRandomSubjectId()
        and: "3 DemographicDocuments for the subject"
        aDemographic(subject)
        aDemographic(subject)
        aDemographic(subject)

        when: "Retrieving all Documents for a subject"
        List result = gateway.findAllForSubject(subject)

        then: "3 results are returned"
        result.size == 3
    }

    def "returns only data for the requested subject"() {
        given: "A subject"
        SubjectId aSubject = aRandomSubjectId()
        SubjectId bSubject = aRandomSubjectId()
        and: "2 DemographicDocuments for a subject"
        aDemographic(aSubject)
        aDemographic(aSubject)
        and: "A DemographicDocument for another subject"
        aDemographic(bSubject)

        when: "Retrieving all Documents for a subject"
        List result = gateway.findAllForSubject(aSubject)

        then: "2 results are returned"
        result.size == 2
    }

    private aDemographic(SubjectId subjectId) {
        def demographic = DemographicDocument.newBuilder()
                .withSubjectId(subjectId)
                .withDemographicType(BIRTH_DATE)
                .withValue("2018-12-10T09:12:00.000Z")
                .build()
        demographicRepository.save(demographic)
    }

}
