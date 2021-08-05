package eu.ehr4cr.workbench.local.eventhandlers.user;

import eu.ehr4cr.workbench.local.model.security.User;

public class UserInvitedEvent {
	private final User user;

	public UserInvitedEvent(User user) {
		this.user = user;
	}

	public User getUser() {
		return user;
	}
}
