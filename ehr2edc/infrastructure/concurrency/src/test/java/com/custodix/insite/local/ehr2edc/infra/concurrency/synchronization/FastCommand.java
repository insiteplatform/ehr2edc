package com.custodix.insite.local.ehr2edc.infra.concurrency.synchronization;

import com.custodix.insite.local.ehr2edc.shared.annotations.Command;

@Command
public class FastCommand {
	void doCommand(Request request) {
		request.getValues().add(request.getValue());
	}

	void doCommand(SynchronizedRequest synchronizedRequest) {
		doCommand(synchronizedRequest.toRequest());
	}
}
