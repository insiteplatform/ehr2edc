package com.custodix.insite.local.ehr2edc.query.executor.vitalsign

import com.custodix.insite.local.ehr2edc.query.executor.vitalsign.domain.VitalSign
import com.custodix.insite.local.ehr2edc.query.executor.vitalsign.domain.VitalSignConcept
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId

import java.time.LocalDateTime

import static com.custodix.insite.local.ehr2edc.query.executor.common.concept.ConceptCode.conceptFor
import static com.custodix.insite.local.ehr2edc.query.executor.objectmother.MeasurementObjectMother.diastolicBloodPressureMeasurement

class VitalSignObjectMother {
    public static final SubjectId SUBJECT_ID = SubjectId.of("subj-1")
    public static final LocalDateTime TIMESTAMP = LocalDateTime.of(2019, 7, 8, 13, 35)
    public static final String DIASTOLIC_BLOOD_PRESSURE_CODE = "271650006"
    public static final String LOCATION_ARM = "40983000"
    public static final String POSITION_SITTING = "33586001"
    public static final String LATERALITY_LEFT = "7771000"

    static VitalSign anEmptyVitalSign() {
        return VitalSign.newBuilder()
                .build()
    }

    static VitalSignConcept anEmptyVitalSignConcept() {
        return VitalSignConcept.newBuilder()
                .build()
    }

    static VitalSignConcept anEmptyVitalSignConceptCode() {
        return VitalSignConcept.newBuilder()
                .withConcept(conceptFor(null))
                .build()
    }

    static VitalSign diastolicBloodPressure() {
        return diastolicBloodPressure(TIMESTAMP)
    }

    static VitalSign diastolicBloodPressure(LocalDateTime timestamp) {
        return defaultVitalSignBuilder().withEffectiveDateTime(timestamp).build()
    }

    static VitalSignConcept diastolicBloodPressureConcept() {
        return defaultVitalSignConceptBuilder().build()
    }

    static VitalSign.Builder defaultVitalSignBuilder() {
        return VitalSign.newBuilder()
                .withSubjectId(SUBJECT_ID)
                .withEffectiveDateTime(LocalDateTime.now())
                .withConcept(diastolicBloodPressureConcept())
                .withMeasurement(diastolicBloodPressureMeasurement())
    }

    static VitalSignConcept.Builder defaultVitalSignConceptBuilder() {
        return VitalSignConcept.newBuilder().withConcept(conceptFor(DIASTOLIC_BLOOD_PRESSURE_CODE))
                .withComponent()
                .withLocation(LOCATION_ARM)
                .withPosition(POSITION_SITTING)
                .withLaterality(LATERALITY_LEFT)
    }
}
