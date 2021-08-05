package com.custodix.insite.local.ehr2edc.ehr.mongo.event.handler;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import com.custodix.insite.local.ehr2edc.ehr.mongo.event.controller.SubjectMigrationController;
import com.custodix.insite.local.ehr2edc.event.EventHandler;
import com.custodix.insite.local.ehr2edc.events.SubjectRegisteredEvent;

public class EHRAsyncEventHandler implements EventHandler {

	private static final Consumer<Object> NOOP = nothing -> {
	};
	private final Map<Class<?>, Consumer> handlerByEventType = new HashMap<>();

	public EHRAsyncEventHandler(SubjectMigrationController subjectMigrationController) {
		add(SubjectRegisteredEvent.class, subjectMigrationController::handle);
	}

	@Override
	public void handle(Object event) {
		handlerByEventType.getOrDefault(event.getClass(), NOOP)
				.accept(event);
	}

	private <T> void add(Class<T> type, Consumer<T> consumer) {
		handlerByEventType.put(type, consumer);
	}
}
