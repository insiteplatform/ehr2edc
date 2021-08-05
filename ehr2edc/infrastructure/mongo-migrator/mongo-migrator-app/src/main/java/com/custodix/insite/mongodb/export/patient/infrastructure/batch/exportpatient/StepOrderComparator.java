package com.custodix.insite.mongodb.export.patient.infrastructure.batch.exportpatient;

import static com.custodix.insite.mongodb.export.patient.infrastructure.batch.exportpatient.removepatientdata.RemovePatientDataStepFactory.REMOVE_PATIENT_DATA_STEP;

import java.io.Serializable;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import org.springframework.batch.core.Step;

public class StepOrderComparator implements Comparator<Step>, Serializable {

	private static final Integer PREPARE_STEP_ORDER_NUMBER = 10;
	private static final Integer EXPORT_STEP_ORDER_NUMBER = 20;
	private static final Map<String, Integer> ORDER_MAP = new HashMap<>();

	static {
		ORDER_MAP.put(REMOVE_PATIENT_DATA_STEP, PREPARE_STEP_ORDER_NUMBER);
	}

	@Override
	public int compare(final Step step1, final Step step2) {
		return getOrderNumber(step1.getName()).compareTo(getOrderNumber(step2.getName()));
	}

	private Integer getOrderNumber(final String stepName) {
		return ORDER_MAP.getOrDefault(stepName, EXPORT_STEP_ORDER_NUMBER);
	}
}
