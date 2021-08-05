package eu.ehr4cr.workbench.local.usecases.messaging.model;

public class MessageModel {

	private Long id;
	private String subject;
	private String body;
	private String attachment;
	private Long notificationId;

	protected MessageModel(MessageModelBuilder messageModelBuilder) {
		this.id = messageModelBuilder.messageId;
		this.subject = messageModelBuilder.subject;
		this.body = messageModelBuilder.body;
		this.attachment = messageModelBuilder.attachment;
		this.notificationId = messageModelBuilder.notificationId;
	}

	public Long getId() {
		return id;
	}

	public String getSubject() {
		return subject;
	}

	public String getBody() {
		return body;
	}

	public String getAttachment() {
		return attachment;
	}

	public Long getNotificationId() {
		return notificationId;
	}

}
