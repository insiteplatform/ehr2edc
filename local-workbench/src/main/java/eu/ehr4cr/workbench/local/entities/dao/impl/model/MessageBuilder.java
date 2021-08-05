package eu.ehr4cr.workbench.local.entities.dao.impl.model;

public final class MessageBuilder {
	Long messageId;
	String subject = "";
	String body = "";
	String attachment = "";
	Notification notification;

	public static MessageBuilder builder() {
		return new MessageBuilder();
	}

	public MessageBuilder messageId(Long messageId) {
		this.messageId = messageId;
		return this;
	}

	public MessageBuilder subject(String subject) {
		if (subject != null) {
			this.subject = subject;
		}
		return this;
	}

	public MessageBuilder body(String body) {
		if (body != null) {
			this.body = body;
		}
		return this;
	}

	public MessageBuilder attachment(String attachment) {
		if (attachment != null) {
			this.attachment = attachment;
		}
		return this;
	}

	public MessageBuilder notification(Notification notification) {
		if (notification == null) {
			throw new IllegalArgumentException("Notification can not be null");
		}
		this.notification = notification;
		return this;
	}

	public Message build() {
		Message message = new Message(this);
		if (message.getNotification() == null) {
			throw new IllegalStateException("Notification should not be null");
		}
		return message;

	}
}
