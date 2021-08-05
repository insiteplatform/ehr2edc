package com.custodix.insite.mongodb.export.patient.domain.model;

public final class DomainEventPublisher {
	private static EventPublisher instance;

	private DomainEventPublisher() {
		//Class is not meant to be an object
	}

	public static EventPublisher getInstance() {
		return instance;
	}

	public static void setPublisher(EventPublisher newInstance) {
		DomainEventPublisher.instance = newInstance;
	}

	public static void publishEvent(Object event) {
		instance.publishEvent(event);
	}
}