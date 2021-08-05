package com.custodix.insite.local.ehr2edc.mongo.app.review.provenance;

import java.util.Optional;

import com.custodix.insite.local.ehr2edc.provenance.model.ProvenanceDataPoint;
import com.custodix.insite.local.ehr2edc.shared.exceptions.SystemException;
import com.custodix.insite.local.ehr2edc.provenance.model.ConceptCode;
import com.custodix.insite.local.ehr2edc.provenance.model.Measurement;
import com.custodix.insite.local.ehr2edc.provenance.model.ProvenanceVitalSign;
import com.custodix.insite.local.ehr2edc.provenance.model.VitalSignConcept;

public final class ReviewedProvenanceVitalSignDocumentMapper implements ReviewedProvenanceDataPointMapper {

	@Override
	public boolean supports(final ProvenanceDataPoint provenanceDataPoint) {
		return provenanceDataPoint instanceof ProvenanceVitalSign;
	}

	@Override
	public ReviewedProvenanceDataPointDocument map(ProvenanceDataPoint provenanceDataPoint) {
		if (supports(provenanceDataPoint)) {
			return toDocument((ProvenanceVitalSign) provenanceDataPoint);
		}
		throw new SystemException("Mapper does not support mapping of " + provenanceDataPoint);
	}

	private ReviewedProvenanceVitalSignDocument toDocument(ProvenanceVitalSign provenanceVitalSign) {
		return ReviewedProvenanceVitalSignDocument.newBuilder()
				.withConcept(getVitalSignConcept(provenanceVitalSign.getConcept()))
				.withEffectiveDateTime(provenanceVitalSign.getEffectiveDateTime())
				.withMeasurement(getMeasurement(provenanceVitalSign.getMeasurement()))
				.build();
	}

	private ReviewedVitalSignConceptDocument getVitalSignConcept(final VitalSignConcept vitalSignConcept) {
			return Optional.ofNullable(vitalSignConcept)
					.map(this::map)
					.orElse(null);
	}

	private ReviewedVitalSignConceptDocument map(final VitalSignConcept c) {
		return ReviewedVitalSignConceptDocument.newBuilder()
				.withConcept(getConceptCode(c.getConcept()))
				.withComponent(c.getComponent())
				.withLocation(c.getLocation())
				.withLaterality(c.getLaterality())
				.withPosition(c.getPosition())
				.build();
	}

	private ReviewedConceptCodeDocument getConceptCode(final ConceptCode concept) {
		return ReviewedConceptCodeDocument.of(concept.getCode());
	}

	private ReviewedMeasurementDocument getMeasurement(Measurement measurement) {
		return Optional.ofNullable(measurement)
				.map(c -> map(measurement))
				.orElse(null);
	}

	private ReviewedMeasurementDocument map(final Measurement measurement) {
		return ReviewedMeasurementDocument.newBuilder()
			.withLowerLimit(measurement.getLowerLimit())
			.withUpperLimit(measurement.getUpperLimit())
			.withValue(measurement.getValue())
			.withUnit(measurement.getUnit())
			.build();
	}
}