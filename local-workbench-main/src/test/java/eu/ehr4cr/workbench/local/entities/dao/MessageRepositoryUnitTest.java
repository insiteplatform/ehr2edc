package eu.ehr4cr.workbench.local.entities.dao;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import eu.ehr4cr.workbench.local.dao.SecurityDao;
import eu.ehr4cr.workbench.local.entities.dao.impl.model.Message;
import eu.ehr4cr.workbench.local.entities.dao.impl.model.Notification;
import eu.ehr4cr.workbench.local.entities.exceptions.MessageNotFoundException;
import eu.ehr4cr.workbench.local.model.security.User;

@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class MessageRepositoryUnitTest extends EntityUnitTestConfig {

	@Autowired
	private NotificationRepository notificationRepository;

	@Autowired
	private MessageRepository messageRepository;

	@Autowired
	private SecurityDao securityDao;

	private User sender;
	private User recipient;
	private Notification notification;

	@Before
	public void setupData() {
		sender = securityDao.createUser("sender", "senderPWD");
		recipient = securityDao.createUser("recipient", "recipientPWD");
		notification = notificationRepository.createNotification(getNewNotification(sender, recipient));
		messageRepository.saveMessage(getNewMessage(notification));
		messageRepository.saveMessage(getNewMessage(notification));
	}

	@Test
	public void testCreateMessage() {
		Message message = messageRepository.saveMessage(getNewMessage(notification));
		assertThatMessageWasSaved(message);
	}

	@Test(expected = MessageNotFoundException.class)
	public void testDeleteMessage() {
		Message message = messageRepository.saveMessage(getNewMessage(notification));
		assertThatMessageWasSaved(message);
		messageRepository.deleteMessage(message.getId());
		assertThat(messageRepository.getMessageById(message.getId())).as("Message was not deleted.")
				.isNull();
	}

	private void assertThatMessageWasSaved(Message message) {
		assertThat(message).as("Message not saved!")
				.isNotNull();
		assertThat(message.getId()).as("Message not saved, no ID!")
				.isNotNull();
	}

	@Test
	public void testGetMessagesForNotification() {
		List<Message> messagesForNotification = messageRepository.getMessagesForNotificationId(notification.getId());
		assertThat(messagesForNotification).as("Messages list is null.")
				.isNotNull();
		assertThat(!messagesForNotification.isEmpty()).as("No messages for notification.")
				.isTrue();
	}

}
