package com.custodix.insite.local.ehr2edc.query.fhir;

import java.util.List;
import java.util.stream.Collectors;

import com.custodix.insite.local.ehr2edc.EHRConnectionRepository;
import com.custodix.insite.local.ehr2edc.populator.EHRGateway;
import com.custodix.insite.local.ehr2edc.populator.PopulationSpecification;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.Query;
import com.custodix.insite.local.ehr2edc.query.populator.EHRGatewayFactory;
import com.custodix.insite.local.ehr2edc.shared.exceptions.SystemException;
import com.custodix.insite.local.ehr2edc.vocabulary.EHRSystem;

public class FhirEHRGatewayFactory implements EHRGatewayFactory {

	private final List<FhirDstu2GatewayFactory> fhirDstu2GatewayFactories;
	private final EHRConnectionRepository ehrConnectionRepository;

	FhirEHRGatewayFactory(List<FhirDstu2GatewayFactory> fhirDstu2GatewayFactories,
			EHRConnectionRepository ehrConnectionRepository) {
		this.fhirDstu2GatewayFactories = fhirDstu2GatewayFactories;
		this.ehrConnectionRepository = ehrConnectionRepository;
	}

	@Override
	public EHRGateway create(Query query, PopulationSpecification specification) {
		List<FhirDstu2GatewayFactory> fhirDstu2GatewayFactoriesForQuery = getFhirDstu2GatewayFactoriesForQuery(query);

		switch (fhirDstu2GatewayFactoriesForQuery.size()) {
		case 0:
			return new FhirEmptyResultGateway();
		case 1:
			return fhirDstu2GatewayFactoriesForQuery.get(0)
					.create(query, specification);
		default:
			throw new SystemException(
					String.format("Multiple FhirDstu2GatewayFactories found for query '%s', only one is allowed.",
							query));
		}
	}

	private List<FhirDstu2GatewayFactory> getFhirDstu2GatewayFactoriesForQuery(Query query) {
		return this.fhirDstu2GatewayFactories.stream()
				.filter(f -> f.canHandle(query))
				.collect(Collectors.toList());
	}

	@Override
	public boolean canHandle(Query query, PopulationSpecification specification) {
		return ehrConnectionRepository.findByStudyIdAndSystem(specification.getStudyId(), EHRSystem.FHIR)
				.isPresent();
	}
}
