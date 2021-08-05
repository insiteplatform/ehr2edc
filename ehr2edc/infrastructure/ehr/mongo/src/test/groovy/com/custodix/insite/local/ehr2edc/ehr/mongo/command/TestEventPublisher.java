package com.custodix.insite.local.ehr2edc.ehr.mongo.command;

import java.util.LinkedList;
import java.util.Queue;

import com.custodix.insite.local.ehr2edc.ehr.main.domain.event.EventPublisher;

public class TestEventPublisher implements EventPublisher {
	private final Queue<Object> events;

	public TestEventPublisher() {
		events = new LinkedList<>();
	}

	@Override
	public void publishEvent(Object event) {
		events.add(event);
	}

	public Object poll() {
		return events.poll();
	}

	public boolean isEmpty() {
		return events.isEmpty();
	}

	public void clear() {
		events.clear();
	}
}