package eu.ehr4cr.workbench.local.usecases.messaging;

import java.util.List;

import eu.ehr4cr.workbench.local.usecases.messaging.model.MessageModel;

public interface MessageService {

	List<MessageModel> addMessagesToNotifications(List<MessageModel> messageModels);

	MessageModel addNewMessage(MessageModel messageModel);

	void removeMessage(Long messageId);

	List<MessageModel> getMessagesForNotificationId(Long notificationId);
}
