package com.custodix.insite.mongodb.export.patient.domain.model;

public interface EventPublisher {
	void publishEvent(Object event);
}