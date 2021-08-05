package eu.ehr4cr.workbench.local.entities.exceptions;

import java.text.MessageFormat;

public class MessageNotFoundException extends RuntimeException {

	public MessageNotFoundException(Long messageId) {
		super(MessageFormat.format("No message found for id {0}", messageId));
	}
}
