package com.custodix.insite.local.ehr2edc.query.fhir.medication;

import java.util.Optional;
import java.util.function.Consumer;

import ca.uhn.fhir.model.api.IDatatype;
import ca.uhn.fhir.model.dstu2.composite.CodeableConceptDt;
import ca.uhn.fhir.model.dstu2.composite.CodingDt;
import ca.uhn.fhir.model.dstu2.composite.ResourceReferenceDt;
import ca.uhn.fhir.model.dstu2.resource.Medication;

public class MedicationDoseFormProcessor {

	public void execute(IDatatype medication, Consumer<String> doseFormConsumer) {
		getDosageFormCode(medication).ifPresent(doseFormConsumer);
	}

	public Optional<String> getDosageFormCode(IDatatype medication) {
		return Optional.ofNullable(medication)
				.filter(value -> value instanceof ResourceReferenceDt)
				.map(value -> ((ResourceReferenceDt) value).getResource())
				.filter(value -> value instanceof Medication)
				.map(value -> ((Medication) value).getProduct())
				.map(Medication.Product::getForm)
				.map(CodeableConceptDt::getCodingFirstRep)
				.map(CodingDt::getCode);
	}
}
