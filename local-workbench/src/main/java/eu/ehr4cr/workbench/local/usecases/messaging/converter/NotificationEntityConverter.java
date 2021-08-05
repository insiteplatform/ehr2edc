package eu.ehr4cr.workbench.local.usecases.messaging.converter;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import eu.ehr4cr.workbench.local.entities.dao.impl.model.Notification;
import eu.ehr4cr.workbench.local.entities.dao.impl.model.NotificationBuilder;
import eu.ehr4cr.workbench.local.entities.enums.NotificationType;
import eu.ehr4cr.workbench.local.entities.enums.Priority;
import eu.ehr4cr.workbench.local.model.security.User;
import eu.ehr4cr.workbench.local.service.IUserMgrService;
import eu.ehr4cr.workbench.local.usecases.messaging.exceptions.NoUserForNotificationException;
import eu.ehr4cr.workbench.local.usecases.messaging.model.NotificationModel;
import eu.ehr4cr.workbench.local.usecases.messaging.model.NotificationModelBuilder;

@Component
public class NotificationEntityConverter implements Converter<NotificationModel, Notification> {

	private IUserMgrService iUserMgrService;

	@Autowired
	public NotificationEntityConverter(IUserMgrService iUserMgrService) {
		this.iUserMgrService = iUserMgrService;
	}

	@Override
	public Notification convert(NotificationModel source) {
		NotificationBuilder builder = NotificationBuilder.builder()
				.type(NotificationType.getNotificationTypeForString(source.getType()))
				.priority(Priority.getPriorityForString(source.getPriority()))
				.expiryDate(source.getExpiryDate())
				.sendingDate(source.getSendingDate())
				.notificationId(source.getId());
		final User sender = iUserMgrService.findUserById(source.getSenderId());
		if (sender != null) {
			builder.sender(sender);
		} else {
			throw new NoUserForNotificationException("sender", source.getSenderId());
		}
		final User recipient = iUserMgrService.findUserById(source.getRecipientId());
		if (recipient != null) {
			builder.recipient(recipient);
		} else {
			throw new NoUserForNotificationException("recipient", source.getRecipientId());
		}
		return builder.build();
	}

	public NotificationModel fromEntity(Notification notification, List<Object> notificationContents) {
		return NotificationModelBuilder.builder()
				.recipientId(notification.getRecipient()
						.getId())
				.senderId(notification.getSender()
						.getId())
				.type(notification.getType()
						.name())
				.sendingDate(notification.getSendingDate())
				.priority(notification.getPriority()
						.name())
				.expiryDate(notification.getExpiryDate())
				.read(notification.isRead())
				.readingDate(notification.getReadingDate())
				.contents(notificationContents)
				.notificationId(notification.getId())
				.build();
	}
}
