package com.custodix.insite.local.ehr2edc.query.populator;

import com.custodix.insite.local.ehr2edc.query.executor.common.concept.ConceptCode;
import com.custodix.insite.local.ehr2edc.query.executor.domain.Measurement;
import com.custodix.insite.local.ehr2edc.query.executor.laboratory.domain.LabConcept;
import com.custodix.insite.local.ehr2edc.query.executor.laboratory.domain.LabValue;
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Month;

import static com.custodix.insite.local.ehr2edc.query.executor.laboratory.domain.FastingStatus.NOT_FASTING;

public class LabValueFactory {
	private static final ConceptCode CONCEPT_GLUCOSE = ConceptCode.conceptFor("2339-0");
	private static final ConceptCode CONCEPT_HEMOGLOBIN = ConceptCode.conceptFor("718-7");
	private static final ConceptCode CONCEPT_HEMATOCRIT = ConceptCode.conceptFor("4544-3");

	LabValue aLabValueGlucose(SubjectId subjectId, LocalDateTime date) {
		LabConcept labConcept = LabConcept.newBuilder()
				.withConcept(CONCEPT_GLUCOSE)
				.withComponent("Glucose")
				.withMethod("")
				.withFastingStatus(NOT_FASTING)
				.withSpecimen("Bld")
				.build();
		Measurement measurement = Measurement.newBuilder()
				.withValue(new BigDecimal(13))
				.withUnit("mg/dL")
				.withLowerLimit(BigDecimal.TEN)
				.withUpperLimit(new BigDecimal(20))
				.build();
		return LabValue.newBuilder()
				.withLabConcept(labConcept)
				.forSubject(subjectId)
				.withStartDate(date)
				.withEndDate(date)
				.withQuantitativeResult(measurement)
				.build();
	}

	LabValue aLabValueHemoglobin(SubjectId subjectId) {
		LabConcept labConcept = LabConcept.newBuilder()
				.withConcept(CONCEPT_HEMOGLOBIN)
				.withComponent("Hemoglobin")
				.withMethod("")
				.withFastingStatus(NOT_FASTING)
				.withSpecimen("Bld")
				.build();
		Measurement measurement = Measurement.newBuilder()
				.withValue(new BigDecimal(14))
				.withUnit("g/dL")
				.withLowerLimit(new BigDecimal("12.1"))
				.withUpperLimit(new BigDecimal("15.1"))
				.build();
		return LabValue.newBuilder()
				.withLabConcept(labConcept)
				.forSubject(subjectId)
				.withStartDate(LocalDateTime.of(2019, Month.JUNE, 20, 0, 0))
				.withEndDate(LocalDateTime.of(2019, Month.JUNE, 20, 0, 0))
				.withQuantitativeResult(measurement)
				.build();
	}

	LabValue aLabValueHematocrit(SubjectId subjectId) {
		LabConcept labConcept = LabConcept.newBuilder()
				.withConcept(CONCEPT_HEMATOCRIT)
				.withComponent("Hematocrit")
				.withMethod("")
				.withFastingStatus(NOT_FASTING)
				.withSpecimen("Bld")
				.build();
		Measurement measurement = Measurement.newBuilder()
				.withValue(new BigDecimal(16))
				.withUnit("g/dL")
				.withLowerLimit(new BigDecimal(14))
				.withUpperLimit(new BigDecimal(18))
				.build();
		return LabValue.newBuilder()
				.withLabConcept(labConcept)
				.forSubject(subjectId)
				.withStartDate(LocalDateTime.of(2019, Month.JUNE, 20, 0, 0))
				.withEndDate(LocalDateTime.of(2019, Month.JUNE, 20, 0, 0))
				.withQuantitativeResult(measurement)
				.build();
	}
}
