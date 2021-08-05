package ca.uhn.fhir.model.dstu2.resource;

import static ca.uhn.fhir.model.dstu2.composite.SimpleQuantityDtObjectMother.aDefaultSimpleQuantityDt;
import static java.util.Arrays.asList;

import java.util.Date;

import ca.uhn.fhir.model.api.TemporalPrecisionEnum;

public class MedicationDispenseObjectMother {

	private static final Date NOW = new Date();

	public static MedicationDispense aDefaultMedicationDispense() {
		MedicationDispense.DosageInstruction aDosageInstruction = aDefaultDosageInstruction();
		MedicationDispense medicationDispense = new MedicationDispense();
		medicationDispense.setDosageInstruction(asList(aDosageInstruction));
		medicationDispense.setWhenPrepared(NOW, TemporalPrecisionEnum.MILLI);
		medicationDispense.setWhenHandedOver(NOW, TemporalPrecisionEnum.MILLI);

		return medicationDispense;
	}

	public static MedicationDispense.DosageInstruction aDefaultDosageInstruction() {
		MedicationDispense.DosageInstruction dosageInstruction = new MedicationDispense.DosageInstruction();
		dosageInstruction.setDose(aDefaultSimpleQuantityDt());
		return dosageInstruction;
	}
}
