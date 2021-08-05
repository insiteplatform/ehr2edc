package eu.ehr4cr.workbench.local.entities.dao.impl.model;

import java.util.Date;

import javax.persistence.*;

import eu.ehr4cr.workbench.local.entities.enums.NotificationType;
import eu.ehr4cr.workbench.local.entities.enums.Priority;
import eu.ehr4cr.workbench.local.model.security.User;

@Entity
@Table(name = "notification")
public class Notification {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	@ManyToOne
	private User sender;
	@ManyToOne
	private User recipient;

	@Column(name = "sending_date")
	private Date sendingDate;
	@Column(name = "reading_date")
	private Date readingDate;
	@Column(name = "expiry_date")
	private Date expiryDate;

	@Column(name = "priority")
	@Enumerated(EnumType.STRING)
	private Priority priority;

	@Column(name = "type")
	@Enumerated(EnumType.STRING)
	private NotificationType type;

	@Column(name = "is_read")
	private boolean isRead;

	protected Notification() {
		// Needed by JPA, either no args, or multi args constructor
	}

	protected Notification(NotificationBuilder notificationBuilder) {
		this.id = notificationBuilder.notificationId;
		this.sender = notificationBuilder.sender;
		this.recipient = notificationBuilder.recipient;
		this.sendingDate = notificationBuilder.sendingDate;
		this.readingDate = notificationBuilder.readingDate;
		this.expiryDate = notificationBuilder.expiryDate;
		this.priority = notificationBuilder.priority;
		this.type = notificationBuilder.type;
		this.isRead = notificationBuilder.isRead;
	}

	public static NotificationBuilder builder() {
		return new NotificationBuilder();
	}

	public Long getId() {
		return id;
	}

	public User getSender() {
		return sender;
	}

	public User getRecipient() {
		return recipient;
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

	public Priority getPriority() {
		return priority;
	}

	public NotificationType getType() {
		return type;
	}

	public boolean isRead() {
		return isRead;
	}

	public void readMessage() {
		this.isRead = true;
		this.readingDate = new Date();
	}
}