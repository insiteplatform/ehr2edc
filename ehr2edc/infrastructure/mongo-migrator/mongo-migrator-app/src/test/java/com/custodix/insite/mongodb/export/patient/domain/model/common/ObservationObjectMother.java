package com.custodix.insite.mongodb.export.patient.domain.model.common;


import static com.custodix.insite.mongodb.export.patient.domain.model.common.ObservationType.NUMERIC;

import java.math.BigDecimal;
import java.util.Arrays;

public class ObservationObjectMother {

    public static Observation aDefaultObservation() {
        return Observation.newBuilder()
                .withType(NUMERIC)
                .withValues(Arrays.asList(ObservationValue.newBuilder().withUnit("gr").withValue(BigDecimal.valueOf(10.22)).build()))
                .build();
    }

}