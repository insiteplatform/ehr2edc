package eu.ehr4cr.workbench.local.security.annotation;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import eu.ehr4cr.workbench.local.usecases.checkcohortpermissions.CheckUserHasPermissionForCohort;

@Aspect
@Component
public class HasPermissionForCohortAspect {

	private CheckUserHasPermissionForCohort checkUserHasPermissionForCohort;

	public HasPermissionForCohortAspect(final CheckUserHasPermissionForCohort checkUserHasPermissionForCohort) {
		this.checkUserHasPermissionForCohort = checkUserHasPermissionForCohort;
	}

	@Around("@annotation(HasPermissionForCohort) && args(hasCohortId)")
	public Object handle(ProceedingJoinPoint proceedingJoinPoint, HasCohortId hasCohortId) throws Throwable {
		if(!checkUserHasPermissionForCohort.check(SecurityContextHolder.getContext().getAuthentication().getPrincipal()
				, hasCohortId.getCohortId())) {
			throw new AccessDeniedException("User does not have access to this cohort");
		}
		return proceedingJoinPoint.proceed();
	}
}
