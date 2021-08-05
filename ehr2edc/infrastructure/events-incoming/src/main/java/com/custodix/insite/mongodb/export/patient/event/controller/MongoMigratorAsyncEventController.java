package com.custodix.insite.mongodb.export.patient.event.controller;

public interface MongoMigratorAsyncEventController<T> {
	void handle(T event);
}
