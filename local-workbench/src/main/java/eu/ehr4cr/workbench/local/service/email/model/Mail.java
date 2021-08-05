package eu.ehr4cr.workbench.local.service.email.model;

import org.thymeleaf.context.Context;

import java.util.List;

public abstract class Mail {

	private final String senderAddress;
	private final List<String> recipientAddresses;

	protected Mail(String senderAddress, List<String> recipientAddresses) {
		this.senderAddress = senderAddress;
		this.recipientAddresses = recipientAddresses;
	}

	public String getSenderAddress() {
		return senderAddress;
	}

	public List<String> getRecipientAddresses() {
		return recipientAddresses;
	}

	public abstract String getSubject();

	public abstract Context getContext();

}
