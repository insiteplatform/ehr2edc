package com.custodix.insite.local.ehr2edc.event;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

import com.custodix.insite.local.ehr2edc.ehr.epic.event.annotation.EHR2EDCEHREpicEventListener;
import com.custodix.insite.local.ehr2edc.ehr.event.annotation.EHREventListener;
import com.custodix.insite.local.ehr2edc.event.annotation.EHR2EDCEventListener;

@Component
class AsynchronousEventHandler {
	private final List<EventHandler> eventHandlers;

	AsynchronousEventHandler(ObjectProvider<List<EventHandler>> eventHandlers) {
		this.eventHandlers = eventHandlers.getIfAvailable(Collections::emptyList);
	}

	@EHR2EDCEventListener
	void handleEvent(com.custodix.insite.local.ehr2edc.events.AsyncSerializationSafeDomainEvent event) {
		eventHandlers.forEach(e -> e.handle(event));
	}

	@EHREventListener
	void handleEvent(com.custodix.insite.local.ehr2edc.ehr.domain.event.AsyncSerializationSafeDomainEvent event) {
		eventHandlers.forEach(e -> e.handle(event));
	}

	@EHR2EDCEHREpicEventListener
	void handleEvent(com.custodix.insite.local.ehr2edc.ehr.epic.domain.event.AsyncSerializationSafeDomainEvent event) {
		eventHandlers.forEach(e -> e.handle(event));
	}
}
