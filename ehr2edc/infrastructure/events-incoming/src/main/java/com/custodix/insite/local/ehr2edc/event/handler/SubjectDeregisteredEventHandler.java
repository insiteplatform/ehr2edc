package com.custodix.insite.local.ehr2edc.event.handler;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import org.springframework.context.event.EventListener;

import com.custodix.insite.local.ehr2edc.event.controller.SubjectDeactivationController;
import com.custodix.insite.local.ehr2edc.event.controller.SubjectRegistrationController;
import com.custodix.insite.local.ehr2edc.events.SubjectDeregisteredEvent;

public class SubjectDeregisteredEventHandler {

	private final Set<Consumer<SubjectDeregisteredEvent>> handlers = new HashSet<>();

	public SubjectDeregisteredEventHandler(SubjectRegistrationController subjectRegistrationController,
			SubjectDeactivationController subjectDeactivationController) {
		handlers.add(subjectRegistrationController::handleSubjectDeregistered);
		handlers.add(subjectDeactivationController::handleSubjectDeregistered);
	}

	@EventListener
	public void handle(SubjectDeregisteredEvent event) {
		handlers.forEach(h -> h.accept(event));
	}
}
