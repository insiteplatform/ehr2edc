package com.custodix.workbench.local.scheduling;

public class ExceptionThrowingTask implements Runnable {
	@Override
	public void run() {
		throw new RuntimeException("An error occurred");
	}
}
