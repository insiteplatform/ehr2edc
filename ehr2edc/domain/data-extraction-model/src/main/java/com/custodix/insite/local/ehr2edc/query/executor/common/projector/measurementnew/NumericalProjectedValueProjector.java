package com.custodix.insite.local.ehr2edc.query.executor.common.projector.measurementnew;

import java.math.BigDecimal;

import com.custodix.insite.local.ehr2edc.query.executor.common.query.Projector;

public interface NumericalProjectedValueProjector
		extends Projector<ProjectedValue<BigDecimal>, ProjectedValue<BigDecimal>> {
}
