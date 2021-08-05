package com.custodix.insite.local.ehr2edc.ehr.epic.domain.event;

import java.time.LocalDate;

public class EpicPortalPatientRegistered implements AsyncSerializationSafeDomainEvent {
	private final String firstName;
	private final String lastName;
	private final LocalDate birthDate;
	private final String oAuthAccessToken;

	private EpicPortalPatientRegistered(Builder builder) {
		firstName = builder.firstName;
		lastName = builder.lastName;
		birthDate = builder.birthDate;
		oAuthAccessToken = builder.oAuthAccessToken;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public LocalDate getBirthDate() {
		return birthDate;
	}

	public String getOAuthAccessToken() {
		return oAuthAccessToken;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static final class Builder {
		private String firstName;
		private String lastName;
		private LocalDate birthDate;
		private String oAuthAccessToken;

		private Builder() {
		}

		public Builder withFirstName(String val) {
			firstName = val;
			return this;
		}

		public Builder withLastName(String val) {
			lastName = val;
			return this;
		}

		public Builder withBirthDate(LocalDate val) {
			birthDate = val;
			return this;
		}

		public Builder withOAuthAccessToken(String val) {
			oAuthAccessToken = val;
			return this;
		}

		public EpicPortalPatientRegistered build() {
			return new EpicPortalPatientRegistered(this);
		}
	}
}
