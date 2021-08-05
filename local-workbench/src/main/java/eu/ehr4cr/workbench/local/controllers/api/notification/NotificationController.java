package eu.ehr4cr.workbench.local.controllers.api.notification;

import eu.ehr4cr.workbench.local.WebRoutes;
import eu.ehr4cr.workbench.local.controllers.BaseController;
import eu.ehr4cr.workbench.local.usecases.messaging.NotificationService;
import eu.ehr4cr.workbench.local.usecases.messaging.model.NotificationModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@Controller
public class NotificationController extends BaseController {

	private final NotificationService notificationService;

	@Autowired
	public NotificationController(NotificationService notificationService) {
		this.notificationService = notificationService;
	}

	@RequestMapping(value = WebRoutes.notificationsReadSingle, method = RequestMethod.POST)
	@ResponseStatus(value = HttpStatus.OK)
	public void postNotificationRead(@PathVariable Long notificationId) {
		notificationService.markNotificationAsRead(notificationId);
	}

	@RequestMapping(value = WebRoutes.notificationsAll, method = RequestMethod.GET, produces = MIME_TYPE_JSON_UTF8)
	public ResponseEntity<List<NotificationModel>> getNotifications() {
		return ResponseEntity.ok(notificationService.getAllNotificationsForUser(this.getUser()));
	}

	@RequestMapping(value = WebRoutes.notificationsUnread, method = RequestMethod.GET, produces = MIME_TYPE_JSON_UTF8)
	public ResponseEntity<List<NotificationModel>> getUnreadNotifications() {
		return ResponseEntity.ok(notificationService.getAllUnreadNotificationsForUser(this.getUser()));
	}
}
