package com.custodix.workbench.local.mail;

import eu.ehr4cr.workbench.local.service.email.model.Mail;

interface MailSender {
	void sendMail(Mail mail);
}
