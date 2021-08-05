package com.custodix.insite.local.ehr2edc.query.populator;

import com.custodix.insite.local.ehr2edc.query.executor.common.concept.ConceptCode;
import com.custodix.insite.local.ehr2edc.query.executor.domain.Measurement;
import com.custodix.insite.local.ehr2edc.query.executor.vitalsign.domain.VitalSign;
import com.custodix.insite.local.ehr2edc.query.executor.vitalsign.domain.VitalSignConcept;
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class VitalSignFactory {
	public static final ConceptCode CONCEPT_BMI = ConceptCode.conceptFor("BMI_CODE");
	public static final ConceptCode CONCEPT_HEIGHT = ConceptCode.conceptFor("50373000");

	VitalSign bmi(SubjectId subjectId, LocalDateTime date) {
		return VitalSign.newBuilder()
				.withSubjectId(subjectId)
				.withEffectiveDateTime(date)
				.withMeasurement(Measurement.newBuilder()
						.withUnit("unit")
						.withLowerLimit(BigDecimal.ZERO)
						.withUpperLimit(BigDecimal.TEN)
						.withValue(BigDecimal.ONE)
						.build())
				.withConcept(bmiConcept())
				.build();
	}

	VitalSign height(SubjectId subjectId, LocalDateTime date) {
		return VitalSign.newBuilder()
				.withSubjectId(subjectId)
				.withEffectiveDateTime(date)
				.withMeasurement(Measurement.newBuilder()
						.withUnit("m")
						.withLowerLimit(new BigDecimal("1.50"))
						.withUpperLimit(new BigDecimal("2.00"))
						.withValue(new BigDecimal("1.77"))
						.build())
				.withConcept(heightConcept())
				.build();
	}

	private static VitalSignConcept bmiConcept() {
		return VitalSignConcept.newBuilder()
				.withConcept(CONCEPT_BMI)
				.withComponent("component")
				.withLaterality("laterality")
				.withLocation("location")
				.withPosition("position")
				.build();
	}

	private static VitalSignConcept heightConcept() {
		return VitalSignConcept.newBuilder()
				.withConcept(CONCEPT_HEIGHT)
				.withComponent("HEIGHT")
				.build();
	}
}
