package eu.ehr4cr.workbench.local.security.annotation;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import eu.ehr4cr.workbench.local.exception.security.InvalidPermissionsException;
import eu.ehr4cr.workbench.local.exception.security.MissingUserException;
import eu.ehr4cr.workbench.local.global.AuthorityType;
import eu.ehr4cr.workbench.local.model.security.User;
import eu.ehr4cr.workbench.local.security.CustomPermissionEvaluator;
import eu.ehr4cr.workbench.local.security.SecurityContextUser;
import eu.ehr4cr.workbench.local.security.exception.WorkbenchAuthenticationException;

@Aspect
@Component
public class HasPermissionAspect {
	private static final Logger LOGGER = LoggerFactory.getLogger(CustomPermissionEvaluator.class);

	@Around("@annotation(hasPermission) ")
	public Object handle(ProceedingJoinPoint proceedingJoinPoint, HasPermission hasPermission) throws Throwable {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if(!validatePermission(authentication, hasPermission.value())) {
			throw new WorkbenchAuthenticationException("User does not have access");
		}
		return proceedingJoinPoint.proceed();
	}

	private boolean validatePermission(Authentication authentication, AuthorityType authorityType) {
		boolean hasPermission = true;
		if (authentication != null) {
			Authentication auth = SecurityContextHolder.getContext()
					.getAuthentication();
			User user = ((SecurityContextUser) auth.getPrincipal()).getWorkbenchUser();
			if (user != null && !user.hasAuthority(authorityType)) {
				InvalidPermissionsException e = new InvalidPermissionsException(user);
				LOGGER.error(e.getMessage());
				throw e;
			} else if (user == null) {
				throw new MissingUserException();
			}
		} else {
			hasPermission = false;
		}
		return hasPermission;
	}
}
