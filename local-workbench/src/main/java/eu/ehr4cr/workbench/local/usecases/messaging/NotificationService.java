package eu.ehr4cr.workbench.local.usecases.messaging;

import java.util.List;

import eu.ehr4cr.workbench.local.model.security.User;
import eu.ehr4cr.workbench.local.usecases.messaging.model.NotificationModel;

public interface NotificationService {
	NotificationModel createNewNotification(NotificationModel notificationModel);

	void markNotificationAsRead(Long notificationId);

	void deleteNotification(Long notificationId);

	List<NotificationModel> getAllNotificationsForUser(User user);

	List<NotificationModel> getAllUnreadNotificationsForUser(User user);

}
