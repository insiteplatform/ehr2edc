package com.custodix.insite.local.ehr2edc.query.fhir.medication;

import java.util.Optional;
import java.util.function.Consumer;

import com.custodix.insite.local.ehr2edc.query.executor.common.concept.ConceptCode;
import com.custodix.insite.local.ehr2edc.query.executor.medication.domain.MedicationConcept;

import ca.uhn.fhir.model.api.IDatatype;
import ca.uhn.fhir.model.dstu2.composite.CodeableConceptDt;
import ca.uhn.fhir.model.dstu2.composite.ResourceReferenceDt;
import ca.uhn.fhir.model.dstu2.resource.Medication;

public class MedicationConceptCodeProcessor {

	public void execute(IDatatype medication, Consumer<MedicationConcept> medicationConceptConsumer) {
		createMedicationConceptCode(medication).ifPresent(medicationConceptConsumer);
	}

	public Optional<MedicationConcept> createMedicationConceptCode(IDatatype medication) {
		return getCodeableConceptDt(medication)
				.map(CodeableConceptDt::getCodingFirstRep)
				.map(coding -> MedicationConcept.newBuilder()
						.withConcept(ConceptCode.conceptFor(coding.getCode()))
						.withName(coding.getDisplay())
						.build());
	}

	private Optional<CodeableConceptDt> getCodeableConceptDt(IDatatype medication) {
		if(isCodeableConceptDt(medication)) {
			return Optional.of((CodeableConceptDt) medication);
		} else if(isMedicationReference(medication)) {
			return Optional.of(((Medication) ((ResourceReferenceDt) medication).getResource()).getCode());
		}
		return Optional.empty();
	}

	private boolean isCodeableConceptDt(IDatatype datatype) {
		return datatype instanceof CodeableConceptDt;
	}
	private boolean isMedicationReference(IDatatype medication) {
		return medication instanceof ResourceReferenceDt && ((ResourceReferenceDt)medication).getResource() instanceof Medication;
	}
}
