package eu.ehr4cr.workbench.local.service.email.model;

import java.util.List;

public interface MailContent {
	Mail createMail(String senderAddress, List<String> recipientAddresses);
}
