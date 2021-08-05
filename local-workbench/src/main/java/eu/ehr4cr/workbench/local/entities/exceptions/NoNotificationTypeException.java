package eu.ehr4cr.workbench.local.entities.exceptions;

import java.text.MessageFormat;

public class NoNotificationTypeException extends RuntimeException {

	public NoNotificationTypeException(String notificationType) {
		super(MessageFormat.format("No {0} notification type defined.", notificationType));
	}
}
