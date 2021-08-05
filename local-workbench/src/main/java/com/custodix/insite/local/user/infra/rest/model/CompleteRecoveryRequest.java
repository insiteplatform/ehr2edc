package com.custodix.insite.local.user.infra.rest.model;

import com.custodix.insite.local.user.vocabulary.Password;

public class CompleteRecoveryRequest {
	private Long userId;
	private String password;

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Password getPassword() {
		return Password.of(password);
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
