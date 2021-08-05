package eu.ehr4cr.workbench.local.usecases.messaging.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NotificationModel {

	private Long id;
	private Long senderId;
	private Long recipientId;
	private Date sendingDate;
	private Date readingDate;
	private Date expiryDate;

	private String priority;
	private String type;
	private List<Object> contents;
	private boolean isRead;

	protected NotificationModel(NotificationModelBuilder notificationModelBuilder) {
		this.id = notificationModelBuilder.notificationId;
		this.senderId = notificationModelBuilder.senderId;
		this.recipientId = notificationModelBuilder.recipientId;
		this.sendingDate = notificationModelBuilder.sendingDate;
		this.readingDate = notificationModelBuilder.readingDate;
		this.expiryDate = notificationModelBuilder.expiryDate;
		this.priority = notificationModelBuilder.priority;
		this.type = notificationModelBuilder.type;
		this.isRead = notificationModelBuilder.isRead;
		this.contents = notificationModelBuilder.contents;
	}

	public Long getId() {
		return id;
	}

	public Long getSenderId() {
		return senderId;
	}

	public Long getRecipientId() {
		return recipientId;
	}

	public Date getSendingDate() {
		return sendingDate;
	}

	public Date getReadingDate() {
		return readingDate;
	}

	public Date getExpiryDate() {
		return expiryDate;
	}

	public String getPriority() {
		return priority;
	}

	public String getType() {
		return type;
	}

	public boolean isRead() {
		return isRead;
	}

	public List<Object> getContents() {
		return contents;
	}

	public void addContentToContents(Object content) {
		if (this.contents == null) {
			contents = new ArrayList<>();
		}
		contents.add(content);
	}
}
