package eu.ehr4cr.workbench.local.usecases.messaging.exceptions;

public class MessagesListIsNullException extends RuntimeException {
	public MessagesListIsNullException() {
		super("MessageModel list passed is null.");
	}
}
