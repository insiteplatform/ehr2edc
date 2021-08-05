package eu.ehr4cr.workbench.local.service.email.model;

import javax.mail.MessagingException;

public class MailException extends RuntimeException {
    public MailException(MessagingException e) {
        super(e);
    }
}
