package com.custodix.insite.local.ehr2edc.security;

import java.lang.reflect.Method;

interface SecurityGuard {
	class CheckPermissionsRequest {
		private final Class<?> targetType;
		private final Method method;
		private final Object[] args;

		CheckPermissionsRequest(Class<?> targetType, Method method, Object[] args) {
			this.targetType = targetType;
			this.method = method;
			this.args = args;
		}

		Class<?> getTargetType() {
			return targetType;
		}

		Method getMethod() {
			return method;
		}

		Object[] getArgs() {
			return args;
		}
	}

	void checkPermission(CheckPermissionsRequest request);
}