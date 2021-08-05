package com.custodix.insite.local.ehr2edc.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.custodix.insite.local.ehr2edc.command.DatawarehouseUpdated;

/**
 * @deprecated use {@link com.custodix.insite.local.ehr2edc.rest.actuator.ActuatorManagementController} instead
 */
@Deprecated
@RestController
@RequestMapping("/ehr2edc/datawarehouse")
public class DatawarehouseController {
	private static final Logger LOGGER = LoggerFactory.getLogger(DatawarehouseController.class);

	private final DatawarehouseUpdated datawarehouseUpdated;

	public DatawarehouseController(DatawarehouseUpdated datawarehouseUpdated) {
		this.datawarehouseUpdated = datawarehouseUpdated;
	}

	@PostMapping
	public ResponseEntity datawarehouseUpdated(@RequestParam(value = "timestamp", required = false) String timestamp) {
		LOGGER.info("Datawarehouse updated at " + timestamp);
		datawarehouseUpdated.update();
		return ResponseEntity.status(HttpStatus.ACCEPTED)
				.build();
	}
}
