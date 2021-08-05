package com.custodix.insite.local.user.infra.rest.model;

import com.custodix.insite.local.user.vocabulary.Password;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UpdatePasswordRequest {
	private final String newPassword;
	private final String oldPassword;

	@JsonCreator
	public UpdatePasswordRequest(@JsonProperty("newPasswd") String newPassword,
			@JsonProperty("oldPasswd") String oldPassword) {
		this.newPassword = newPassword;
		this.oldPassword = oldPassword;
	}

	public Password getNewPassword() {
		return Password.of(newPassword);
	}

	public Password getOldPassword() {
		return Password.of(oldPassword);
	}
}
