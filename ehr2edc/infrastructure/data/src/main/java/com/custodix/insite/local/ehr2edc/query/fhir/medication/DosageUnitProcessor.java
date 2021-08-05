package com.custodix.insite.local.ehr2edc.query.fhir.medication;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.function.Consumer;

import ca.uhn.fhir.model.api.IDatatype;
import ca.uhn.fhir.model.dstu2.composite.RangeDt;
import ca.uhn.fhir.model.dstu2.composite.SimpleQuantityDt;

public class DosageUnitProcessor {

	public void execute(IDatatype dose, Consumer<String> dosageUnitConsumer) {
		if (isSimpleValue(dose)) {
			dosageUnitConsumer.accept(execute((SimpleQuantityDt) dose));
		} else if (isRangeValue(dose)) {
			dosageUnitConsumer.accept(execute(((RangeDt) dose).getLow()));
		}
	}

	private String execute(SimpleQuantityDt quantity) {
		if (isNotBlank(quantity.getCode())) {
			return quantity.getCode();
		} else {
			return quantity.getUnit();
		}
	}

	private boolean isSimpleValue(IDatatype dose) {
		return dose instanceof SimpleQuantityDt;
	}

	private boolean isRangeValue(IDatatype dose) {
		return dose instanceof RangeDt;
	}
}
