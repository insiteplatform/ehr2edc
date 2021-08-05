package eu.ehr4cr.workbench.local.security.annotation;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import eu.ehr4cr.workbench.local.usecases.checkcohortpatientpermissions.CheckUserHasPermissionForCohortPatient;

@Aspect
@Component
public class HasPermissionForCohortPatientAspect {

	private CheckUserHasPermissionForCohortPatient checkUserHasPermissionForCohortPatient;

	public HasPermissionForCohortPatientAspect(final CheckUserHasPermissionForCohortPatient checkUserHasPermissionForCohortPatient) {
		this.checkUserHasPermissionForCohortPatient = checkUserHasPermissionForCohortPatient;
	}

	@Around("@annotation(HasPermissionForCohortPatient) && args(hasPatientId)")
	public Object handle(ProceedingJoinPoint proceedingJoinPoint, HasPatientId hasPatientId) throws Throwable {
		if(!checkUserHasPermissionForCohortPatient.check(SecurityContextHolder.getContext().getAuthentication().getPrincipal()
		, (long) hasPatientId.getPatientId())) {
			throw new AccessDeniedException("User does not have access to this cohort");
		}
		return proceedingJoinPoint.proceed();
	}
}
