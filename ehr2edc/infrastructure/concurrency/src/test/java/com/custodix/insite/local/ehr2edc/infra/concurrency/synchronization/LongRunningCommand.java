package com.custodix.insite.local.ehr2edc.infra.concurrency.synchronization;

import com.custodix.insite.local.ehr2edc.shared.annotations.Command;

@Command
public class LongRunningCommand {
	void doCommand(Request request) throws InterruptedException {
		Thread.sleep(50);
		request.getValues().add(request.getValue());
	}

	void doCommand(SynchronizedRequest synchronizedRequest)
			throws InterruptedException {
		synchronizedRequest.start();
		doCommand(synchronizedRequest.toRequest());
		synchronizedRequest.stop();
	}
}
