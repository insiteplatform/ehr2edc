package com.custodix.insite.local.ehr2edc.ehr.epic.main.domain.event;

public interface EventPublisher {
	void publishEvent(Object event);
}