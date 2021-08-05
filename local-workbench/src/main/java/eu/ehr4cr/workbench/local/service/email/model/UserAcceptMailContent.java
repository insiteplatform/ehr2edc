package eu.ehr4cr.workbench.local.service.email.model;

import java.util.List;
import java.util.concurrent.TimeUnit;

public interface UserAcceptMailContent extends MailContent {
	Integer getAcceptExpireValue();

	TimeUnit getAcceptExpireUnit();

	String getAcceptAcceptUrl();

	default Mail createMail(String senderAddress, List<String> recipientAddresses) {
		return new UserAcceptMail(senderAddress, recipientAddresses, this);
	}
}
