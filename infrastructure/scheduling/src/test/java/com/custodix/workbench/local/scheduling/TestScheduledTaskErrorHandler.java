package com.custodix.workbench.local.scheduling;

public class TestScheduledTaskErrorHandler extends ScheduledTaskErrorHandler {
	private boolean isHandled;

	@Override
	public void handleError(final Throwable t) {
		super.handleError(t);
		isHandled = true;
	}

	public boolean isHandled() {
		return isHandled;
	}
}
