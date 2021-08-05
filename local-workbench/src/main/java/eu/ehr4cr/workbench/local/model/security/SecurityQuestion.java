package eu.ehr4cr.workbench.local.model.security;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
class SecurityQuestion implements Serializable {
	@Column(name = "securityQuestionId")
	private String securityQuestionId;
	@Column(name = "securityAnswer")
	private String securityAnswer;

	SecurityQuestion() {
		// JPA
	}

	SecurityQuestion(String securityQuestionId, String securityAnswer) {
		this.securityQuestionId = securityQuestionId;
		this.securityAnswer = securityAnswer;
	}

	public String getSecurityQuestionId() {
		return securityQuestionId;
	}

	public String getSecurityAnswer() {
		return securityAnswer;
	}

	public boolean matches(String answer) {
		return securityAnswer.equalsIgnoreCase(answer);
	}
}
