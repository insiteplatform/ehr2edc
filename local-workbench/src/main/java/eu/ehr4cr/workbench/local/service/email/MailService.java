package eu.ehr4cr.workbench.local.service.email;

import eu.ehr4cr.workbench.local.global.GroupType;
import eu.ehr4cr.workbench.local.service.email.model.*;

public interface MailService {

	void sendMail(MailContent content, String recipientAddress);

	void sendMail(MailContent content, GroupType recipientType);

}
