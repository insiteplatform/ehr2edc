package com.custodix.insite.local.ehr2edc.query.fhir.vitalsign.observation.mapping;

import static java.util.Optional.ofNullable;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.function.Consumer;

import org.apache.commons.lang3.StringUtils;

import ca.uhn.fhir.model.api.IDatatype;
import ca.uhn.fhir.model.dstu2.composite.QuantityDt;

class VitalSignObservationValueProcessor {
	static void processValue(IDatatype value, Consumer<BigDecimal> consumer) {
		getQuantity(value).map(QuantityDt::getValue)
				.ifPresent(consumer);
	}

	static void processUnit(IDatatype value, Consumer<String> consumer) {
		getQuantity(value).map(VitalSignObservationValueProcessor::getUnit)
				.ifPresent(consumer);
	}

	private static Optional<QuantityDt> getQuantity(IDatatype value) {
		Class<QuantityDt> quantityClass = QuantityDt.class;
		return ofNullable(value).filter(quantityClass::isInstance)
				.map(quantityClass::cast);
	}

	private static String getUnit(QuantityDt quantity) {
		if (StringUtils.isNotBlank(quantity.getCode())) {
			return quantity.getCode();
		} else {
			return quantity.getUnit();
		}
	}
}
