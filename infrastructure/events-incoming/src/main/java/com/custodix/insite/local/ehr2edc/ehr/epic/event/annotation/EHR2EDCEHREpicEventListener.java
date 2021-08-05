package com.custodix.insite.local.ehr2edc.ehr.epic.event.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.jms.annotation.JmsListener;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@JmsListener(destination = EHR2EDCEHREpicEventListener.EHR2EDC_EHR_EPIC_EVENTS_DESTINATION)
public @interface EHR2EDCEHREpicEventListener {
	String EHR2EDC_EHR_EPIC_EVENTS_DESTINATION = "ehr2edc.ehr.epic.events";
}
