package eu.ehr4cr.workbench.local.service.email.model;

import java.util.List;

public interface ClinicalStudyInvitationMailContent extends MailContent {
	String getParticipationUrl();

	default Mail createMail(String senderAddress, List<String> recipientAddresses) {
		return new ClinicalStudyInvitationMail(senderAddress, recipientAddresses, this);
	}
}
