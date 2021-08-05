package com.custodix.insite.local.ehr2edc.ehr.epic.event.controller

import com.custodix.insite.local.ehr2edc.ehr.epic.command.PatientRegistration
import com.custodix.insite.local.ehr2edc.ehr.epic.domain.event.EpicPortalPatientRegistered
import com.custodix.insite.local.ehr2edc.ehr.epic.event.config.EHREpicEventConfiguration
import eu.ehr4cr.workbench.local.eventhandlers.AsyncEventsTest
import eu.ehr4cr.workbench.local.eventhandlers.AsynchronousEventHandler
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import

import java.time.LocalDate

import static org.mockito.ArgumentMatchers.argThat
import static org.mockito.Mockito.timeout
import static org.mockito.Mockito.verify

@Import([ AsynchronousEventHandler, EHREpicEventConfiguration ])
class SubjectRegistrationControllerSpec extends AsyncEventsTest {

    private static final String FIRST_NAME = "John"
    private static final String LAST_NAME = "SMITH"
    private static final LocalDate DATE_2009_09_09 = LocalDate.of(2009, 9, 9)
    private static final String OAUTH_ACCESS_TOKEN = "aAuth-access-token-123"

    @MockBean
    protected PatientRegistration patientRegistration

    def "The 'EPIC portal patient registered '-event triggers patient registration"() {
        given: 'A EpicPortalPatientRegistered'
        EpicPortalPatientRegistered event = EpicPortalPatientRegistered.newBuilder()
                .withFirstName(FIRST_NAME)
                .withLastName(LAST_NAME)
                .withBirthDate(DATE_2009_09_09)
                .withOAuthAccessToken(OAUTH_ACCESS_TOKEN)
                .build()


        when: "The event is published"
        ehrEpicEventPublisher.publishEvent(event)

        then: "The migration of patient data is triggered"
        verify(patientRegistration, timeout(5000)).register(argThat({
            PatientRegistration.Request request ->
                request.lastName = LAST_NAME &&
                request.firstName == FIRST_NAME &&
                request.birthDate == DATE_2009_09_09 &&
                request.oAuthAccessToken.value == OAUTH_ACCESS_TOKEN
        }) as PatientRegistration.Request)
    }
}
