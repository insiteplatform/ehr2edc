package com.custodix.insite.local.ehr2edc.infra.edc.rave.odm.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.FIELD)
public final class UserRef {
	@XmlAttribute(name = "UserOID")
	private String userOID;

	private UserRef(Builder builder) {
		userOID = builder.userOID;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static final class Builder {
		private String userOID;

		private Builder() {
		}

		public Builder withUserOID(String userOID) {
			this.userOID = userOID;
			return this;
		}

		public UserRef build() {
			return new UserRef(this);
		}
	}
}
