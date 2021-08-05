package com.custodix.workbench.local.scheduling;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ErrorHandler;

public class ScheduledTaskErrorHandler implements ErrorHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(ScheduledTaskErrorHandler.class);

	@Override
	public void handleError(final Throwable t) {
		LOGGER.error("Unexpected error occurred in scheduled task. Add debug logging for more info.");
		LOGGER.debug("Stacktrace:", t);
	}
}
