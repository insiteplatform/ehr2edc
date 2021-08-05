package com.custodix.insite.local.ehr2edc.application

import com.custodix.insite.local.ehr2edc.query.GetObservationSummary
import com.custodix.insite.local.ehr2edc.query.observationsummary.ObservationSummaryEHRGateway
import com.custodix.insite.local.ehr2edc.query.observationsummary.ObservationSummaryItem
import com.custodix.insite.local.ehr2edc.snapshots.StudySnapshot
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.security.access.AccessDeniedException

import java.time.LocalDate

import static java.util.Arrays.asList
import static java.util.Collections.emptyList
import static org.mockito.BDDMockito.given

class GetObservationSummarySpec extends AbstractSpecification {

    @Autowired
    private GetObservationSummary getObservationSummary

    @MockBean
    private ObservationSummaryEHRGateway observationSummaryEHRGateway

    def "Getting the observation summary for with no subject id"() {
        when: "I ask for the observation summary without a subject id"
        def request = GetObservationSummary.Request.newBuilder().withSubjectId(null).build()
        getObservationSummary.getSummary(request)

        then: "Access is denied"
        AccessDeniedException e = thrown(AccessDeniedException)
        e.message == "User is not an assigned Investigator"
    }

    def "Getting the observation summary for with an invalid subject id"() {
        given: "An invalid subject identifier"
        def subjectId = SubjectId.newBuilder().build()

        when: "I ask for the observation summary with that subject identifier"
        def request = GetObservationSummary.Request.newBuilder().withSubjectId(subjectId).build()
        getObservationSummary.getSummary(request)

        then: "Indicate the user has no permission to perform the request"
        AccessDeniedException exception = thrown(AccessDeniedException)
        exception.message == "User is not an assigned Investigator"
    }

    def "Getting the observation summary for a subject without observations"() {
        given: "A subject without observations"
        StudySnapshot study = generateKnownStudy(USER_ID_KNOWN)
        def subjectId = study.subjects[0].subjectId
        given(observationSummaryEHRGateway.findForSubject(subjectId)).willReturn(emptyList())

        when: "I ask for the observation summary for that subject"
        def request = GetObservationSummary.Request.newBuilder().withSubjectId(subjectId).build()
        def response = getObservationSummary.getSummary(request)

        then: "I get an empty list of observation summary data"
        response.summaryItems.empty
    }

    def "Getting the observation summary for a subject as user who is not assigned investigator"() {
        given: "A subject with a single observation on 01-01-2015"
        StudySnapshot study = generateKnownStudy(USER_ID_OTHER)
        def subjectId = study.subjects[0].subjectId
        given(observationSummaryEHRGateway.findForSubject(subjectId)).willReturn(asList(
                buildObservationSummaryItem(LocalDate.of(2015, 1, 1), "medication")))

        when: "I ask for the observation summary for that subject"
        def request = GetObservationSummary.Request.newBuilder().withSubjectId(subjectId).build()
        def response = getObservationSummary.getSummary(request)

        then: "Acess is denied"
        AccessDeniedException exception = thrown(AccessDeniedException)
        exception.message == "User is not an assigned Investigator"
    }

    def "Getting the observation summary for a subject as user who is not authenticated"() {
        given: "A subject with a single observation on 01-01-2015"
        StudySnapshot study = generateKnownStudy(USER_ID_KNOWN)
        def subjectId = study.subjects[0].subjectId
        given(observationSummaryEHRGateway.findForSubject(subjectId)).willReturn(asList(
                buildObservationSummaryItem(LocalDate.of(2015, 1, 1), "medication")))
        and: "No authenticated user"
        withoutAuthenticatedUser()

        when: "I ask for the observation summary for that subject"
        def request = GetObservationSummary.Request.newBuilder().withSubjectId(subjectId).build()
        def response = getObservationSummary.getSummary(request)

        then: "Access is denied"
        AccessDeniedException exception = thrown(AccessDeniedException)
        exception.message == "User is not an assigned Investigator"
    }

    def "Getting the observation summary for a subject with a single observation"() {
        given: "A subject with a single observation on 01-01-2015"
        StudySnapshot study = generateKnownStudy(USER_ID_KNOWN)
        def subjectId = study.subjects[0].subjectId
        given(observationSummaryEHRGateway.findForSubject(subjectId)).willReturn(asList(
                buildObservationSummaryItem(LocalDate.of(2015, 1, 1), "medication")))

        when: "I ask for the observation summary for that subject"
        def request = GetObservationSummary.Request.newBuilder().withSubjectId(subjectId).build()
        def response = getObservationSummary.getSummary(request)

        then: "I get an list of observations with a single item on 01-01-2015"
        response.summaryItems.size() == 1
        response.summaryItems[0].date == LocalDate.of(2015, 1, 1)
        response.summaryItems[0].amountOfObservations == 5
    }

    def "Getting the observation summary for a subject with observations over multiple dates"() {
        given: "A subject with observations on 01-01-2015, 01-02-2015 and 01-03-2015"
        StudySnapshot study = generateKnownStudy(USER_ID_KNOWN)
        def subjectId = study.subjects[0].subjectId
        def observationSummaryItems = asList(
                buildObservationSummaryItem(LocalDate.of(2015, 1, 1), "medication"),
                buildObservationSummaryItem(LocalDate.of(2015, 2, 1), "medication"),
                buildObservationSummaryItem(LocalDate.of(2015, 3, 1), "medication"))
        given(observationSummaryEHRGateway.findForSubject(subjectId)).willReturn(observationSummaryItems)

        when: "I ask for the observation summary for that subject"
        def request = GetObservationSummary.Request.newBuilder().withSubjectId(subjectId).build()
        def response = getObservationSummary.getSummary(request)

        then: "I get an list of observations with items on 01-01-2015, 01-02-2015 and 01-03-2015"
        response.summaryItems.size() == 3
        response.summaryItems[0].date == LocalDate.of(2015, 1, 1)
        response.summaryItems[1].date == LocalDate.of(2015, 2, 1)
        response.summaryItems[2].date == LocalDate.of(2015, 3, 1)
    }

    def "Getting the observation summary for a subject with observations over multiple categories"() {
        given: "A subject with observations in medication, vital signs and laboratory"
        StudySnapshot study = generateKnownStudy(USER_ID_KNOWN)
        def subjectId = study.subjects[0].subjectId
        def observationSummaryItems = asList(
                buildObservationSummaryItem(LocalDate.of(2015, 1, 1), "medication"),
                buildObservationSummaryItem(LocalDate.of(2015, 1, 1), "vital signs"),
                buildObservationSummaryItem(LocalDate.of(2015, 1, 1), "laboratory"))
        given(observationSummaryEHRGateway.findForSubject(subjectId)).willReturn(observationSummaryItems)

        when: "I ask for the observation summary for that subject"
        def request = GetObservationSummary.Request.newBuilder().withSubjectId(subjectId).build()
        def response = getObservationSummary.getSummary(request)

        then: "I get a single observation with the amount set to the sum of all 3 observations"
        response.summaryItems.size() == 1
        response.summaryItems[0].amountOfObservations == 15
    }

    private ObservationSummaryItem buildObservationSummaryItem(LocalDate date, String category) {
        return new ObservationSummaryItem(date, category, 5)
    }
}