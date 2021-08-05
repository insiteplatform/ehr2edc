package eu.ehr4cr.workbench.local.service.email.model;

import java.util.List;
import java.util.concurrent.TimeUnit;

public interface UserRecoverMailContent extends MailContent {
	Integer getRecoverExpireValue();

	TimeUnit getRecoverExpireUnit();

	String getRecoverAcceptUrl();

	default Mail createMail(String senderAddress, List<String> recipientAddresses) {
		return new UserRecoverMail(senderAddress, recipientAddresses, this);
	}
}
