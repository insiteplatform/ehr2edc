package com.custodix.local.workbench.configuration;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;

class CustomAsyncExceptionHandler implements AsyncUncaughtExceptionHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(CustomAsyncExceptionHandler.class);

	@Override
	public void handleUncaughtException(Throwable throwable, Method method, Object... obj) {
		LOGGER.error("Error in async method ", throwable);
	}

}
