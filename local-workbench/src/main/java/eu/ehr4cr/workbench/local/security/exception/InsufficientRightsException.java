package eu.ehr4cr.workbench.local.security.exception;

import org.springframework.security.core.AuthenticationException;

public class InsufficientRightsException extends AuthenticationException {

	public InsufficientRightsException(String msg) {
		super(msg);
	}
}
