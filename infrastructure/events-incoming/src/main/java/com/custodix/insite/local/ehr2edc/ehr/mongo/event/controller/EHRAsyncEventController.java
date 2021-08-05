package com.custodix.insite.local.ehr2edc.ehr.mongo.event.controller;

import com.custodix.insite.local.ehr2edc.events.AsyncSerializationSafeDomainEvent;

public interface EHRAsyncEventController<T extends AsyncSerializationSafeDomainEvent> {
	void handle(T event);
}
