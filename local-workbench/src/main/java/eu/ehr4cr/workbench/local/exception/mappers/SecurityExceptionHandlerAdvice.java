package eu.ehr4cr.workbench.local.exception.mappers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import eu.ehr4cr.workbench.local.exception.security.InvitedUserExistsException;
import eu.ehr4cr.workbench.local.exception.security.TempPasswordMismatchException;

@ControllerAdvice
@Order(SecurityExceptionHandlerAdvice.SECOND_HIGHEST_PRECEDENCE)
//TODO:  EHR2EDC-433
public class SecurityExceptionHandlerAdvice {
	private static final Logger LOGGER = LoggerFactory.getLogger(SecurityExceptionHandlerAdvice.class);
	static final int SECOND_HIGHEST_PRECEDENCE = Ordered.HIGHEST_PRECEDENCE + 1;

	@ExceptionHandler(InvitedUserExistsException.class)
	@ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
	@ResponseBody
	public String invitedUserExistsException() {
		return "Such a user already exists";
	}

	@ExceptionHandler(TempPasswordMismatchException.class)
	@ResponseStatus(HttpStatus.FORBIDDEN)
	public void tempPasswordMismatchException() {
		LOGGER.error("Attempting to accept an expired user");
	}

	@ExceptionHandler(AccessDeniedException.class)
	@ResponseStatus(HttpStatus.FORBIDDEN)
	public void accessDeniedException() {
	}
}
