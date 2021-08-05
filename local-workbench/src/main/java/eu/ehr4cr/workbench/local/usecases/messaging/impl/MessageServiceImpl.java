package eu.ehr4cr.workbench.local.usecases.messaging.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.ehr4cr.workbench.local.entities.dao.MessageRepository;
import eu.ehr4cr.workbench.local.entities.dao.impl.model.Message;
import eu.ehr4cr.workbench.local.usecases.messaging.MessageService;
import eu.ehr4cr.workbench.local.usecases.messaging.converter.MessageEntityConverter;
import eu.ehr4cr.workbench.local.usecases.messaging.exceptions.MessagesListIsNullException;
import eu.ehr4cr.workbench.local.usecases.messaging.model.MessageModel;

@Service
class MessageServiceImpl implements MessageService {

	private final MessageRepository messageRepository;
	private final MessageEntityConverter messageEntityConverter;

	@Autowired
	public MessageServiceImpl(MessageRepository messageRepository, MessageEntityConverter messageEntityConverter) {
		this.messageRepository = messageRepository;
		this.messageEntityConverter = messageEntityConverter;
	}

	@Override
	public MessageModel addNewMessage(MessageModel messageModel) {
		return createMessage(messageModel);
	}

	@Override
	public List<MessageModel> addMessagesToNotifications(List<MessageModel> messageModels) {
		if (messageModels != null) {
			return messageModels.stream()
					.map(this::addNewMessage)
					.collect(Collectors.toList());
		} else {
			throw new MessagesListIsNullException();
		}
	}

	private MessageModel createMessage(MessageModel messageModel) {
		Message messageEntity = messageEntityConverter.convert(messageModel);
		return messageEntityConverter.fromEntity(messageRepository.saveMessage(messageEntity));
	}

	@Override
	public void removeMessage(Long messageId) {
		messageRepository.deleteMessage(messageId);
	}

	@Override
	public List<MessageModel> getMessagesForNotificationId(Long notificationId) {
		final List<MessageModel> messageModels = new ArrayList<>();
		final List<Message> messages = messageRepository.getMessagesForNotificationId(notificationId);
		messages.forEach(message -> messageModels.add(messageEntityConverter.fromEntity(message)));
		return messageModels;
	}
}
