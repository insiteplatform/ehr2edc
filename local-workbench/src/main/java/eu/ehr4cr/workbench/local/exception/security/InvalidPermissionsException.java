package eu.ehr4cr.workbench.local.exception.security;

import java.text.MessageFormat;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import eu.ehr4cr.workbench.local.model.security.User;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED,
				reason = "Invalid permissions")
public class InvalidPermissionsException extends RuntimeException {

	private User user;

	public InvalidPermissionsException(User user) {
		super();
		this.user = user;
	}

	public User getUser() {
		return user;
	}

	@Override
	public String getMessage() {
		return MessageFormat.format("User {0} does not have the permissions needed for this call.", user.getUsername());
	}
}
