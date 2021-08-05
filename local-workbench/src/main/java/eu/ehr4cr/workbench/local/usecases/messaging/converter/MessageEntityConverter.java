package eu.ehr4cr.workbench.local.usecases.messaging.converter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import eu.ehr4cr.workbench.local.entities.dao.NotificationRepository;
import eu.ehr4cr.workbench.local.entities.dao.impl.model.Message;
import eu.ehr4cr.workbench.local.entities.dao.impl.model.MessageBuilder;
import eu.ehr4cr.workbench.local.usecases.messaging.exceptions.MessageEntityNullException;
import eu.ehr4cr.workbench.local.usecases.messaging.exceptions.MessageModelNullException;
import eu.ehr4cr.workbench.local.usecases.messaging.model.MessageModel;
import eu.ehr4cr.workbench.local.usecases.messaging.model.MessageModelBuilder;

@Component
public class MessageEntityConverter implements Converter<MessageModel, Message> {

	private NotificationRepository notificationRepository;

	@Autowired
	public MessageEntityConverter(NotificationRepository notificationRepository) {
		this.notificationRepository = notificationRepository;
	}

	@Override
	public Message convert(MessageModel source) {
		if (source != null) {
			return MessageBuilder.builder()
					.messageId(source.getId())
					.body(source.getBody())
					.subject(source.getSubject())
					.attachment(source.getAttachment())
					.notification(notificationRepository.getNotificationById(source.getNotificationId()))
					.build();
		} else {
			throw new MessageModelNullException();
		}
	}

	public MessageModel fromEntity(Message message) {
		if (message != null) {
			return MessageModelBuilder.builder()
					.body(message.getBody())
					.messageId(message.getId())
					.attachment(message.getAttachment())
					.notificationId(message.getNotification()
							.getId())
					.subject(message.getSubject())
					.build();
		} else {
			throw new MessageEntityNullException();

		}

	}
}
