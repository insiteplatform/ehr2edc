package eu.ehr4cr.workbench.local;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

import eu.ehr4cr.workbench.local.eventpublisher.EventPublisher;

public class TestEventPublisher implements EventPublisher {
	private final Queue<Object> events;
	private final EventPublisher eventPublisher;
	private boolean shouldPropagateEvents;

	public TestEventPublisher() {
		this(null);
		this.shouldPropagateEvents = false;
	}

	public TestEventPublisher(EventPublisher eventPublisher) {
		events = new LinkedList<>();
		this.eventPublisher = eventPublisher;
		this.shouldPropagateEvents = false;
	}

	@Override
	public void publishEvent(Object event) {
		events.add(event);
		if (shouldPropagateEvents) {
			eventPublisher.publishEvent(event);
		}
	}

	public void enableEventPropagation() {
		shouldPropagateEvents = true;
	}

	public void disableEventPropagation() {
		shouldPropagateEvents = false;
	}

	public Object poll() {
		return events.poll();
	}

	public boolean isEmpty() {
		return events.isEmpty();
	}

	/**
	 * @deprecated Use {@link #poll()} ()} to handle each individual event
	 */
	@Deprecated
	public <T> List<T> getAllEventsFor(Class<T> eventClass) {
		return events.stream()
				.filter(eventClass::isInstance)
				.map(event -> (T) event)
				.collect(Collectors.toList());
	}

	public void clear() {
		events.clear();
	}
}