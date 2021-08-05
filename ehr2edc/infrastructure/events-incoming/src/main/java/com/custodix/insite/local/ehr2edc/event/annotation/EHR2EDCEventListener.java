package com.custodix.insite.local.ehr2edc.event.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.jms.annotation.JmsListener;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@JmsListener(destination = EHR2EDCEventListener.EHR2EDC_EVENTS_DESTINATION)
public @interface EHR2EDCEventListener {
	String EHR2EDC_EVENTS_DESTINATION = "ehr2edc.events";
}
