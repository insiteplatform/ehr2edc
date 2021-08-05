package com.custodix.insite.local.ehr2edc.query.fhir.medication;

import java.math.BigDecimal;
import java.util.function.Consumer;

import ca.uhn.fhir.model.api.IDatatype;
import ca.uhn.fhir.model.dstu2.composite.RangeDt;
import ca.uhn.fhir.model.dstu2.composite.SimpleQuantityDt;

public class DosageValueProcessor {

	public void execute(IDatatype dose, Consumer<BigDecimal> dosageValueConsumer) {
		if (isSimpleValue(dose)) {
			dosageValueConsumer.accept(((SimpleQuantityDt) dose).getValue());
		} else if (isRangeValue(dose)) {
			dosageValueConsumer.accept(((RangeDt) dose).getLow()
					.getValue());
		}
	}

	private boolean isSimpleValue(IDatatype dose) {
		return dose instanceof SimpleQuantityDt;
	}

	private boolean isRangeValue(IDatatype dose) {
		return dose instanceof RangeDt;
	}
}
