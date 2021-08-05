package eu.ehr4cr.workbench.local.eventhandlers.user

import com.custodix.insite.local.user.domain.events.ImminentlyExpiringPasswordEvent
import com.custodix.insite.local.user.infra.notifications.ImminentlyExpiringPasswordNotification
import eu.ehr4cr.workbench.local.WebRoutes
import spock.lang.Timeout

import static org.awaitility.Awaitility.await

@Timeout(10)
class ImminentlyExpiringPasswordEventControllerTest extends UserAsyncEventsTest {
    private static final String USER_MAIL_ADDRESS = "devnull@custodix.com"
    private static final Date EXPIRATION_DATE = new Date()

    def "An 'ImminentlyExpiringPasswordEvent' triggers a mail notification"() {
        given: "An 'ImminentlyExpiringPasswordEvent'"
        ImminentlyExpiringPasswordEvent event = ImminentlyExpiringPasswordEvent.newBuilder()
                .withUserMailAddress(USER_MAIL_ADDRESS)
                .withExpirationDate(EXPIRATION_DATE)
                .build()

        when: "The event is published"
        eventPublisher.publishEvent(event)

        then: "A mail notification is sent"
        await().until { !mailNotificationService.isEmpty() }
        ImminentlyExpiringPasswordNotification notification = mailNotificationService.poll() as ImminentlyExpiringPasswordNotification
        with(notification) {
            userMailAddress == USER_MAIL_ADDRESS
            expirationDate == EXPIRATION_DATE
            changePasswordUrl == "http://test.domain.com" + WebRoutes.MY_ACCOUNT_PASSWORD
        }
    }
}
