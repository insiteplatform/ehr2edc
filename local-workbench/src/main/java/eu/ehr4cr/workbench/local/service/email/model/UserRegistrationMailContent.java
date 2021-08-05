package eu.ehr4cr.workbench.local.service.email.model;

import java.util.List;

public interface UserRegistrationMailContent extends MailContent {
	String getUsername();

	String getAcceptUrl();

	default Mail createMail(String senderAddress, List<String> recipientAddresses) {
		return new UserRegistrationMail(senderAddress, recipientAddresses, this);
	}
}
