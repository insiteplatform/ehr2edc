package eu.ehr4cr.workbench.local.usecases.messaging.exceptions;

public class MessageEntityNullException extends RuntimeException {
	public MessageEntityNullException() {
		super("Message entity passed to converter is null.");
	}
}
