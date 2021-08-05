package com.custodix.insite.local.ehr2edc.mongo.app.review.provenance;

import java.util.Optional;

import com.custodix.insite.local.ehr2edc.provenance.model.*;
import com.custodix.insite.local.ehr2edc.shared.exceptions.SystemException;

public final class ReviewedProvenanceMedicationDocumentMapper implements ReviewedProvenanceDataPointMapper {

	@Override
	public boolean supports(final ProvenanceDataPoint provenanceDataPoint) {
		return provenanceDataPoint instanceof ProvenanceMedication;
	}

	@Override
	public ReviewedProvenanceDataPointDocument map(ProvenanceDataPoint provenanceDataPoint) {
		if (supports(provenanceDataPoint)) {
			return toDocument((ProvenanceMedication) provenanceDataPoint);
		}
		throw new SystemException("Mapper does not support mapping of " + provenanceDataPoint);
	}

	private ReviewedProvenanceMedicationDocument toDocument(ProvenanceMedication provenanceMedication) {
		return ReviewedProvenanceMedicationDocument.newBuilder()
				.withConcept(getConcept(provenanceMedication))
				.withStartDate(provenanceMedication.getStartDate())
				.withEndDate(provenanceMedication.getEndDate())
				.withDosage(getDosage(provenanceMedication))
				.withAdministrationRoute(provenanceMedication.getAdministrationRoute())
				.withDoseForm(provenanceMedication.getDoseForm())
				.withDosingFrequency(provenanceMedication.getDosingFrequency())
				.withEventType(provenanceMedication.getEventType())
				.build();
	}

	private ReviewedDosageDocument getDosage(final ProvenanceMedication provenanceMedication) {
		return Optional.ofNullable(provenanceMedication.getDosage())
				.map(this::map)
				.orElse(null);
	}

	private ReviewedMedicationConceptDocument getConcept(final ProvenanceMedication provenanceMedication) {
		return Optional.ofNullable(provenanceMedication.getConcept())
				.map(this::map)
				.orElse(null);
	}

	private ReviewedMedicationConceptDocument map(MedicationConcept medicationConcept) {
		ReviewedConceptCodeDocument concept = Optional.ofNullable(medicationConcept.getConcept())
				.map(this::map)
				.orElse(null);
		return ReviewedMedicationConceptDocument.newBuilder()
				.withConcept(concept)
				.withName(medicationConcept.getName())
				.build();
	}

	private ReviewedConceptCodeDocument map(ConceptCode concept) {
		return ReviewedConceptCodeDocument.of(concept.getCode());
	}

	private ReviewedDosageDocument map(Dosage dosage) {
		return ReviewedDosageDocument.newBuilder()
				.withValue(dosage.getValue())
				.withUnit(dosage.getUnit())
				.build();
	}
}
