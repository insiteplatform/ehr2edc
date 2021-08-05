package eu.ehr4cr.workbench.local.usecases.messaging.converter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eu.ehr4cr.workbench.local.entities.dao.NotificationRepository;
import eu.ehr4cr.workbench.local.entities.dao.impl.model.Message;
import eu.ehr4cr.workbench.local.usecases.messaging.model.MessageModel;

@RunWith(SpringJUnit4ClassRunner.class)
public class MessageEntityConverterTest extends ConverterTestConfig {

	@Mock
	private NotificationRepository notificationRepository;

	@InjectMocks
	private MessageEntityConverter messageEntityConverter = new MessageEntityConverter(notificationRepository);

	private static final String IDS_DO_NOT_MATCH = "Ids do not match";
	private static final String NOTIFICATION_IDS_DO_NOT_MATCH = "Notification ids do not match";
	private static final String BODY_DOES_NOT_MATCH = "Body does not match";
	private static final String SUBJECT_DOES_NOT_MATCH = "Subject does not match";
	private static final String ATTACHMENT_DOES_NOT_MATCH = "Attachment does not match";

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		when(notificationRepository.getNotificationById(1L)).thenReturn(getNotificationWithId());
	}

	@Test
	public void testFromEntityConversion() {
		Message message = getMessageWithId(getNotificationWithId());
		MessageModel messageModel = messageEntityConverter.fromEntity(message);
		Message convertedMessage = messageEntityConverter.convert(messageModel);
		assertMessageEntitiesAreEqual(message, convertedMessage);
	}

	private void assertMessageEntitiesAreEqual(Message message, Message convertedMessage) {
		assertThat(convertedMessage.getId()).as(IDS_DO_NOT_MATCH)
				.isEqualTo(message.getId());
		assertThat(convertedMessage.getNotification()
				.getId()).as(NOTIFICATION_IDS_DO_NOT_MATCH)
				.isEqualTo(message.getNotification()
						.getId());
		assertThat(convertedMessage.getBody()).as(BODY_DOES_NOT_MATCH)
				.isEqualTo(message.getBody());
		assertThat(convertedMessage.getSubject()).as(SUBJECT_DOES_NOT_MATCH)
				.isEqualTo(message.getSubject());
		assertThat(convertedMessage.getAttachment()).as(ATTACHMENT_DOES_NOT_MATCH)
				.isEqualTo(message.getAttachment());
	}

	@Test
	public void testToEntityConversion() {
		MessageModel messageModel = getNewMessageModel();
		Message message = messageEntityConverter.convert(messageModel);
		MessageModel fromEntityMessageModel = messageEntityConverter.fromEntity(message);
		assertMessageModelsAreEqual(messageModel, fromEntityMessageModel);
	}

	private void assertMessageModelsAreEqual(MessageModel messageModel, MessageModel fromEntityMessageModel) {
		assertThat(fromEntityMessageModel.getId()).as(IDS_DO_NOT_MATCH)
				.isEqualTo(messageModel.getId());
		assertThat(fromEntityMessageModel.getNotificationId()).as(NOTIFICATION_IDS_DO_NOT_MATCH)
				.isEqualTo(messageModel.getNotificationId());
		assertThat(fromEntityMessageModel.getBody()).as(BODY_DOES_NOT_MATCH)
				.isEqualTo(messageModel.getBody());
		assertThat(fromEntityMessageModel.getSubject()).as(SUBJECT_DOES_NOT_MATCH)
				.isEqualTo(messageModel.getSubject());
		assertThat(fromEntityMessageModel.getAttachment()).as(ATTACHMENT_DOES_NOT_MATCH)
				.isEqualTo(messageModel.getAttachment());
	}
}
