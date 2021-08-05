package eu.ehr4cr.workbench.local.usecases.messaging.model;

public final class MessageModelBuilder {

	Long messageId;
	String subject = "";
	String body = "";
	String attachment = "";
	Long notificationId;

	private MessageModelBuilder() {
	}

	public static MessageModelBuilder builder() {
		return new MessageModelBuilder();
	}

	public MessageModelBuilder messageId(Long messageId) {
		this.messageId = messageId;
		return this;
	}

	public MessageModelBuilder subject(String subject) {
		if (subject != null) {
			this.subject = subject;
		}
		return this;
	}

	public MessageModelBuilder body(String body) {
		if (body != null) {
			this.body = body;
		}
		return this;
	}

	public MessageModelBuilder attachment(String attachment) {
		if (attachment != null) {
			this.attachment = attachment;
		}
		return this;
	}

	public MessageModelBuilder notificationId(Long notificationId) {
		if (notificationId == null) {
			throw new IllegalArgumentException("Notification id can not be null");
		}
		this.notificationId = notificationId;
		return this;
	}

	public MessageModel build() {
		return new MessageModel(this);
	}

}
