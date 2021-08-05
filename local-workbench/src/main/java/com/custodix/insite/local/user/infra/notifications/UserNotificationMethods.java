package com.custodix.insite.local.user.infra.notifications;

public interface UserNotificationMethods {
	void sendImminentlyExpiringPassword(ImminentlyExpiringPasswordNotification notification);
}
