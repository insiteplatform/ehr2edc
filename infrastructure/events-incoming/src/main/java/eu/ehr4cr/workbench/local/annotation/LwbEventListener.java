package eu.ehr4cr.workbench.local.annotation;

import static eu.ehr4cr.workbench.local.Constants.LOCAL_WORKBENCH_EVENTS_DESTINATION;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.jms.annotation.JmsListener;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@JmsListener(destination = LOCAL_WORKBENCH_EVENTS_DESTINATION)
public @interface LwbEventListener {
}
