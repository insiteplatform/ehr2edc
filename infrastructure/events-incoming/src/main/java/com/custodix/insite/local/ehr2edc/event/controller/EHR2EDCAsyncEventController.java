package com.custodix.insite.local.ehr2edc.event.controller;

import com.custodix.insite.local.ehr2edc.events.AsyncSerializationSafeDomainEvent;

public interface EHR2EDCAsyncEventController<T extends AsyncSerializationSafeDomainEvent> {
	void handle(T event);
}
