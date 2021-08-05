package com.custodix.insite.local.ehr2edc.security;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.security.access.AccessDeniedException;

import com.custodix.insite.local.ehr2edc.query.security.IsAny;
import com.custodix.insite.local.ehr2edc.query.security.IsAssignedInvestigator;
import com.custodix.insite.local.ehr2edc.query.security.IsAuthenticated;
import com.custodix.insite.local.ehr2edc.query.security.IsDRM;
import com.custodix.insite.local.ehr2edc.shared.annotations.security.Allow;

class ReflectionSecurityGuard implements SecurityGuard {
	private final IsAny isAny;
	private final IsAuthenticated isAuthenticated;
	private final IsAssignedInvestigator isAssignedInvestigator;
	private final IsDRM isDRM;

	private final EnumMap<Allow.Rule, SecurityGuard> ruleGuardEnumMap;

	ReflectionSecurityGuard(IsAny isAny,
			IsAuthenticated isAuthenticated,
			IsAssignedInvestigator isAssignedInvestigator,
			IsDRM isDRM) {
		this.isAny = isAny;
		this.isAuthenticated = isAuthenticated;
		this.isAssignedInvestigator = isAssignedInvestigator;
		this.isDRM = isDRM;
		this.ruleGuardEnumMap = createAllowGuardMap();
	}

	private EnumMap<Allow.Rule, SecurityGuard> createAllowGuardMap() {
		final EnumMap<Allow.Rule, SecurityGuard> result = new EnumMap<>(Allow.Rule.class);
		result.put(Allow.Rule.ANYONE, request -> isAny.checkPermission());
		result.put(Allow.Rule.AUTHENTICATED, request -> isAuthenticated.checkPermission());
		result.put(Allow.Rule.ASSIGNED_INVESTIGATOR,
				request -> isAssignedInvestigator.checkPermission(Arrays.asList(request.getArgs())));
		result.put(Allow.Rule.DRM, request -> isDRM.checkPermission());
		return result;
	}

	@Override
	public void checkPermission(SecurityGuard.CheckPermissionsRequest request) {
		final Allow.Rule[] rules = getSecurityRules(request);
		if (rules.length == 0) {
			throw new AccessDeniedException("Access denied.");
		}
		for (Allow.Rule rule : rules) {
			final SecurityGuard guard = ruleGuardEnumMap.get(rule);
			if (guard != null) {
				guard.checkPermission(request);
			}
		}
	}

	private Allow.Rule[] getSecurityRules(SecurityGuard.CheckPermissionsRequest request) {
		final Optional<Allow> first = getSecurityAnnotations(request.getTargetType(), request.getMethod()).findFirst();
		return first.map(Allow::value)
				.orElse(new Allow.Rule[0]);
	}

	private Stream<Allow> getSecurityAnnotations(Class<?> leaf, Method method) {
		if (leaf == null) {
			return Stream.empty();
		}

		final Stream<Allow> self = getSecurityAnnotation(leaf, method);
		final Class<?> superClass = leaf.getSuperclass();
		final Class<?>[] interfaces = leaf.getInterfaces();
		final Stream<Allow> typesWithMethod = Stream.concat(Stream.of(superClass), Stream.of(interfaces))
				.flatMap(c -> getSecurityAnnotations(c, method));

		return Stream.concat(self, typesWithMethod);
	}

	private Stream<Allow> getSecurityAnnotation(Class<?> leaf, Method method) {
		try {
			final Method declaredMethod = leaf.getDeclaredMethod(method.getName(), method.getParameterTypes());
			final Allow annotation = declaredMethod.getAnnotation(Allow.class);
			return annotation == null ? Stream.empty() : Stream.of(annotation);
		} catch (NoSuchMethodException e) {
			return Stream.empty();
		}
	}
}