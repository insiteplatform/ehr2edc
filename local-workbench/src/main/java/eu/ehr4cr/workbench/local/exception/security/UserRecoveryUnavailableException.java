package eu.ehr4cr.workbench.local.exception.security;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class UserRecoveryUnavailableException extends RuntimeException {
}
