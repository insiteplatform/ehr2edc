package com.custodix.insite.local.ehr2edc;

import com.custodix.insite.local.ehr2edc.events.DatawarehouseUpdatedEvent;

public class DatawarehouseUpdate {

	public void finished() {
		DomainEventPublisher.getInstance()
				.publishEvent(new DatawarehouseUpdatedEvent());
	}
}
