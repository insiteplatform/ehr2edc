package com.custodix.insite.local.ehr2edc.usecase.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.custodix.insite.local.ehr2edc.DatawarehouseUpdate;
import com.custodix.insite.local.ehr2edc.command.DatawarehouseUpdated;
import com.custodix.insite.local.ehr2edc.shared.annotations.Command;

@Command
class DatawarehouseUpdatedCommand implements DatawarehouseUpdated {
	private static final Logger LOGGER = LoggerFactory.getLogger(DatawarehouseUpdatedCommand.class);

	@Override
	public void update() {
		LOGGER.info("Datawarehouse updated.");
		new DatawarehouseUpdate().finished();
	}
}
