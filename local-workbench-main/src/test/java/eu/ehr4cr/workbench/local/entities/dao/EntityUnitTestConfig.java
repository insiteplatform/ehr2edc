package eu.ehr4cr.workbench.local.entities.dao;

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

public class EntityUnitTestConfig extends AbstractWorkbenchTest {

	@Test
	public void name() {

	}

	protected Notification getNewNotification(User sender, User recipient) {
		return NotificationBuilder.builder()
				.sender(sender)
				.recipient(recipient)
				.type(NotificationType.MESSAGE)
				.priority(Priority.HIGH)
				.sendingDate(new Date())
				.build();
	}

	protected Message getNewMessage(Notification notification) {
		return MessageBuilder.builder()
				.subject("Subject")
				.body("Body")
				.notification(notification)
				.build();
	}
}
