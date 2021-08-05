package eu.ehr4cr.workbench.local.service.email.model;

import java.util.List;
import java.util.concurrent.TimeUnit;

public interface UserInviteMailContent extends MailContent {
	Integer getInviteExpireValue();

	TimeUnit getInviteExpireUnit();

	String getInviteAcceptUrl();

	default Mail createMail(String senderAddress, List<String> recipientAddresses) {
		return new UserInviteMail(senderAddress, recipientAddresses, this);
	}
}
