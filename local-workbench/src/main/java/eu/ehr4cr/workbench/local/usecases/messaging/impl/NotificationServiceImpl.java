package eu.ehr4cr.workbench.local.usecases.messaging.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.ehr4cr.workbench.local.entities.dao.NotificationRepository;
import eu.ehr4cr.workbench.local.entities.dao.impl.model.Notification;
import eu.ehr4cr.workbench.local.entities.enums.NotificationType;
import eu.ehr4cr.workbench.local.model.security.User;
import eu.ehr4cr.workbench.local.usecases.messaging.MessageService;
import eu.ehr4cr.workbench.local.usecases.messaging.NotificationService;
import eu.ehr4cr.workbench.local.usecases.messaging.converter.NotificationEntityConverter;
import eu.ehr4cr.workbench.local.usecases.messaging.model.MessageModel;
import eu.ehr4cr.workbench.local.usecases.messaging.model.MessageModelBuilder;
import eu.ehr4cr.workbench.local.usecases.messaging.model.NotificationModel;

@Service
class NotificationServiceImpl implements NotificationService {

	private final NotificationRepository notificationRepository;
	private final MessageService messageService;
	private final NotificationEntityConverter notificationEntityConverter;

	@Autowired
	public NotificationServiceImpl(NotificationRepository notificationRepository, MessageService messageService,
			NotificationEntityConverter notificationEntityConverter) {
		this.notificationRepository = notificationRepository;
		this.messageService = messageService;
		this.notificationEntityConverter = notificationEntityConverter;
	}

	@Override
	public NotificationModel createNewNotification(NotificationModel notificationModel) {
		final Notification notification = notificationRepository
				.createNotification(notificationEntityConverter.convert(notificationModel));
		List<Object> contents = persistNotificationContents(notificationModel.getContents(), notification.getType(),
				notification.getId());
		return notificationEntityConverter.fromEntity(notification, contents);
	}

	@Override
	public void markNotificationAsRead(Long notificationId) {
		Notification notification = notificationRepository.getNotificationById(notificationId);
		notification.readMessage();
		notificationRepository.updateNotification(notification);
	}

	@Override
	public void deleteNotification(Long notificationId) {
		notificationRepository.deleteNotification(notificationId);
	}

	@Override
	public List<NotificationModel> getAllNotificationsForUser(User user) {
		return populateNotificationResponseListFromEntities(notificationRepository.getAllNotificationsForUser(user));
	}

	private List<NotificationModel> populateNotificationResponseListFromEntities(List<Notification> notifications) {
		final List<NotificationModel> notificationModels = new ArrayList<>();
		notifications.forEach(notification -> notificationModels.add(convertNotificationEntityToModel(notification)));
		return notificationModels;
	}

	@Override
	public List<NotificationModel> getAllUnreadNotificationsForUser(User user) {
		return populateNotificationResponseListFromEntities(
				notificationRepository.getAllUnreadNotificationsForUser(user));
	}

	private List<Object> persistNotificationContents(List<Object> contents, NotificationType type,
			Long notificationId) {
		if (type.equals(NotificationType.MESSAGE)) {
			return persistMessages(contents, notificationId);
		}
		return new ArrayList<>();
	}

	private List<Object> persistMessages(List<Object> contents, Long notificationId) {
		List<Object> messages = new ArrayList<>();
		for (Object content : contents) {
			final MessageModel messageModel = (MessageModel) content;
			MessageModelBuilder messageWithId = MessageModelBuilder.builder()
					.notificationId(notificationId)
					.attachment(messageModel.getAttachment())
					.subject(messageModel.getSubject())
					.body(messageModel.getBody());
			messages.add(messageService.addNewMessage(messageWithId.build()));
		}
		return messages;
	}

	private NotificationModel convertNotificationEntityToModel(Notification notification) {
		final List<MessageModel> messageModels = messageService.getMessagesForNotificationId(notification.getId());
		return notificationEntityConverter.fromEntity(notification, new ArrayList<>(messageModels));
	}
}
