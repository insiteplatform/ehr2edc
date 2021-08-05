package eu.ehr4cr.workbench.local.eventhandlers.user

import com.custodix.insite.local.user.domain.events.PasswordRecoveredEvent
import com.custodix.insite.local.user.vocabulary.Email

import static org.mockito.ArgumentMatchers.argThat
import static org.mockito.Mockito.timeout
import static org.mockito.Mockito.verify

class PasswordRecoveredEventControllerSpec extends UserAsyncEventsTest {
    private static final Email EMAIL = Email.of("devnull@custodix.com")

    def "A 'PasswordRecoveredEvent' triggers an account unlock"() {
        given: "An 'PasswordRecoveredEvent'"
        PasswordRecoveredEvent event = PasswordRecoveredEvent.newBuilder()
                .withEmail(EMAIL)
                .build()

        when: "The event is published"
        eventPublisher.publishEvent(event)

        then: "Authentication as SystemUser to have the MANAGE_ACCOUNTS authority"
        verify(systemUserAuthenticator, timeout(5000)).authenticate()
        then: "The account is unlocked"
        verify(unlockAccount, timeout(5000)).unlockAccount(argThat({
            r -> r.email == EMAIL
        }))
    }
}
