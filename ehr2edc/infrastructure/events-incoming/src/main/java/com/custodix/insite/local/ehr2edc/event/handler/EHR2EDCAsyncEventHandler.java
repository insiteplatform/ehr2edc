package com.custodix.insite.local.ehr2edc.event.handler;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import com.custodix.insite.local.ehr2edc.ehr.domain.event.EHRSubjectRegistrationStatusUpdated;
import com.custodix.insite.local.ehr2edc.event.EventHandler;
import com.custodix.insite.local.ehr2edc.event.controller.DatawarehouseUpdatedEventHandler;
import com.custodix.insite.local.ehr2edc.event.controller.SubjectCreatedEventController;
import com.custodix.insite.local.ehr2edc.event.controller.UpdateEHRSubjectRegistrationStatusController;
import com.custodix.insite.local.ehr2edc.events.DatawarehouseUpdatedEvent;
import com.custodix.insite.local.ehr2edc.events.SubjectCreated;

public class EHR2EDCAsyncEventHandler implements EventHandler {

	private static final Consumer<Object> NOOP = nothing -> {
	};
	private final Map<Class<?>, Consumer> handlerByEventType = new HashMap<>();

	public EHR2EDCAsyncEventHandler(
			SubjectCreatedEventController subjectCreatedEventController,
			DatawarehouseUpdatedEventHandler datawarehouseUpdatedEventHandler,
			UpdateEHRSubjectRegistrationStatusController updateEHRSubjectRegistrationStatusController) {
		add(SubjectCreated.class, subjectCreatedEventController::handle);
		add(DatawarehouseUpdatedEvent.class, datawarehouseUpdatedEventHandler::handle);
		add(EHRSubjectRegistrationStatusUpdated.class, updateEHRSubjectRegistrationStatusController::handle);
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
