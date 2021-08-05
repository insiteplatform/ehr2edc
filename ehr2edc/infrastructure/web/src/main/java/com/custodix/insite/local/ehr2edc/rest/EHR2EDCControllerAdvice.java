package com.custodix.insite.local.ehr2edc.rest;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

import java.util.Collections;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.custodix.insite.local.ehr2edc.rest.models.outcome.Issue;
import com.custodix.insite.local.ehr2edc.rest.models.outcome.OperationOutcome;
import com.custodix.insite.local.ehr2edc.shared.exceptions.*;

@ControllerAdvice("com.custodix.insite.local.ehr2edc.rest")
@Order(Ordered.HIGHEST_PRECEDENCE)
final class EHR2EDCControllerAdvice {

	private static final Logger LOGGER = LoggerFactory.getLogger(EHR2EDCControllerAdvice.class);
	private static final String GENERIC_ERROR_MESSAGE = "An unexpected error occurred!";

	private EHR2EDCControllerAdvice() {
	}

	@ExceptionHandler(DomainException.class)
	public ResponseEntity<OperationOutcome> handleDomainException(DomainException domainException) {
		return ResponseEntity.badRequest()
				.contentType(MediaType.APPLICATION_JSON)
				.body(createErrorResponse(convertUserException(domainException)));
	}

	private UserException convertUserException(DomainException domainException) {
		return new UserException(domainException.getMessage());
	}

	@ExceptionHandler(UseCaseConstraintViolationException.class)
	public ResponseEntity<OperationOutcome> handleUseCaseConstraintViolationException(
			UseCaseConstraintViolationException useCaseConstraintViolationException) {
		return ResponseEntity.badRequest()
				.contentType(MediaType.APPLICATION_JSON)
				.body(createErrorResponse(useCaseConstraintViolationException));
	}

	@ExceptionHandler(UserException.class)
	public ResponseEntity<OperationOutcome> handleUserException(UserException userException) {
		return ResponseEntity.badRequest()
				.contentType(MediaType.APPLICATION_JSON)
				.body(createErrorResponse(userException));
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<OperationOutcome> handleAccessDeniedException(AccessDeniedException accessDeniedException) {
		return ResponseEntity.status(HttpStatus.FORBIDDEN)
				.contentType(MediaType.APPLICATION_JSON)
				.body(createErrorResponse(accessDeniedException));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<OperationOutcome> handleAllExceptions(Exception exception) {
		return handleSystemException(new SystemException(exception.getMessage(), exception));
	}

	@ExceptionHandler(SystemException.class)
	public ResponseEntity<OperationOutcome> handleSystemException(SystemException systemException) {
		final String reference = generateUniqueExceptionReference();

		logSystemException(systemException, reference);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.contentType(MediaType.APPLICATION_JSON)
				.body(createErrorResponse(reference));
	}

	private String generateUniqueExceptionReference() {
		return UUID.randomUUID()
				.toString();
	}

	private void logSystemException(Exception systemException, String exceptionReference) {
		LOGGER.error("*** System error reference: {}", exceptionReference, systemException);
	}

	private OperationOutcome createErrorResponse(UseCaseConstraintViolationException userMessageException) {
		return userMessageException.getConstraintViolations()
				.stream()
				.map(this::buildIssue)
				.collect(collectingAndThen(toList(), OperationOutcome::of));
	}

	private OperationOutcome createErrorResponse(UserException userException) {
		return OperationOutcome.newBuilder()
				.withIssues(Collections.singleton(buildIssue(userException)))
				.build();
	}

	private OperationOutcome createErrorResponse(AccessDeniedException accessDeniedException) {
		return OperationOutcome.newBuilder()
				.withIssues(Collections.singleton(buildIssue(accessDeniedException)))
				.build();
	}

	private OperationOutcome createErrorResponse(String reference) {
		return OperationOutcome.newBuilder()
				.withIssues(Collections.singleton(buildIssue(reference)))
				.build();
	}

	private Issue buildIssue(UseCaseConstraintViolation violation) {
		return Issue.newBuilder()
				.withField(violation.getField())
				.withMessage(violation.getMessage())
				.build();
	}

	private Issue buildIssue(UserException userException) {
		return Issue.newBuilder()
				.withMessage(userException.getMessage())
				.build();
	}

	private Issue buildIssue(AccessDeniedException accessDeniedException) {
		return Issue.newBuilder()
				.withMessage(accessDeniedException.getMessage())
				.build();
	}

	private Issue buildIssue(String reference) {
		return Issue.newBuilder()
				.withReference(reference)
				.withMessage(GENERIC_ERROR_MESSAGE)
				.build();
	}

}
