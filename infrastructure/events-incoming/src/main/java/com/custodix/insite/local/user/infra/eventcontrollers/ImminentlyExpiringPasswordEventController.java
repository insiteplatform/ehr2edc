package com.custodix.insite.local.user.infra.eventcontrollers;

import org.springframework.stereotype.Component;

import com.custodix.insite.local.user.domain.events.ImminentlyExpiringPasswordEvent;
import com.custodix.insite.local.user.infra.notifications.ImminentlyExpiringPasswordNotification;
import com.custodix.insite.local.user.infra.notifications.UserMailNotificationService;

import eu.ehr4cr.workbench.local.WebRoutes;
import eu.ehr4cr.workbench.local.properties.PropertyProvider;

@Component
class ImminentlyExpiringPasswordEventController {
	private final UserMailNotificationService userMailNotificationService;
	private final PropertyProvider propertyProvider;

	ImminentlyExpiringPasswordEventController(UserMailNotificationService userMailNotificationService,
			PropertyProvider propertyProvider) {
		this.userMailNotificationService = userMailNotificationService;
		this.propertyProvider = propertyProvider;
	}

	void handle(ImminentlyExpiringPasswordEvent event) {
		ImminentlyExpiringPasswordNotification notification = ImminentlyExpiringPasswordNotification.newBuilder()
				.withUserMailAddress(event.getUserMailAddress())
				.withExpirationDate(event.getExpirationDate())
				.withChangePasswordUrl(propertyProvider.getBaseUrl() + WebRoutes.MY_ACCOUNT_PASSWORD)
				.build();
		userMailNotificationService.sendImminentlyExpiringPassword(notification);
	}
}
