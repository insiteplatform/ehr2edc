package eu.ehr4cr.workbench.local.usecases.user.register;

public class RegisterUserRequest {
	private final String userEmail;
	private final String userName;

	public RegisterUserRequest(String userEmail, String userName) {
		this.userEmail = userEmail;
		this.userName = userName;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public String getUserName() {
		return userName;
	}
}
