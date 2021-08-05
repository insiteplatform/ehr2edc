package eu.ehr4cr.workbench.local.service.email;

import java.util.LinkedList;
import java.util.Queue;

import eu.ehr4cr.workbench.local.global.GroupType;
import eu.ehr4cr.workbench.local.service.email.model.MailContent;

public class TestMailService implements MailService {
	private final Queue<Mail> mails;

	public TestMailService() {
		mails = new LinkedList<>();
	}

	@Override
	public void sendMail(MailContent content, String recipientAddress) {
		mails.add(new RecipientMail(content, recipientAddress));
	}

	@Override
	public void sendMail(MailContent content, GroupType recipientType) {
		mails.add(new GroupMail(content, recipientType));
	}

	public Mail poll() {
		return mails.poll();
	}

	public boolean isEmpty() {
		return mails.isEmpty();
	}

	public void clear() {
		mails.clear();
	}

	static abstract class Mail {
		private final MailContent content;

		Mail(MailContent content) {
			this.content = content;
		}

		public MailContent getContent() {
			return content;
		}
	}

	public static class RecipientMail extends Mail {
		private final String recipient;

		RecipientMail(MailContent mailContent, String recipient) {
			super(mailContent);
			this.recipient = recipient;
		}

		public String getRecipient() {
			return recipient;
		}
	}

	public static class GroupMail extends Mail {
		private final GroupType group;

		GroupMail(MailContent mailContent, GroupType group) {
			super(mailContent);
			this.group = group;
		}

		public GroupType getGroup() {
			return group;
		}
	}
}
