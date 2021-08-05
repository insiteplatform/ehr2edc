package com.custodix.workbench.local.mail;

import com.custodix.insite.local.user.infra.notifications.ImminentlyExpiringPasswordNotification;
import com.custodix.insite.local.user.infra.notifications.UserMailNotificationService;

import eu.ehr4cr.workbench.local.properties.SupportSettings;
import eu.ehr4cr.workbench.local.service.email.MailService;
import eu.ehr4cr.workbench.local.service.email.model.*;

class MailNotificationServiceImpl implements UserMailNotificationService {
	private final MailService mailService;
	private final SupportSettings supportSettings;

	MailNotificationServiceImpl(MailService mailService, SupportSettings supportSettings) {
		this.mailService = mailService;
		this.supportSettings = supportSettings;
	}

	@Override
	public void sendImminentlyExpiringPassword(ImminentlyExpiringPasswordNotification notification) {
		ImminentlyExpiringPasswordMailContent mailContent = ImminentlyExpiringPasswordMailContent.newBuilder()
				.withExpirationDate(notification.getExpirationDate())
				.withChangePasswordUrl(notification.getChangePasswordUrl())
				.build();
		mailService.sendMail(mailContent, notification.getUserMailAddress());
	}
}
