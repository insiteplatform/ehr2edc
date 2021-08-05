package com.custodix.insite.local.user.infra.notifications;

import java.util.Date;

public final class ImminentlyExpiringPasswordNotification {
	private final String userMailAddress;
	private final Date expirationDate;
	private final String changePasswordUrl;

	private ImminentlyExpiringPasswordNotification(Builder builder) {
		userMailAddress = builder.userMailAddress;
		expirationDate = builder.expirationDate;
		changePasswordUrl = builder.changePasswordUrl;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public String getUserMailAddress() {
		return userMailAddress;
	}

	public Date getExpirationDate() {
		return expirationDate;
	}

	public String getChangePasswordUrl() {
		return changePasswordUrl;
	}

	public static final class Builder {
		private String userMailAddress;
		private Date expirationDate;
		private String changePasswordUrl;

		private Builder() {
		}

		public Builder withUserMailAddress(String userMailAddress) {
			this.userMailAddress = userMailAddress;
			return this;
		}

		public Builder withExpirationDate(Date expirationDate) {
			this.expirationDate = expirationDate;
			return this;
		}

		public Builder withChangePasswordUrl(String changePasswordUrl) {
			this.changePasswordUrl = changePasswordUrl;
			return this;
		}

		public ImminentlyExpiringPasswordNotification build() {
			return new ImminentlyExpiringPasswordNotification(this);
		}
	}
}
