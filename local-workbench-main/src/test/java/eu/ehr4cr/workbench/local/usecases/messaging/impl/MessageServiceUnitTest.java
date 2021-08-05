package eu.ehr4cr.workbench.local.usecases.messaging.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import eu.ehr4cr.workbench.local.entities.dao.MessageRepository;
import eu.ehr4cr.workbench.local.entities.dao.impl.model.Message;
import eu.ehr4cr.workbench.local.entities.dao.impl.model.Notification;
import eu.ehr4cr.workbench.local.usecases.messaging.MessageService;
import eu.ehr4cr.workbench.local.usecases.messaging.UseCaseTestConfig;
import eu.ehr4cr.workbench.local.usecases.messaging.converter.MessageEntityConverter;
import eu.ehr4cr.workbench.local.usecases.messaging.model.MessageModel;

public class MessageServiceUnitTest extends UseCaseTestConfig {

	@Mock
	private MessageRepository messageRepository;

	@Mock
	private MessageEntityConverter messageEntityConverter;

	private MessageService messageService;

	private MessageModel messageModelWithId;
	private Notification notificationWithId;
	private Message messageWithId;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		messageService = new MessageServiceImpl(messageRepository, messageEntityConverter);
		notificationWithId = getNotificationWithId();
		messageWithId = getMessageWithId(notificationWithId);
		messageModelWithId = getNewMessageModel();
		List<Message> messages = new ArrayList<>();
		messages.add(messageWithId);
		when(messageEntityConverter.convert(any(MessageModel.class))).thenReturn(messageWithId);
		when(messageRepository.saveMessage(any(Message.class))).thenReturn(messageWithId);
		when(messageRepository.getMessagesForNotificationId(notificationWithId.getId())).thenReturn(messages);
		when(messageEntityConverter.fromEntity(any(Message.class))).thenReturn(messageModelWithId);
		doNothing().when(messageRepository)
				.deleteMessage(eq(1L));
		List<MessageModel> messagesInNotification = new ArrayList<>();
		messagesInNotification.add(messageModelWithId);
	}

	@Test
	public void testAddMessageToNotification() {
		MessageModel model = messageService.addNewMessage(messageModelWithId);
		assertThat(model).as("Message not added and is null")
				.isNotNull();
		assertThat(model.getId()).as("Message not added, no id present")
				.isNotNull();
	}

	@Test
	public void testAddMessagesToNotification() {
		List<MessageModel> messageModels = new ArrayList<>();
		messageModels.add(messageModelWithId);
		List<MessageModel> models = messageService.addMessagesToNotifications(messageModels);
		assertThat(models).as("List of messages not saved and is null")
				.isNotNull();
		assertThat(models.isEmpty()).as("List of messages not saved and is empty")
				.isFalse();
		assertThat(models.get(0)
				.getId()).as("Saved messageId does not match method parameter passed.")
				.isEqualTo(messageModels.get(0)
						.getId());
	}

	@Test
	public void testRemoveMessage() {
		messageService.removeMessage(1L);
		verify(messageRepository).deleteMessage(1L);
	}

	@Test
	public void testGetAllMessagesForNotification() {
		List<MessageModel> messageModels = messageService.getMessagesForNotificationId(notificationWithId.getId());
		assertThat(messageModels).as("Messages list is null")
				.isNotNull();
		assertThat(!messageModels.isEmpty()).as("No messages found for notification")
				.isTrue();
	}

}
