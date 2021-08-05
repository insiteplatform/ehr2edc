package com.custodix.insite.local.ehr2edc.populator;

public interface EventPopulator {
	PopulatedEvent populateEvent(PopulationSpecification populationSpecification);
}
