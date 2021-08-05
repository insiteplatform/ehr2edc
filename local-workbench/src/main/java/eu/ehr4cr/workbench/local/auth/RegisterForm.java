package eu.ehr4cr.workbench.local.auth;

public class RegisterForm {

	private String username = "";
	private String company = "";
	private String email = "";
	private String errorMsg = "";
	private boolean error = false;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public boolean isError() {
		return error;
	}

	public void setError(boolean error) {
		this.error = error;
	}

	@Override
	public String toString() {
		return "RegisterForm{" + "username=" + username + ", company=" + company + ", email=" + email + ", errorMsg="
				+ errorMsg + ", error=" + error + '}';
	}
}
