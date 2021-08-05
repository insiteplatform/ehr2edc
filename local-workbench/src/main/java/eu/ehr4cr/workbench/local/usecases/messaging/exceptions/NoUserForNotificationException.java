package eu.ehr4cr.workbench.local.usecases.messaging.exceptions;

import java.text.MessageFormat;

public class NoUserForNotificationException extends RuntimeException {

	public NoUserForNotificationException(String userType, Long userId) {
		super(MessageFormat.format("No notification {0} with id: {1}", userType, userId));
	}
}
