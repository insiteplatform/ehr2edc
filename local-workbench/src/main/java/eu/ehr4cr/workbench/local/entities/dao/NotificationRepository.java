package eu.ehr4cr.workbench.local.entities.dao;

import java.util.List;

import eu.ehr4cr.workbench.local.entities.dao.impl.model.Notification;
import eu.ehr4cr.workbench.local.model.security.User;

public interface NotificationRepository {

	List<Notification> getAllNotificationsForUser(User user);

	Notification createNotification(Notification notification);

	void updateNotification(Notification notification);

	void deleteNotification(Long notificationId);

	List<Notification> getAllUnreadNotificationsForUser(User user);

	Notification getNotificationById(Long notificationId);
}
