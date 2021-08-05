package com.custodix.insite.local.ehr2edc.ehr.main.domain.event;

public interface EventPublisher {
	void publishEvent(Object event);
}