package com.custodix.insite.local.ehr2edc.event.controller;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import org.springframework.context.event.EventListener;

import com.custodix.insite.local.ehr2edc.events.SubjectRegisteredEvent;

public class SubjectRegisteredEventHandler {

	private final Set<Consumer<SubjectRegisteredEvent>> handlers = new HashSet<>();

	public SubjectRegisteredEventHandler(SubjectRegistrationController subjectRegistrationController,
			SubjectActivationController subjectActivationController) {
		handlers.add(subjectRegistrationController::handleSubjectRegistered);
		handlers.add(subjectActivationController::handleSubjectRegistered);
	}

	@EventListener
	public void handle(SubjectRegisteredEvent event) {
		handlers.forEach(h -> h.accept(event));
	}
}
