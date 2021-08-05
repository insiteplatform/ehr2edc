package com.custodix.insite.local.ehr2edc;

import java.util.List;

import com.custodix.insite.local.ehr2edc.vocabulary.ExternalEDCConnection;
import com.custodix.insite.local.ehr2edc.vocabulary.LabName;

public interface LabAnalyteRangesGateway {
	List<LabName> findActiveLabs(ExternalEDCConnection externalEDCConnection);
}
