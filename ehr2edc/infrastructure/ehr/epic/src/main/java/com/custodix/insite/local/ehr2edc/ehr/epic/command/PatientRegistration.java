package com.custodix.insite.local.ehr2edc.ehr.epic.command;

import static com.custodix.insite.local.ehr2edc.shared.annotations.security.Allow.Rule.ANYONE;

import java.time.LocalDate;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.custodix.insite.local.ehr2edc.ehr.epic.vocabulary.OAuthAccessToken;
import com.custodix.insite.local.ehr2edc.shared.annotations.security.Allow;

public interface PatientRegistration {

	@Allow(ANYONE)
	void register(@Valid @NotNull Request request);


	class Request {
		@NotBlank
		private String firstName;
		@NotBlank
		private String lastName;
		@NotBlank
		private LocalDate birthDate;
		@NotNull
		private OAuthAccessToken oAuthAccessToken;

		private Request(Builder builder) {
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

		public OAuthAccessToken getoAuthAccessToken() {
			return oAuthAccessToken;
		}

		public static Builder newBuilder() {
			return new Builder();
		}

		public static final class Builder {
			private String firstName;
			private String lastName;
			private LocalDate birthDate;
			private OAuthAccessToken oAuthAccessToken;

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

			public Builder withOAuthAccessToken(OAuthAccessToken val) {
				oAuthAccessToken = val;
				return this;
			}

			public Request build() {
				return new Request(this);
			}
		}
	}
}
