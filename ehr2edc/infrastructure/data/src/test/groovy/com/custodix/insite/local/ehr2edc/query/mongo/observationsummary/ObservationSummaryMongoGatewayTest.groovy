package com.custodix.insite.local.ehr2edc.query.mongo.observationsummary

import com.custodix.insite.local.ehr2edc.query.mongo.MongoQueryDataTest
import com.custodix.insite.local.ehr2edc.query.observationsummary.ObservationSummaryEHRGateway
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import spock.lang.Specification

import java.time.LocalDate

@MongoQueryDataTest
class ObservationSummaryMongoGatewayTest extends Specification {

    @Autowired
    private ObservationSummaryEHRGateway observationSummaryEHRGateway
    @Autowired
    private MongoTemplate mongoTemplate

    def "Getting all observation summary items for a subject that has no documents"() {
        given: "A subject id with no summary items"
        def subjectId = SubjectId.of("subject-1")

        when: "I get the summary items"
        def observationSummaryItems = observationSummaryEHRGateway.findForSubject(subjectId)

        then: "I receive no items"
        observationSummaryItems.empty
    }

    def "Getting all observation summary items for a subject that has documents"() {
        given: "A subject id with a summary item"
        def subjectId = SubjectId.of("subject-1")
        mongoTemplate.insert(observationSummaryDocumentFor(LocalDate.of(2016, 2, 2), 3, "laboratory", "subject-1"), "ObservationSummary")

        when: "I get the summary items"
        def observationSummaryItems = observationSummaryEHRGateway.findForSubject(subjectId)

        then: "I receive no items"
        observationSummaryItems.size() == 1
        observationSummaryItems.get(0).category == "laboratory"
        observationSummaryItems.get(0).date == LocalDate.of(2016, 2, 2)
        observationSummaryItems.get(0).amountOfObservations == 3
    }

    private ObservationSummaryDocument observationSummaryDocumentFor(LocalDate date, Integer amountOfObservations, String laboratory, String subjectId) {
        new ObservationSummaryDocument(date, amountOfObservations, laboratory, subjectId)
    }

    static class ObservationSummaryDocument {
        private final LocalDate date
        private final int amountOfObservations
        private final String category
        private final String subjectId

        ObservationSummaryDocument(LocalDate date, int amountOfObservations, String category, String subjectId) {
            this.date = date
            this.amountOfObservations = amountOfObservations
            this.category = category
            this.subjectId = subjectId
        }
    }
}