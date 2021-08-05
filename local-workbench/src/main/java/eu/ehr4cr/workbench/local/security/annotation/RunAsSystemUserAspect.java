package eu.ehr4cr.workbench.local.security.annotation;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import eu.ehr4cr.workbench.local.security.SystemUserAuthenticator;

@Aspect
@Component
public class RunAsSystemUserAspect {
	private final SystemUserAuthenticator systemUserAuthenticator;

	public RunAsSystemUserAspect(SystemUserAuthenticator systemUserAuthenticator) {
		this.systemUserAuthenticator = systemUserAuthenticator;
	}

	@Around("@annotation(RunAsSystemUser)")
	public Object handle(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
		Authentication originalAuthentication = SecurityContextHolder.getContext()
				.getAuthentication();
		try {
			systemUserAuthenticator.authenticate();
			return proceedingJoinPoint.proceed();
		} finally {
			SecurityContextHolder.getContext()
					.setAuthentication(originalAuthentication);
		}
	}
}
