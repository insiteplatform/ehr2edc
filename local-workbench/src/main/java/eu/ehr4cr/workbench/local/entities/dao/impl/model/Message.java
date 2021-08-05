package eu.ehr4cr.workbench.local.entities.dao.impl.model;

import javax.persistence.*;

@Entity
@Table(name = "message")
public class Message {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private String subject;
	@Lob
	private String body;

	@Lob
	private String attachment;

	@ManyToOne
	@JoinColumn(name = "notification_id")
	private Notification notification;

	protected Message() {
		// Needed by JPA, either no args, or multi args constructor
	}

	protected Message(MessageBuilder messageBuilder) {
		this.id = messageBuilder.messageId;
		this.subject = messageBuilder.subject;
		this.body = messageBuilder.body;
		this.attachment = messageBuilder.attachment;
		this.notification = messageBuilder.notification;
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

	public Notification getNotification() {
		return notification;
	}
}
