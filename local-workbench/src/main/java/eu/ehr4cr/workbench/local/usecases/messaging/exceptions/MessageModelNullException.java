package eu.ehr4cr.workbench.local.usecases.messaging.exceptions;

public class MessageModelNullException extends RuntimeException {
	public MessageModelNullException() {
		super("Message model passed to converter is null.");
	}
}
