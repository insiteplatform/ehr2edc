package eu.ehr4cr.workbench.local.entities.enums;

import eu.ehr4cr.workbench.local.entities.exceptions.NoNotificationTypeException;

public enum NotificationType {
	MESSAGE;

	public static NotificationType getNotificationTypeForString(String notificationType) {
		for (NotificationType type : NotificationType.values()) {
			if (type.name()
					.equalsIgnoreCase(notificationType)) {
				return type;
			}
		}
		throw new NoNotificationTypeException(notificationType);
	}
}
