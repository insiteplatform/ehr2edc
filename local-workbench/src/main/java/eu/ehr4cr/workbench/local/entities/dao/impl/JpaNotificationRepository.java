package eu.ehr4cr.workbench.local.entities.dao.impl;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import eu.ehr4cr.workbench.local.dao.jpa.AbstractJpaGenericRepository;
import eu.ehr4cr.workbench.local.entities.dao.NotificationRepository;
import eu.ehr4cr.workbench.local.entities.dao.impl.model.Notification;
import eu.ehr4cr.workbench.local.entities.exceptions.NotificationNotFoundException;
import eu.ehr4cr.workbench.local.model.security.User;

@Repository
class JpaNotificationRepository extends AbstractJpaGenericRepository implements NotificationRepository {

	private static final String NOTIFICATION_SENDING_DATE_PARAM = "sendingDate";
	private static final String NOTIFICATION_RECIPIENT_PARAM = "recipient";
	private static final String NOTIFICATION_IS_READ_PARAM = "isRead";

	@Override
	public List<Notification> getAllNotificationsForUser(User user) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Notification> q = cb.createQuery(Notification.class);
		Root<Notification> c = q.from(Notification.class);
		q.select(c)
				.where(cb.equal(c.get(NOTIFICATION_RECIPIENT_PARAM), user))
				.orderBy(cb.desc(c.get(NOTIFICATION_SENDING_DATE_PARAM)));

		return entityManager.createQuery(q)
				.getResultList();
	}

	@Override
	public Notification createNotification(Notification notification) {
		this.persist(notification);
		return notification;
	}

	@Override
	public void updateNotification(Notification notification) {
		this.merge(notification);
	}

	@Override
	public void deleteNotification(Long notificationId) {
		Notification notification = loadNotificationById(notificationId);
		this.remove(notification);
	}

	@Override
	public List<Notification> getAllUnreadNotificationsForUser(User user) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Notification> q = cb.createQuery(Notification.class);
		Root<Notification> c = q.from(Notification.class);
		q.select(c)
				.where(cb.and(cb.equal(c.get(NOTIFICATION_RECIPIENT_PARAM), user),
						cb.equal(c.get(NOTIFICATION_IS_READ_PARAM), false)))
				.orderBy(cb.desc(c.get(NOTIFICATION_SENDING_DATE_PARAM)));

		return entityManager.createQuery(q)
				.getResultList();
	}

	@Override
	public Notification getNotificationById(Long notificationId) {
		return loadNotificationById(notificationId);
	}

	private Notification loadNotificationById(Long notificationId) {
		final Notification notification = entityManager.find(Notification.class, notificationId);
		if (notification == null) {
			throw new NotificationNotFoundException(notificationId);
		}
		return notification;
	}
}
