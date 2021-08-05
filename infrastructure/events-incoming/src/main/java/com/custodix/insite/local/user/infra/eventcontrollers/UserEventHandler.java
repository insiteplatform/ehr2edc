package com.custodix.insite.local.user.infra.eventcontrollers;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import org.springframework.stereotype.Component;

import com.custodix.insite.local.user.domain.events.ImminentlyExpiringPasswordEvent;
import com.custodix.insite.local.user.domain.events.PasswordRecoveredEvent;

import eu.ehr4cr.workbench.local.eventhandlers.EventHandler;

@Component
class UserEventHandler implements EventHandler {
	private static final Consumer<Object> NOOP = nothing -> {
	};
	private final Map<Class<?>, Consumer> handlerByEventType;

	UserEventHandler(ImminentlyExpiringPasswordEventController imminentlyExpiringPasswordEventController,
			PasswordRecoveredEventController passwordRecoveredEventController) {
		handlerByEventType = new HashMap<>();
		add(ImminentlyExpiringPasswordEvent.class, imminentlyExpiringPasswordEventController::handle);
		add(PasswordRecoveredEvent.class, passwordRecoveredEventController::handle);
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
