package com.custodix.insite.local.ehr2edc.shared.exceptions.conversion;

import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import com.custodix.insite.local.ehr2edc.shared.exceptions.DomainException;
import com.custodix.insite.local.ehr2edc.shared.exceptions.SystemException;
import com.custodix.insite.local.ehr2edc.shared.exceptions.UseCaseConstraintViolationException;
import com.custodix.insite.local.ehr2edc.shared.exceptions.UserException;

@Aspect
@Component
@Order(1)
class UseCaseSystemExceptionConversionAspect {

	@AfterThrowing(pointcut = "execution(* com.custodix.insite.local.ehr2edc.usecase..*(..))",
				   throwing = "ex")
	public void execute(final Exception ex) {
		if (!isComprehensibleByUser(ex)) {
			throw new SystemException(ex.getMessage(), ex);
		}
	}

	private boolean isComprehensibleByUser(Exception ex) {
		return ex instanceof UserException || ex instanceof UseCaseConstraintViolationException
				|| ex instanceof DomainException || ex instanceof AccessDeniedException;
	}
}
