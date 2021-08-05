package eu.ehr4cr.workbench.local.eventhandlers.user

import com.custodix.insite.local.user.SendUserInviteMessage
import eu.ehr4cr.workbench.local.model.security.User
import org.mockito.ArgumentCaptor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.ApplicationEventPublisher
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

import static com.custodix.insite.local.user.SendUserInviteMessage.Request
import static org.assertj.core.api.Assertions.assertThat
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.verify

@ContextConfiguration(classes = UserInvitedEventHandler.class)
class UserInvitedEventHandlerTest extends Specification {
    @MockBean
    private SendUserInviteMessage sendUserInviteMessage
    @Autowired
    private ApplicationEventPublisher eventPublisher

    def "A user invited event triggers an invitation message to the user"() {
        given: "A user"
        User user = mock(User.class)

        and: "A user invited event"
        UserInvitedEvent event = new UserInvitedEvent(user)

        when: "The event is published"
        eventPublisher.publishEvent(event)

        then: "An invitation message is sent"
        validateInvitationMessageSent(user)
    }

    private void validateInvitationMessageSent(User user) {
        ArgumentCaptor<Request> requestCaptor = ArgumentCaptor.forClass(Request.class)
        verify(sendUserInviteMessage).send(requestCaptor.capture())
        validateInvitationMessage(requestCaptor.getValue(), user)
    }

    private void validateInvitationMessage(Request request, User user) {
        assertThat(request).isNotNull()
        assertThat(request.getUser()).isEqualTo(user)
    }
}
