package com.custodix.insite.local.user.infra.rest.model;

import com.custodix.insite.local.user.vocabulary.Password;

public class CompleteInvitationRequest {
	private Long userId;
	private String password;
	private String securityQuestionId;
	private String securityQuestionAnswer;

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

	public String getSecurityQuestionId() {
		return securityQuestionId;
	}

	public void setSecurityQuestionId(String securityQuestionId) {
		this.securityQuestionId = securityQuestionId;
	}

	public String getSecurityQuestionAnswer() {
		return securityQuestionAnswer;
	}

	public void setSecurityQuestionAnswer(String securityQuestionAnswer) {
		this.securityQuestionAnswer = securityQuestionAnswer;
	}
}
