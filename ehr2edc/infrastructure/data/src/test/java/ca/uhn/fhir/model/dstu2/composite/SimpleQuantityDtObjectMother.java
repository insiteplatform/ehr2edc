package ca.uhn.fhir.model.dstu2.composite;

public class SimpleQuantityDtObjectMother {

	public static SimpleQuantityDt aDefaultSimpleQuantityDt() {
		SimpleQuantityDt simpleQuantityDt = new SimpleQuantityDt();
		simpleQuantityDt.setUnit("unitValue");
		simpleQuantityDt.setCode("unitCode");
		simpleQuantityDt.setSystem("unitSystem");
		simpleQuantityDt.setValue(0L);
		return simpleQuantityDt;
	}
}
