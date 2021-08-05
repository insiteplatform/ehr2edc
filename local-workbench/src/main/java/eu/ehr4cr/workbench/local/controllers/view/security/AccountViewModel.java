package eu.ehr4cr.workbench.local.controllers.view.security;

import java.util.Date;
import java.util.List;

import com.custodix.insite.local.user.GetAccount.Response.TreatingPhysicianInfo;

import eu.ehr4cr.workbench.local.model.security.Question;

public final class AccountViewModel {
	private final String email;
	private final String userName;
	private final Date passwordExpiryDate;
	private final String securityQuestionId;
	private final String securityAnswer;
	private final List<Question> securityQuestions;
	private final TreatingPhysicianInfo treatingPhysician;

	private AccountViewModel(Builder builder) {
		email = builder.email;
		userName = builder.userName;
		passwordExpiryDate = builder.passwordExpiryDate;
		securityQuestionId = builder.securityQuestionId;
		securityAnswer = builder.securityAnswer;
		securityQuestions = builder.securityQuestions;
		treatingPhysician = builder.treatingPhysician;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public String getEmail() {
		return email;
	}

	public String getUserName() {
		return userName;
	}

	public Date getPasswordExpiryDate() {
		return passwordExpiryDate;
	}

	public String getSecurityQuestionId() {
		return securityQuestionId;
	}

	public String getSecurityAnswer() {
		return securityAnswer;
	}

	public List<Question> getSecurityQuestions() {
		return securityQuestions;
	}

	public TreatingPhysicianInfo getTreatingPhysician() {
		return treatingPhysician;
	}

	public static final class Builder {
		private String email;
		private String userName;
		private Date passwordExpiryDate;
		private String securityQuestionId;
		private String securityAnswer;
		private List<Question> securityQuestions;
		private TreatingPhysicianInfo treatingPhysician;

		private Builder() {
		}

		public Builder withEmail(String email) {
			this.email = email;
			return this;
		}

		public Builder withUserName(String userName) {
			this.userName = userName;
			return this;
		}

		public Builder withPasswordExpiryDate(Date passwordExpiryDate) {
			this.passwordExpiryDate = passwordExpiryDate;
			return this;
		}

		public Builder withSecurityQuestionId(String securityQuestionId) {
			this.securityQuestionId = securityQuestionId;
			return this;
		}

		public Builder withSecurityAnswer(String securityAnswer) {
			this.securityAnswer = securityAnswer;
			return this;
		}

		public Builder withSecurityQuestions(List<Question> securityQuestions) {
			this.securityQuestions = securityQuestions;
			return this;
		}

		public Builder withTreatingPhysician(TreatingPhysicianInfo treatingPhysician) {
			this.treatingPhysician = treatingPhysician;
			return this;
		}

		public AccountViewModel build() {
			return new AccountViewModel(this);
		}
	}
}
