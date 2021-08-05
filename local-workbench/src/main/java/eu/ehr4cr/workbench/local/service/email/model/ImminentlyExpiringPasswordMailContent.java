package eu.ehr4cr.workbench.local.service.email.model;

import java.util.Date;
import java.util.List;

public final class ImminentlyExpiringPasswordMailContent implements MailContent {
	private final Date expirationDate;
	private final String changePasswordUrl;

	private ImminentlyExpiringPasswordMailContent(Builder builder) {
		expirationDate = builder.expirationDate;
		changePasswordUrl = builder.changePasswordUrl;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	@Override
	public Mail createMail(String senderAddress, List<String> recipientAddresses) {
		return new ImminentlyExpiringPasswordMail(senderAddress, recipientAddresses, this);
	}

	public Date getExpirationDate() {
		return expirationDate;
	}

	public String getChangePasswordUrl() {
		return changePasswordUrl;
	}

	public static final class Builder {
		private Date expirationDate;
		private String changePasswordUrl;

		private Builder() {
		}

		public Builder withExpirationDate(Date expirationDate) {
			this.expirationDate = expirationDate;
			return this;
		}

		public Builder withChangePasswordUrl(String changePasswordUrl) {
			this.changePasswordUrl = changePasswordUrl;
			return this;
		}

		public ImminentlyExpiringPasswordMailContent build() {
			return new ImminentlyExpiringPasswordMailContent(this);
		}
	}
}
