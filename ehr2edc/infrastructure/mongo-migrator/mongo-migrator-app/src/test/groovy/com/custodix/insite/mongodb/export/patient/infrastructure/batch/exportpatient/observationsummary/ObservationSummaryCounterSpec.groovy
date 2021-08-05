package com.custodix.insite.mongodb.export.patient.infrastructure.batch.exportpatient.observationsummary

import com.custodix.insite.mongodb.export.patient.domain.model.SummarizableObservationFact
import com.custodix.insite.mongodb.export.patient.infrastructure.mongo.model.observationsummary.ObservationSummaryDocument
import spock.lang.Specification

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

class ObservationSummaryCounterSpec extends Specification {

    def "Getting the observation summary without incrementing any dates will return an empty list"() {
        given: "An observation summary counter without any increments"
        ObservationSummaryCounter observationSummaryCounter = new ObservationSummaryCounter()

        when: "I get the observation summary items from that counter"
        def summaryItems = observationSummaryCounter.getSummaryItemsDocumentsFor("subject-1")

        then: "I receive an empty list"
        summaryItems.empty
    }

    def "Getting the observation summary after incrementing the same date multiple times will return a single item"() {
        given: "An observation summary counter"
        ObservationSummaryCounter observationSummaryCounter = new ObservationSummaryCounter()
        and: "I increment 5 times for 01-01-2019"
        5.times {
            observationSummaryCounter.incrementFor(factFor("subject-1", LocalDate.of(2019, 1, 1), "laboratory"))
        }

        when: "I get the observation summary items from that counter"
        def summaryItems = observationSummaryCounter.getSummaryItemsDocumentsFor("subject-1")

        then: "I receive a single summary item for 01-01-2019 with 5 observations"
        summaryItems.size() == 1
        summaryItemDocumentMatches(summaryItems.get(0), LocalDate.of(2019, 1, 1), 5)
    }

    def "Getting the observation summary after incrementing multiple dates will return multiple items"() {
        given: "An observation summary counter"
        ObservationSummaryCounter observationSummaryCounter = new ObservationSummaryCounter()
        and: "I increment for 01-01-2019, 01-02-2019 and 01-03-2019"
        observationSummaryCounter.incrementFor(factFor("subject-1", LocalDate.of(2019, 1, 1), "laboratory"))
        observationSummaryCounter.incrementFor(factFor("subject-1", LocalDate.of(2019, 2, 1), "laboratory"))
        observationSummaryCounter.incrementFor(factFor("subject-1", LocalDate.of(2019, 3, 1), "laboratory"))

        when: "I get the observation summary items from that counter"
        def summaryItems = observationSummaryCounter.getSummaryItemsDocumentsFor("subject-1")

        then: "I receive single summary item for each of the dates with 1 observation"
        summaryItems.size() == 3
        summaryItemDocumentMatches(summaryItems.get(0), LocalDate.of(2019, 1, 1), 1)
        summaryItemDocumentMatches(summaryItems.get(1), LocalDate.of(2019, 2, 1), 1)
        summaryItemDocumentMatches(summaryItems.get(2), LocalDate.of(2019, 3, 1), 1)
    }

    def "Getting the observations for a single after incrementing for different subjects"() {
        given: "An observation summary counter"
        ObservationSummaryCounter observationSummaryCounter = new ObservationSummaryCounter()
        and: "I increment 5 times for subject-1 and 3 times for subject-2"
        5.times {
            observationSummaryCounter.incrementFor(factFor("subject-1", LocalDate.of(2019, 1, 1), "laboratory"))
        }
        3.times {
            observationSummaryCounter.incrementFor(factFor("subject-2", LocalDate.of(2019, 1, 1), "laboratory"))
        }

        when: "I get the observation summary items from subject-1"
        def summaryItems = observationSummaryCounter.getSummaryItemsDocumentsFor("subject-1")

        then: "I only receive a single summary item for subject-1 with 5 observations"
        summaryItems.size() == 1
        summaryItemDocumentMatches(summaryItems.get(0), LocalDate.of(2019, 1, 1), 5)
    }

    SummarizableObservationFact factFor(String subjectId, LocalDate observationDate, String category) {
        return new SummarizableObservationFact() {
            @Override
            Instant getObservationInstant() {
                return observationDate.atStartOfDay().toInstant(ZoneOffset.UTC)
            }

            @Override
            String getCategory() {
                return category
            }

            @Override
            String getSubjectIdentifier() {
                return subjectId
            }
        }
    }

    void summaryItemDocumentMatches(ObservationSummaryDocument summaryItemDocument, LocalDate expectedDate, Integer expectedAmountOfObservations) {
        assert summaryItemDocument.subjectId.id == "subject-1"
        assert summaryItemDocument.amountOfObservations == expectedAmountOfObservations
        assert summaryItemDocument.category == "laboratory"
        assert summaryItemDocument.date == expectedDate
    }

}