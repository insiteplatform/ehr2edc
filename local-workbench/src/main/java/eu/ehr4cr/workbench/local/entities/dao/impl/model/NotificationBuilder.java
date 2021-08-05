package eu.ehr4cr.workbench.local.entities.dao.impl.model;

import java.util.Date;

import eu.ehr4cr.workbench.local.entities.enums.NotificationType;
import eu.ehr4cr.workbench.local.entities.enums.Priority;
import eu.ehr4cr.workbench.local.model.security.User;

public final class NotificationBuilder {
	Long notificationId;
	User sender;
	User recipient;
	Date sendingDate;
	Date readingDate;
	Date expiryDate;
	Priority priority;
	NotificationType type;
	boolean isRead;

	public static NotificationBuilder builder() {
		return new NotificationBuilder();
	}

	public NotificationBuilder notificationId(Long notificationId) {
		this.notificationId = notificationId;
		return this;
	}

	public NotificationBuilder sender(User sender) {
		if (sender == null) {
			throw new IllegalArgumentException("The sender may not be null");
		}
		this.sender = sender;
		return this;
	}

	public NotificationBuilder recipient(User recipient) {
		if (recipient == null) {
			throw new IllegalArgumentException("The recipient may not be null");
		}
		this.recipient = recipient;
		return this;
	}

	public NotificationBuilder sendingDate(Date sendingDate) {
		if (sendingDate == null) {
			throw new IllegalArgumentException("The sending date may not be null");
		}
		this.sendingDate = sendingDate;
		return this;
	}

	public NotificationBuilder expiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
		return this;
	}

	public NotificationBuilder priority(Priority priority) {
		if (priority == null) {
			throw new IllegalArgumentException("Priority date can not be null");
		}
		this.priority = priority;
		return this;
	}

	public NotificationBuilder type(NotificationType type) {
		if (type == null) {
			throw new IllegalArgumentException("Notification type can not be null");
		}
		this.type = type;
		return this;
	}

	public Notification build() {
		Notification notification = new Notification(this);
		if (notification.getSender() == null) {
			throw new IllegalStateException("Sender should not be null");
		}
		if (notification.getRecipient() == null) {
			throw new IllegalStateException("Recipient should not be null");
		}
		if (notification.getType() == null) {
			throw new IllegalStateException("Type should not be null");
		}
		if (notification.getPriority() == null) {
			throw new IllegalStateException("Priority should not be null");
		}
		if (notification.getSendingDate() == null) {
			throw new IllegalStateException("Sending date should not be null");
		}
		return notification;
	}
}
