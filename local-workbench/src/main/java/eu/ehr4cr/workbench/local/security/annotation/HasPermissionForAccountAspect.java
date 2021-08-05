package eu.ehr4cr.workbench.local.security.annotation;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import eu.ehr4cr.workbench.local.security.SecurityContextUser;
import eu.ehr4cr.workbench.local.usecases.checkuserpermissions.CheckUserHasPermissionForAccount;

@Aspect
@Component
class HasPermissionForAccountAspect {
	private final CheckUserHasPermissionForAccount checkUserHasPermissionForAccount;

	HasPermissionForAccountAspect(CheckUserHasPermissionForAccount checkUserHasPermissionForAccount) {
		this.checkUserHasPermissionForAccount = checkUserHasPermissionForAccount;
	}

	@Around("@annotation(HasPermissionForAccount) && args(hasUserIdentifier)")
	public Object handle(ProceedingJoinPoint proceedingJoinPoint, HasUserIdentifier hasUserIdentifier) throws Throwable {
		final SecurityContextUser securityContextUser = (SecurityContextUser) SecurityContextHolder.getContext()
				.getAuthentication()
				.getPrincipal();
		if (!checkUserHasPermissionForAccount.check(securityContextUser.getWorkbenchUser(),
				hasUserIdentifier.getUserIdentifier())) {
			throw new AccessDeniedException(
					"User does not have access to the account of user " + hasUserIdentifier.getUserIdentifier());
		}
		return proceedingJoinPoint.proceed();
	}
}
