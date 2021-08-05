package eu.ehr4cr.workbench.local.usecases.messaging;

import java.util.Date;

import org.junit.Test;

import eu.ehr4cr.workbench.local.AbstractWorkbenchTest;
import eu.ehr4cr.workbench.local.entities.dao.impl.model.Message;
import eu.ehr4cr.workbench.local.entities.dao.impl.model.MessageBuilder;
import eu.ehr4cr.workbench.local.entities.dao.impl.model.Notification;
import eu.ehr4cr.workbench.local.entities.dao.impl.model.NotificationBuilder;
import eu.ehr4cr.workbench.local.entities.enums.NotificationType;
import eu.ehr4cr.workbench.local.entities.enums.Priority;
import eu.ehr4cr.workbench.local.model.security.User;
import eu.ehr4cr.workbench.local.usecases.messaging.model.MessageModel;
import eu.ehr4cr.workbench.local.usecases.messaging.model.MessageModelBuilder;
import eu.ehr4cr.workbench.local.usecases.messaging.model.NotificationModel;
import eu.ehr4cr.workbench.local.usecases.messaging.model.NotificationModelBuilder;

public class UseCaseTestConfig extends AbstractWorkbenchTest {

	private static final String MESSAGE_SUBJECT = "Subject";
	private static final String MESSAGE_BODY = "Body";
	private static final long NOTIFICATION_ID = 1L;
	private static final long MESSAGE_ID = 1L;
	protected static final long RECIPIENT_ID = 2L;
	protected static final long SENDER_ID = 1L;

	@Test
	public void name() {

	}

	protected User getNewUser(String username, String pass) {
		return new User(username, pass);
	}

	private NotificationBuilder newNotification() {
		User sender = new User("senderUsername", "senderPWD");
		sender.setId(1L);
		User recipient = new User("recipientUsername", "recipientPWD");
		recipient.setId(2L);
		return NotificationBuilder.builder()
				.sender(sender)
				.recipient(recipient)
				.type(NotificationType.MESSAGE)
				.priority(Priority.HIGH)
				.sendingDate(new Date());
	}

	public Notification getNotificationWithId() {
		final Notification notification = newNotification().notificationId(NOTIFICATION_ID)
				.build();
		return notification;
	}

	public Message getMessageWithId(Notification notificationWithId) {
		final Message message = getNewMessage(notificationWithId).messageId(MESSAGE_ID)
				.build();
		return message;
	}

	protected Notification getNotificationWithoutId() {
		return newNotification().build();
	}

	private MessageBuilder getNewMessage(Notification notificationWithId) {
		return MessageBuilder.builder()
				.subject(MESSAGE_SUBJECT)
				.body(MESSAGE_BODY)
				.notification(notificationWithId);
	}

	public NotificationModel getNewNotificationModel() {
		NotificationModelBuilder notificationModelBuilder = NotificationModelBuilder.builder()
				.priority("HIGH")
				.recipientId(RECIPIENT_ID)
				.senderId(SENDER_ID)
				.sendingDate(new Date())
				.type("MESSAGE")
				.notificationId(NOTIFICATION_ID);
		NotificationModel notificationModel = notificationModelBuilder.build();
		notificationModel.addContentToContents(getNewMessageModel());
		return notificationModel;
	}

	public MessageModel getNewMessageModel() {
		return MessageModelBuilder.builder()
				.body(MESSAGE_BODY)
				.subject(MESSAGE_SUBJECT)
				.notificationId(NOTIFICATION_ID)
				.messageId(MESSAGE_ID)
				.build();

	}
}
