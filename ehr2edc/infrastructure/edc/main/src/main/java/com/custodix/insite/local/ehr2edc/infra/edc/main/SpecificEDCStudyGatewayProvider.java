package com.custodix.insite.local.ehr2edc.infra.edc.main;

import java.util.Set;

import com.custodix.insite.local.ehr2edc.infra.edc.api.SpecificEDCStudyGateway;
import com.custodix.insite.local.ehr2edc.shared.exceptions.SystemException;
import com.custodix.insite.local.ehr2edc.vocabulary.EDCSystem;
import com.custodix.insite.local.ehr2edc.vocabulary.ExternalEDCConnection;

class SpecificEDCStudyGatewayProvider {
	private final Set<SpecificEDCStudyGateway> gateways;

	SpecificEDCStudyGatewayProvider(Set<SpecificEDCStudyGateway> gateways) {
		this.gateways = gateways;
	}

	public SpecificEDCStudyGateway get(ExternalEDCConnection connection) {
		EDCSystem edcSystem = connection.getEdcSystem();
		return gateways.stream()
				.filter(g -> g.supports(edcSystem))
				.findFirst()
				.orElseThrow(() -> new SystemException("No EDC gateway implementation found for system " + edcSystem));
	}
}
