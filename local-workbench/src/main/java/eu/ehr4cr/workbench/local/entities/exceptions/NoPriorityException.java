package eu.ehr4cr.workbench.local.entities.exceptions;

import java.text.MessageFormat;

public class NoPriorityException extends RuntimeException {

	public NoPriorityException(String priority) {
		super(MessageFormat.format("No {0} priority level defined.", priority));
	}
}
