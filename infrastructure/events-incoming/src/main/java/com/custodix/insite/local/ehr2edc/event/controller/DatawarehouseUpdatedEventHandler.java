package com.custodix.insite.local.ehr2edc.event.controller;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import com.custodix.insite.local.ehr2edc.events.DatawarehouseUpdatedEvent;

public class DatawarehouseUpdatedEventHandler implements EHR2EDCAsyncEventController<DatawarehouseUpdatedEvent> {

	private final Set<Consumer<DatawarehouseUpdatedEvent>> handlers = new HashSet<>();

	public DatawarehouseUpdatedEventHandler(ExportPatientIdsController exportPatientIdsController,
			ExportSubjectsController exportSubjectsController) {
		handlers.add(exportPatientIdsController::handle);
		handlers.add(exportSubjectsController::handle);
	}

	@Override
	public void handle(DatawarehouseUpdatedEvent event) {
		handlers.forEach(h -> h.accept(event));
	}
}
