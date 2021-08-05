package eu.ehr4cr.workbench.local.exception.security;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "User missing")
public class MissingUserException extends RuntimeException {
}
