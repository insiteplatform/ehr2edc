package com.custodix.insite.local.user.infra.notifications;

import java.util.LinkedList;
import java.util.Queue;

public class TestUserMailNotificationService implements UserMailNotificationService {
	private final Queue<Object> notifications;

	public TestUserMailNotificationService() {
		this.notifications = new LinkedList<>();
	}

	public boolean isEmpty() {
		return notifications.isEmpty();
	}

	public Object poll() {
		return notifications.poll();
	}

	@Override
	public void sendImminentlyExpiringPassword(ImminentlyExpiringPasswordNotification notification) {
		notifications.add(notification);
	}
}
