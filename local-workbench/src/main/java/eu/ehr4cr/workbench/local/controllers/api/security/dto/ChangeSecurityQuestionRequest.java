package eu.ehr4cr.workbench.local.controllers.api.security.dto;

public class ChangeSecurityQuestionRequest {
	private String securityQuestionId;
	private String securityAnswer;

	public String getSecurityQuestionId() {
		return securityQuestionId;
	}

	public void setSecurityQuestionId(String securityQuestionId) {
		this.securityQuestionId = securityQuestionId;
	}

	public String getSecurityAnswer() {
		return securityAnswer;
	}

	public void setSecurityAnswer(String securityAnswer) {
		this.securityAnswer = securityAnswer;
	}
}
