package eu.ehr4cr.workbench.local.security.exception;

import org.springframework.security.core.AuthenticationException;

public class WorkbenchAuthenticationException extends AuthenticationException {

	public WorkbenchAuthenticationException(String msg) {
		super(msg);
	}
}
