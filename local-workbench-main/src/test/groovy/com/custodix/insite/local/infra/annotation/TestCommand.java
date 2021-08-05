package com.custodix.insite.local.infra.annotation;

import com.custodix.insite.local.shared.annotations.Command;

@Command
public class TestCommand {
	public void doOperationSuccess() {
		// No-op
	}

	public void doOperationFail() {
		throw new RuntimeException("This is a test exception");
	}
}
