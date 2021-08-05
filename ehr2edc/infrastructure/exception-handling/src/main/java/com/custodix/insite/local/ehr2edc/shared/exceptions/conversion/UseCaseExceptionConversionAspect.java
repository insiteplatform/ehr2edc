package com.custodix.insite.local.ehr2edc.shared.exceptions.conversion;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Path;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.custodix.insite.local.ehr2edc.shared.exceptions.DomainException;
import com.custodix.insite.local.ehr2edc.shared.exceptions.UseCaseConstraintViolation;
import com.custodix.insite.local.ehr2edc.shared.exceptions.UseCaseConstraintViolationException;
import com.custodix.insite.local.ehr2edc.shared.exceptions.UserException;

@Aspect
@Component
@Order(2)
class UseCaseExceptionConversionAspect {

	private static final int NUMBER_OF_PATH_NODES_TO_SKIP = 2;
	private static final Object[] EMPTY_ARRAY = new Object[0];
	private static final String EMPTY_STRING = "";
	private static final String DOT = ".";
	private static final String OPEN_BRACKET = "{";
	private static final String CLOSE_BRACKET = "}";

	private final EHR2EDCMessageSourceTranslationService messageSourceTranslationService;

	UseCaseExceptionConversionAspect(EHR2EDCMessageSourceTranslationService messageSourceTranslationService) {
		this.messageSourceTranslationService = messageSourceTranslationService;
	}

	@AfterThrowing(pointcut = "execution(* com.custodix.insite.local.ehr2edc.usecase..*(..))",
				   throwing = "ex")
	public void execute(final ConstraintViolationException ex) {
		throw new UseCaseConstraintViolationException(violationsAsConstraintViolations(ex));
	}

	@AfterThrowing(pointcut = "execution(* com.custodix.insite.local.ehr2edc.usecase..*(..)) ",
				   throwing = "ex")
	public void execute(final DomainException ex) {
		if (StringUtils.isBlank(ex.getKey())) {
			throw new UserException(ex.getMessage());
		}
		throw new UserException(messageSourceTranslationService.translate(ex));
	}

	private List<UseCaseConstraintViolation> violationsAsConstraintViolations(ConstraintViolationException cve) {
		return cve.getConstraintViolations()
				.stream()
				.map(this::asUseCaseConstraintViolation)
				.collect(Collectors.toList());
	}

	private UseCaseConstraintViolation asUseCaseConstraintViolation(ConstraintViolation<?> constraintViolation) {
		ConstraintValidationMessage constraintValidationMessage = new ConstraintValidationMessage(constraintViolation);
		return UseCaseConstraintViolation.constraintViolation(constraintValidationMessage.getField(),
				constraintValidationMessage.getMessage());
	}

	private class ConstraintValidationMessage {
		private final ConstraintViolation<?> constraintViolation;

		ConstraintValidationMessage(ConstraintViolation<?> constraintViolation) {
			this.constraintViolation = constraintViolation;
		}

		public Object[] getParameters() {
			return EMPTY_ARRAY;
		}

		String getMessage() {
			return constraintViolation.getMessage()
					.replace(OPEN_BRACKET, EMPTY_STRING)
					.replace(CLOSE_BRACKET, EMPTY_STRING);
		}

		String getField() {
			return StreamSupport.stream(constraintViolation.getPropertyPath()
					.spliterator(), false)
					.skip(NUMBER_OF_PATH_NODES_TO_SKIP)
					.map(Path.Node::getName)
					.collect(Collectors.joining(DOT));
		}
	}
}
