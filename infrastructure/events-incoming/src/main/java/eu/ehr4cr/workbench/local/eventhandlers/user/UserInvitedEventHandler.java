package eu.ehr4cr.workbench.local.eventhandlers.user;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.custodix.insite.local.user.SendUserInviteMessage;
import com.custodix.insite.local.user.SendUserInviteMessage.Request;

@Component
class UserInvitedEventHandler {
	private final SendUserInviteMessage sendUserInviteMessage;

	UserInvitedEventHandler(SendUserInviteMessage sendUserInviteMessage) {
		this.sendUserInviteMessage = sendUserInviteMessage;
	}

	@EventListener
	public void handleUserInvitedEvent(UserInvitedEvent userInvitedEvent) {
		Request request = Request.newBuilder()
				.withUser(userInvitedEvent.getUser())
				.build();
		sendUserInviteMessage.send(request);
	}
}
