package eu.ehr4cr.workbench.local.exception.mappers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import eu.ehr4cr.workbench.local.exception.security.InvitedUserExistsException;
import eu.ehr4cr.workbench.local.exception.security.TempPasswordMismatchException;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RestSecurityExceptionHandlerAdvice {
	private static final Logger LOGGER = LoggerFactory.getLogger(RestSecurityExceptionHandlerAdvice.class);

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


}
