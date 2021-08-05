package com.custodix.insite.local.ehr2edc.security;

import java.lang.reflect.Method;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

@Aspect
class AuthorizationController {

	private final SecurityGuard securityGuard;

	AuthorizationController(SecurityGuard securityGuard) {
		this.securityGuard = securityGuard;
	}

	@Before("annotatedWithSecurity() && publicMethod()")
	void publicMethodInsideSecurity(JoinPoint joinPoint) {
		checkSecurity(joinPoint, securityGuard::checkPermission);
	}

	@Pointcut("notAGuardCheck() && hasDenyAll()")
	void annotatedWithSecurity()  { /* pointcut */ }

	@Pointcut("(within(@com.custodix.insite.local.ehr2edc.shared.annotations.security.DenyAll *) || "
		     + "within(@(@com.custodix.insite.local.ehr2edc.shared.annotations.security.DenyAll *) *)))")
	void hasDenyAll() { /* pointcut */ }

	@Pointcut("!within(@com.custodix.insite.local.ehr2edc.shared.annotations.security.GuardCheck *)")
	void notAGuardCheck() { /* pointcut */ }

	@Pointcut("execution(public * *(..))")
	void publicMethod() { /* pointcut */ }

	private void checkSecurity(JoinPoint joinPoint, SecurityGuard guard) {
		SecurityGuard.CheckPermissionsRequest request = createCheckPermissionsRequest(joinPoint);
		guard.checkPermission(request);
	}

	private SecurityGuard.CheckPermissionsRequest createCheckPermissionsRequest(JoinPoint joinPoint) {
		final Signature signature = joinPoint.getSignature();
		if (signature instanceof MethodSignature) {
			final Class<?> targetType = joinPoint.getTarget().getClass();
			final Method method = ((MethodSignature) signature).getMethod();
			final Object[] args = joinPoint.getArgs();
			return new SecurityGuard.CheckPermissionsRequest(targetType, method, args);
		}
		return null;
	}
}
