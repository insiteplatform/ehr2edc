package com.custodix.insite.local.ehr2edc.application

import com.custodix.insite.local.GetUsersController
import com.custodix.insite.local.ehr2edc.application.AbstractSpecification
import com.custodix.insite.local.ehr2edc.query.GetPopulatedEventHistory
import com.custodix.insite.local.ehr2edc.shared.exceptions.UseCaseConstraintViolation
import com.custodix.insite.local.ehr2edc.shared.exceptions.UseCaseConstraintViolationException
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId
import com.custodix.insite.local.ehr2edc.vocabulary.UserIdentifier
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.AccessDeniedException

import java.time.Instant
import java.time.LocalDate

import static org.assertj.core.api.Assertions.assertThat

class GetPopulatedEventHistorySpec extends AbstractSpecification {
    @Autowired
    private GetPopulatedEventHistory getEventPopulationHistory
    @Autowired
    private TestGetUsersController userRepository

    @Override
    setup() {
        def user = GetUsersController.User.newBuilder().withId(123).withName("populator").build()
        userRepository.addUsers([user])
    }

    def "Retrieving the population history throws authorization errors when the subject doesn't exist"() {
        given: "A study with events"
        def study = generateKnownStudy(USER_ID_KNOWN)
        and: "All events were populated once"
        def events = populateEvents(study)

        when: "I retrieve the event population history"
        def request = GetPopulatedEventHistory.Request.newBuilder()
                .withSubjectId(SubjectId.of("invalid-subject"))
                .withEventId(events[0].eventDefinitionId)
                .build()
        getEventPopulationHistory.get(request)

        then: "An exception gets thrown that I don't have access to the subject"
        thrown(AccessDeniedException)
    }

    def "Retrieving the population history throws authorization errors when the user is not an investigator"() {
        given: "A study with events"
        def study = generateKnownStudy(USER_ID_KNOWN)
        and: "All events were populated once"
        def events = populateEvents(study)
        and: "I am logged in as an non-investigator"
        withCurrentUserHavingId(UserIdentifier.of("99999"))

        when: "I retrieve the event population history"
        def request = GetPopulatedEventHistory.Request.newBuilder()
                .withSubjectId(study.subjects[0].subjectId)
                .withEventId(events[0].eventDefinitionId)
                .build()
        getEventPopulationHistory.get(request)

        then: "An exception gets thrown that I don't have access to the subject"
        thrown(AccessDeniedException)
    }

    def "Retrieving the population history throws authorization errors when no user is authenticated"() {
        given: "A study with events"
        def study = generateKnownStudy(USER_ID_KNOWN)
        and: "All events were populated once"
        def events = populateEvents(study)
        and: "No user is authenticated"
        withoutAuthenticatedUser()

        when: "I retrieve the event population history"
        def request = GetPopulatedEventHistory.Request.newBuilder()
                .withSubjectId(study.subjects[0].subjectId)
                .withEventId(events[0].eventDefinitionId)
                .build()
        getEventPopulationHistory.get(request)

        then: "An exception gets thrown that I don't have access to the subject"
        thrown(AccessDeniedException)
    }

    def "Retrieving the population history is not allowed when no subject is is provided"() {
        when: "I retrieve the event population history"
        def request = GetPopulatedEventHistory.Request.newBuilder()
                .build()
        getEventPopulationHistory.get(request)

        then: "Access is denied"
        AccessDeniedException e = thrown(AccessDeniedException)
        e.message == "User is not an assigned Investigator"
    }

    def "Retrieving the population history throws a validation exception when the event id is missing"() {
        given: "A study"
        def study = generateKnownStudy(USER_ID_KNOWN)
        and: "All events were populated once"
        def events = populateEvents(study)
        when: "I retrieve the event population history"
        def request = GetPopulatedEventHistory.Request.newBuilder().withSubjectId(study.subjects[0].subjectId)
                .build()
        getEventPopulationHistory.get(request)

        then: "An exception gets thrown that the event id are missing"
        def exception = thrown(UseCaseConstraintViolationException)
        exception.constraintViolations.size() == 1
        assertThat(exception.constraintViolations).containsExactlyInAnyOrder(
                UseCaseConstraintViolation.constraintViolation("eventId", "must not be null")
        )
    }

    def "Retrieving the population history after the event was populated once"() {
        given: "A study with events"
        def study = generateKnownStudy(USER_ID_KNOWN)
        and: "All events were populated once"
        def events = populateEvents(study)

        when: "I retrieve the event population history"
        def request = GetPopulatedEventHistory.Request.newBuilder()
                .withSubjectId(study.subjects[0].subjectId)
                .withEventId(events[0].eventDefinitionId)
                .build()
        def response = getEventPopulationHistory.get(request)

        then: "I receive something"
        response.historyItems.size() == 1
        response.historyItems[0].eventId != null
        response.historyItems[0].populationTime == Instant.parse("2018-12-12T13:10:12Z")
        response.historyItems[0].referenceDate == LocalDate.parse("2016-06-09")
        response.historyItems[0].populator == "populator"
    }

    def "Retrieving the population history after the event was populated once without a populator"() {
        given: "A study with events"
        def study = generateKnownStudy(USER_ID_KNOWN)
        and: "All events were populated once, but the populator is missing"
        def events = populateEventsWithoutPopulator(study)

        when: "I retrieve the event population history"
        def request = GetPopulatedEventHistory.Request.newBuilder()
                .withSubjectId(study.subjects[0].subjectId)
                .withEventId(events[0].eventDefinitionId)
                .build()
        def response = getEventPopulationHistory.get(request)

        then: "I receive something"
        response.historyItems.size() == 1
        response.historyItems[0].eventId != null
        response.historyItems[0].populationTime == Instant.parse("2018-12-12T13:10:12Z")
        response.historyItems[0].referenceDate == LocalDate.parse("2016-06-09")
        response.historyItems[0].populator == null
    }

    def "Retrieving the population history after the event was populated twice"() {
        given: "A study with events"
        def study = generateKnownStudy(USER_ID_KNOWN)
        and: "All events were populated twice"
        populateEvents(study)
        def events = populateEvents(study)

        when: "I retrieve the event population history"
        def request = GetPopulatedEventHistory.Request.newBuilder()
                .withSubjectId(study.subjects[0].subjectId)
                .withEventId(events[0].eventDefinitionId)
                .build()
        def response = getEventPopulationHistory.get(request)

        then: "I receive something"
        response.historyItems.size() == 2
        response.historyItems[0].eventId != null
        response.historyItems[0].populationTime == Instant.parse("2018-12-12T13:10:12Z")
        response.historyItems[0].referenceDate == LocalDate.parse("2016-06-09")
        response.historyItems[0].populator == "populator"
        response.historyItems[1].eventId != null
        response.historyItems[1].populationTime == Instant.parse("2018-12-12T13:10:12Z")
        response.historyItems[1].referenceDate == LocalDate.parse("2016-06-09")
        response.historyItems[1].populator == "populator"
    }
}