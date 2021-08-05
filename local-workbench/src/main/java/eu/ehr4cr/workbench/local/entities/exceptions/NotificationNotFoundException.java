package eu.ehr4cr.workbench.local.entities.exceptions;

import java.text.MessageFormat;

public class NotificationNotFoundException extends RuntimeException {

	public NotificationNotFoundException(Long notificationId) {
		super(MessageFormat.format("No notification found for id {0}.", notificationId));
	}
}
