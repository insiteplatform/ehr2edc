package eu.ehr4cr.workbench.local.entities.dao;

import java.util.List;

import eu.ehr4cr.workbench.local.entities.dao.impl.model.Message;

public interface MessageRepository {
	Message getMessageById(Long messageId);

	Message saveMessage(Message message);

	void deleteMessage(Long messageId);

	List<Message> getMessagesForNotificationId(Long notificationId);
}
