package com.custodix.insite.local.ehr2edc.query.fhir.vitalsign.postprocessing;

import java.util.stream.Stream;

import com.custodix.insite.local.ehr2edc.query.executor.vitalsign.domain.VitalSign;

public interface VitalSignProcessor {
	Stream<VitalSign> process(final Stream<VitalSign> vitalSigns);
}
