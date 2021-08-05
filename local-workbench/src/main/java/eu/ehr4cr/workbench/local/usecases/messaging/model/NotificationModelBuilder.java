package eu.ehr4cr.workbench.local.usecases.messaging.model;

import java.util.Date;
import java.util.List;

public final class NotificationModelBuilder {

	Long notificationId;
	Long senderId;
	Long recipientId;
	Date sendingDate;
	Date readingDate;
	Date expiryDate;

	String priority;
	String type;
	List<Object> contents;
	boolean isRead;

	public static NotificationModelBuilder builder() {
		return new NotificationModelBuilder();
	}

	private NotificationModelBuilder() {
	}

	public NotificationModelBuilder notificationId(Long notificationId) {
		this.notificationId = notificationId;
		return this;
	}

	public NotificationModelBuilder senderId(Long senderId) {
		if (senderId == null) {
			throw new IllegalArgumentException("The sender id may not be null");
		}
		this.senderId = senderId;
		return this;
	}

	public NotificationModelBuilder recipientId(Long recipientId) {
		if (recipientId == null) {
			throw new IllegalArgumentException("The recipient id may not be null");
		}
		this.recipientId = recipientId;
		return this;
	}

	public NotificationModelBuilder sendingDate(Date sendingDate) {
		if (sendingDate == null) {
			throw new IllegalArgumentException("The sending date may not be null");
		}
		this.sendingDate = sendingDate;
		return this;
	}

	public NotificationModelBuilder readingDate(Date readingDate) {
		this.readingDate = readingDate;
		return this;
	}

	public NotificationModelBuilder expiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
		return this;
	}

	public NotificationModelBuilder priority(String priority) {
		if (priority == null) {
			throw new IllegalArgumentException("Priority date can not be null");
		}
		this.priority = priority;
		return this;
	}

	public NotificationModelBuilder type(String type) {
		if (type == null) {
			throw new IllegalArgumentException("Notification type can not be null");
		}
		this.type = type;
		return this;
	}

	public NotificationModelBuilder contents(List<Object> contents) {
		this.contents = contents;
		return this;
	}

	public NotificationModelBuilder read(boolean isRead) {
		this.isRead = isRead;
		return this;
	}

	public NotificationModel build() {

		if (this.senderId == null) {
			throw new IllegalStateException("Sender id should not be null");
		}
		if (this.recipientId == null) {
			throw new IllegalStateException("Recipient id should not be null");
		}
		if (this.type == null) {
			throw new IllegalStateException("Type should not be null");
		}
		if (this.priority == null) {
			throw new IllegalStateException("Priority should not be null");
		}
		if (this.sendingDate == null) {
			throw new IllegalStateException("Sending date should not be null");
		}
		NotificationModel notificationModel = new NotificationModel(this);
		return notificationModel;
	}
}
