package eu.ehr4cr.workbench.local.usecases.messaging.converter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eu.ehr4cr.workbench.local.entities.dao.impl.model.Notification;
import eu.ehr4cr.workbench.local.model.security.User;
import eu.ehr4cr.workbench.local.service.IUserMgrService;
import eu.ehr4cr.workbench.local.usecases.messaging.model.NotificationModel;

@RunWith(SpringJUnit4ClassRunner.class)
public class NotificationEntityConverterTest extends ConverterTestConfig {

	@Mock
	private IUserMgrService iUserMgrService;

	@InjectMocks
	private NotificationEntityConverter notificationEntityConverter = new NotificationEntityConverter(iUserMgrService);

	private static final String IDS_DO_NOT_MATCH = "Ids do not match";
	private static final String EXPIRY_DATE_DOES_NOT_MATCH = "Expiry date does not match";
	private static final String PRIORITY_DOES_NOT_MATCH = "Priority does not match";
	private static final String READING_DATE_DOES_NOT_MATCH = "Reading date does not match";
	private static final String RECIPIENT_IDS_DO_NOT_MATCH = "Recipient ids do not match";
	private static final String SENDER_IDS_DO_NOT_MATCH = "Sender ids do not match";
	private static final String SENDING_DATE_DOES_NOT_MATCH = "Sending date does not match";
	private static final String TYPE_DOES_NOT_MATCH = "Type does not match";

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		User sender = new User("testSender", "senderPWD");
		sender.setId(SENDER_ID);
		User recipient = new User("testRecipient", "recipientPWD");
		recipient.setId(RECIPIENT_ID);
		when(iUserMgrService.findUserById(SENDER_ID)).thenReturn(sender);
		when(iUserMgrService.findUserById(RECIPIENT_ID)).thenReturn(recipient);
	}

	@Test
	public void testFromEntityConversion() {
		Notification notification = getNotificationWithId();
		NotificationModel notificationModel = notificationEntityConverter.fromEntity(notification, new ArrayList<>());
		Notification convertedNotification = notificationEntityConverter.convert(notificationModel);
		assertNotificationEntitiesAreEqual(notification, convertedNotification);
	}

	@Test
	public void testToEntityConversion() {
		NotificationModel notificationModel = getNewNotificationModel();
		Notification notification = notificationEntityConverter.convert(notificationModel);
		NotificationModel convertedNotificationModel = notificationEntityConverter.fromEntity(notification,
				new ArrayList<>());
		assertNotificationModelsAreEqual(notificationModel, convertedNotificationModel);
	}

	private void assertNotificationModelsAreEqual(NotificationModel original, NotificationModel converted) {
		assertThat(converted.getId()).as(IDS_DO_NOT_MATCH)
				.isEqualTo(original.getId());
		assertThat(converted.getExpiryDate()).as(EXPIRY_DATE_DOES_NOT_MATCH)
				.isEqualTo(original.getExpiryDate());
		assertThat(converted.getPriority()).as(PRIORITY_DOES_NOT_MATCH)
				.isEqualTo(original.getPriority());
		assertThat(converted.getReadingDate()).as(READING_DATE_DOES_NOT_MATCH)
				.isEqualTo(original.getReadingDate());
		assertThat(converted.getRecipientId()).as(RECIPIENT_IDS_DO_NOT_MATCH)
				.isEqualTo(original.getRecipientId());
		assertThat(converted.getSenderId()).as(SENDER_IDS_DO_NOT_MATCH)
				.isEqualTo(original.getSenderId());
		assertThat(converted.getSendingDate()).as(SENDING_DATE_DOES_NOT_MATCH)
				.isEqualTo(original.getSendingDate());
		assertThat(converted.getType()).as(TYPE_DOES_NOT_MATCH)
				.isEqualTo(original.getType());
	}

	private void assertNotificationEntitiesAreEqual(Notification original, Notification converted) {
		assertThat(converted.getId()).as(IDS_DO_NOT_MATCH)
				.isEqualTo(original.getId());
		assertThat(converted.getExpiryDate()).as(EXPIRY_DATE_DOES_NOT_MATCH)
				.isEqualTo(original.getExpiryDate());
		assertThat(converted.getPriority()).as(PRIORITY_DOES_NOT_MATCH)
				.isEqualTo(original.getPriority());
		assertThat(converted.getReadingDate()).as(READING_DATE_DOES_NOT_MATCH)
				.isEqualTo(original.getReadingDate());
		assertThat(converted.getRecipient()
				.getId()).as(RECIPIENT_IDS_DO_NOT_MATCH)
				.isEqualTo(original.getRecipient()
						.getId());
		assertThat(converted.getSender()
				.getId()).as(SENDER_IDS_DO_NOT_MATCH)
				.isEqualTo(original.getSender()
						.getId());
		assertThat(converted.getSendingDate()).as(SENDING_DATE_DOES_NOT_MATCH)
				.isEqualTo(original.getSendingDate());
		assertThat(converted.getType()).as(TYPE_DOES_NOT_MATCH)
				.isEqualTo(original.getType());
	}
}
