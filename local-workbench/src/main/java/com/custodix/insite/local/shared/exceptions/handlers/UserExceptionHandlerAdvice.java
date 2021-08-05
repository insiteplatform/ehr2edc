package com.custodix.insite.local.shared.exceptions.handlers;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.*;

import com.custodix.insite.local.shared.exceptions.UserException;

@ControllerAdvice(annotations = RestController.class)
@Order(Ordered.HIGHEST_PRECEDENCE)
public class UserExceptionHandlerAdvice {
	@ExceptionHandler(UserException.class)
	@ResponseStatus(BAD_REQUEST)
	@ResponseBody
	String userException(UserException e) {
		return e.getMessage();
	}
}
