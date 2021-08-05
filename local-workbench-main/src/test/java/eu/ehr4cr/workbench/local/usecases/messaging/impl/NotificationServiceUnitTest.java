package eu.ehr4cr.workbench.local.usecases.messaging.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eu.ehr4cr.workbench.local.entities.dao.NotificationRepository;
import eu.ehr4cr.workbench.local.entities.dao.impl.model.Notification;
import eu.ehr4cr.workbench.local.model.security.User;
import eu.ehr4cr.workbench.local.service.IUserMgrService;
import eu.ehr4cr.workbench.local.usecases.messaging.MessageService;
import eu.ehr4cr.workbench.local.usecases.messaging.NotificationService;
import eu.ehr4cr.workbench.local.usecases.messaging.UseCaseTestConfig;
import eu.ehr4cr.workbench.local.usecases.messaging.converter.NotificationEntityConverter;
import eu.ehr4cr.workbench.local.usecases.messaging.model.NotificationModel;

@RunWith(SpringJUnit4ClassRunner.class)
public class NotificationServiceUnitTest extends UseCaseTestConfig {

	@Mock
	private MessageService messageService;

	@Mock
	private NotificationRepository notificationRepository;

	@Mock
	private IUserMgrService iUserMgrService;

	@Mock
	private NotificationEntityConverter notificationEntityConverter;

	private NotificationService notificationService;

	private NotificationModel notificationModel;
	private Notification notificationWithoutId;
	private Notification notificationWithId;

	private User user;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		notificationService = new NotificationServiceImpl(notificationRepository, messageService,
				notificationEntityConverter);
		notificationModel = getNewNotificationModel();
		notificationWithoutId = getNotificationWithoutId();
		notificationWithId = getNotificationWithId();
		user = getNewUser("test", "testPWD");
		List<Notification> notifications = new ArrayList<>();
		notifications.add(notificationWithId);

		when(notificationEntityConverter.convert(notificationModel)).thenReturn(notificationWithoutId);
		when(notificationRepository.createNotification(eq(notificationWithoutId))).thenReturn(notificationWithId);
		when(notificationRepository.getNotificationById(eq(1L))).thenReturn(notificationWithId);
		when(notificationRepository.getAllNotificationsForUser(eq(user))).thenReturn(notifications);
		when(notificationRepository.getAllUnreadNotificationsForUser(eq(user))).thenReturn(notifications);

		when(notificationEntityConverter.fromEntity(eq(notificationWithId), any())).thenReturn(notificationModel);
		doNothing().when(notificationRepository)
				.deleteNotification(eq(1L));
		doNothing().when(notificationRepository)
				.updateNotification(notificationWithId);
	}

	@Test
	public void testCreateNewNotification() {
		NotificationModel notification = notificationService.createNewNotification(notificationModel);
		assertThatNotificationIsExisting(notification);
		assertThat(notification.getId()).as("Notification id doesn't match")
				.isEqualTo(1L);
	}

	@Test
	public void testMarkNotificationAsRead() {
		notificationService.markNotificationAsRead(1L);
		verify(notificationRepository).updateNotification(notificationWithId);
	}

	@Test
	public void testDeleteNotification() {
		notificationService.deleteNotification(1L);
		verify(notificationRepository).deleteNotification(1L);
	}

	@Test
	public void testGetUnreadNotificationsForUser() {
		List<NotificationModel> notificationsModels = notificationService.getAllNotificationsForUser(user);
		assertThat(notificationsModels).as("Notification list is null")
				.isNotNull();
		assertThat(!notificationsModels.isEmpty()).as("No notifications found for user")
				.isTrue();
	}

	@Test
	public void testGetAllNotificationsForUser() {
		List<NotificationModel> unreadNotificationsModels = notificationService.getAllUnreadNotificationsForUser(user);
		assertThat(unreadNotificationsModels).as("Unread notification list is null")
				.isNotNull();
		assertThat(!unreadNotificationsModels.isEmpty()).as("No unread notifications found for user")
				.isTrue();
		assertThat(unreadNotificationsModels.get(0)
				.isRead()).as("All notifications are read")
				.isFalse();
	}

	private void assertThatNotificationIsExisting(NotificationModel notification) {
		assertThat(notification).as("Notification is null.")
				.isNotNull();
		assertThat(notification.getId()).as("Notification id is null, notification not persisted")
				.isNotNull();
	}

}
