package eu.ehr4cr.workbench.local.entities.dao.impl;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import eu.ehr4cr.workbench.local.dao.jpa.AbstractJpaGenericRepository;
import eu.ehr4cr.workbench.local.entities.dao.MessageRepository;
import eu.ehr4cr.workbench.local.entities.dao.impl.model.Message;
import eu.ehr4cr.workbench.local.entities.exceptions.MessageNotFoundException;

@Repository

class JpaMessageRepository extends AbstractJpaGenericRepository implements MessageRepository {

	private static final String NOTIFICATION_ID = "notification";

	@Override
	public Message getMessageById(Long messageId) {
		return loadMessageById(messageId);
	}

	@Override
	public Message saveMessage(Message message) {
		this.persist(message);
		return message;
	}

	@Override
	public void deleteMessage(Long messageId) {
		Message message = getMessageById(messageId);
		this.remove(message);
	}

	private Message loadMessageById(Long messageId) {
		final Message message = entityManager.find(Message.class, messageId);
		if (message == null) {
			throw new MessageNotFoundException(messageId);
		}
		return message;
	}

	@Override
	public List<Message> getMessagesForNotificationId(Long notificationId) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Message> q = cb.createQuery(Message.class);
		Root<Message> c = q.from(Message.class);
		q.select(c)
				.where(cb.equal(c.get(NOTIFICATION_ID), notificationId));

		return entityManager.createQuery(q)
				.getResultList();
	}
}
