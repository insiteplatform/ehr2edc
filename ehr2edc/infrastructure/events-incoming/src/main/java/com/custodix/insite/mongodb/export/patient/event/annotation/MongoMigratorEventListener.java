package com.custodix.insite.mongodb.export.patient.event.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.jms.annotation.JmsListener;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@JmsListener(destination = MongoMigratorEventListener.MONGO_MIGRATOR_EVENTS_DESTINATION)
public @interface MongoMigratorEventListener {

	String MONGO_MIGRATOR_EVENTS_DESTINATION = "mongo.migrator.events";
}
