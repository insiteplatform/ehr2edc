package com.custodix.insite.local.ehr2edc.query.mongo.vitalsign.gateway;

import static java.util.Optional.ofNullable;

import java.util.function.Function;

import com.custodix.insite.local.ehr2edc.query.executor.domain.Measurement;
import com.custodix.insite.local.ehr2edc.query.executor.vitalsign.domain.VitalSign;
import com.custodix.insite.local.ehr2edc.query.executor.vitalsign.domain.VitalSignConcept;
import com.custodix.insite.local.ehr2edc.query.mongo.vitalsign.model.VitalSignConceptField;
import com.custodix.insite.local.ehr2edc.query.mongo.vitalsign.model.VitalSignDocument;
import com.custodix.insite.local.ehr2edc.query.mongo.vitalsign.model.VitalSignMeasurementField;

public class ToVitalSign implements Function<VitalSignDocument, VitalSign> {

	static ToVitalSign toVitalSign() {
		return new ToVitalSign();
	}

	@Override
	public VitalSign apply(final VitalSignDocument document) {
		VitalSign.Builder builder = VitalSign.newBuilder()
				.withSubjectId(document.getSubjectId())
				.withEffectiveDateTime(document.getEffectiveDateTime());

		ofNullable(document.getConcept()).map(this::map)
				.ifPresent(builder::withConcept);

		ofNullable(document.getMeasurement()).map(this::map)
				.ifPresent(builder::withMeasurement);

		return builder.build();
	}

	private Measurement map(final VitalSignMeasurementField vitalSignMeasurementField) {
		return Measurement.newBuilder()
				.withLowerLimit(vitalSignMeasurementField.getLowerLimit())
				.withUpperLimit(vitalSignMeasurementField.getUpperLimit())
				.withValue(vitalSignMeasurementField.getValue())
				.withUnit(vitalSignMeasurementField.getUnit())
				.build();
	}

	private VitalSignConcept map(final VitalSignConceptField vitalSignConceptField) {
		return VitalSignConcept.newBuilder()
				.withConcept(vitalSignConceptField.getConcept())
				.withComponent(vitalSignConceptField.getComponent())
				.withLaterality(vitalSignConceptField.getLaterality())
				.withLocation(vitalSignConceptField.getLocation())
				.withPosition(vitalSignConceptField.getPosition())
				.build();
	}
}
