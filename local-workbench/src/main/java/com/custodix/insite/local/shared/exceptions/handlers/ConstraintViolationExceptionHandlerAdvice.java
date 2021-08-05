package com.custodix.insite.local.shared.exceptions.handlers;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.*;

@ControllerAdvice(annotations = RestController.class)
@Order(Ordered.HIGHEST_PRECEDENCE)
class ConstraintViolationExceptionHandlerAdvice {
	@ExceptionHandler(ConstraintViolationException.class)
	@ResponseStatus(BAD_REQUEST)
	@ResponseBody
	String constraintViolationException(ConstraintViolationException e) {
		return extractConstraintViolationErrorMessage(e);
	}

	private String extractConstraintViolationErrorMessage(ConstraintViolationException exception) {
		Set<ConstraintViolation<?>> constraintViolations = exception.getConstraintViolations();
		return constraintViolations.stream()
				.map(this::getMessage)
				.collect(Collectors.joining("\n"));
	}

	private String getMessage(ConstraintViolation violation) {
		String message = violation.getMessage();
		if (isAttributeKeyIncludedInMessage(message)) {
			return message;
		} else {
			return prependMessageWithAttributeKey(violation, message);
		}
	}

	private boolean isAttributeKeyIncludedInMessage(String message) {
		return message.contains("Password");
	}

	private String prependMessageWithAttributeKey(ConstraintViolation violation, String message) {
		String attributeKey = getAttributeKey(violation);
		return attributeKey + " " + message;
	}

	private String getAttributeKey(ConstraintViolation violation) {
		String key = ((PathImpl) violation.getPropertyPath()).getLeafNode()
				.getName();
		String spacedKey = convertCamelCaseToSpacedWords(key);
		return StringUtils.capitalize(spacedKey.toLowerCase());
	}

	private String convertCamelCaseToSpacedWords(String key) {
		return key.replaceAll("([A-Z][a-z])", " $1");
	}
}
