package eu.ehr4cr.workbench.local.entities.dao;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.custodix.insite.local.user.vocabulary.Email;

import eu.ehr4cr.workbench.local.dao.SecurityDao;
import eu.ehr4cr.workbench.local.entities.dao.impl.model.Notification;
import eu.ehr4cr.workbench.local.entities.exceptions.NotificationNotFoundException;
import eu.ehr4cr.workbench.local.model.security.User;

@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class NotificationRepositoryUnitTest extends EntityUnitTestConfig {

	private static final Email SENDER_EMAIL = Email.of("sender@custodix.com");
	private static final Email RECIPIENT_EMAIL = Email.of("recipient@custodix.com");
	@Autowired
	private NotificationRepository notificationRepository;

	@Autowired
	private SecurityDao securityDao;

	private User sender;
	private User recipient;

	@Before
	public void setupData() {
		sender = securityDao.createUser(SENDER_EMAIL.getValue(), "senderPWDNotificationRepositoryUnitTest");
		recipient = securityDao.createUser(RECIPIENT_EMAIL.getValue(), "recipientPWDNotificationRepositoryUnitTest");
	}

	@Test
	public void testGetAllNotificationsForUser() {
		notificationRepository.createNotification(getNewNotification(sender, recipient));
		List<Notification> allNotification = notificationRepository.getAllNotificationsForUser(recipient);
		assertThat(allNotification).as("User notifications are null.")
				.isNotNull();
		assertThat(allNotification.isEmpty()).as("User notifications are empty.")
				.isFalse();
		assertThat(allNotification.size() == 1).as("There is more than 1 notification.")
				.isTrue();
	}

	@Test
	public void testCreateNotification() {
		Notification notification = notificationRepository.createNotification(getNewNotification(sender, recipient));
		assertThat(notification.getId()).as("Notification id is null.")
				.isNotNull();
	}

	@Test(expected = NotificationNotFoundException.class)
	public void testDeleteNotification() {
		Notification notification = notificationRepository.createNotification(getNewNotification(sender, recipient));
		notificationRepository.deleteNotification(notification.getId());
		assertThat(notificationRepository.getNotificationById(notification.getId())).as("Notification was not deleted.")
				.isNull();
	}

	@Test
	public void testGetAllUnreadNotificationsForUser() {
		notificationRepository.createNotification(getNewNotification(sender, recipient));
		List<Notification> unreadNotifications = notificationRepository.getAllUnreadNotificationsForUser(recipient);
		assertThatNotificationAreExisting(unreadNotifications);

	}

	private void assertThatNotificationAreExisting(List<Notification> unreadNotifications) {
		assertThat(unreadNotifications).as("Unread notifications are null.")
				.isNotNull();
		assertThat(unreadNotifications.isEmpty()).as("Unread notifications are empty.")
				.isFalse();
	}

	@Test
	public void testUpdateNotification() {
		notificationRepository.createNotification(getNewNotification(sender, recipient));
		List<Notification> unreadNotifications = notificationRepository.getAllUnreadNotificationsForUser(recipient);
		unreadNotifications.get(0)
				.readMessage();
		notificationRepository.updateNotification(unreadNotifications.get(0));

		unreadNotifications = notificationRepository.getAllUnreadNotificationsForUser(recipient);
		assertThat(unreadNotifications.isEmpty()).as("Notification was not read.")
				.isTrue();
	}

}
