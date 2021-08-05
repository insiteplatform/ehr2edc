package eu.ehr4cr.workbench.local.entities.enums;

import eu.ehr4cr.workbench.local.entities.exceptions.NoPriorityException;

public enum Priority {
	LOW, MEDIUM, HIGH;

	public static Priority getPriorityForString(String priority) {
		for (Priority notificationPriority : Priority.values()) {
			if (notificationPriority.name()
					.equalsIgnoreCase(priority)) {
				return notificationPriority;
			}
		}
		throw new NoPriorityException(priority);
	}

}
