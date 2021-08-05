package com.custodix.insite.local.ehr2edc.query.fhir.demographic;

import static java.util.function.Function.identity;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import com.custodix.insite.local.ehr2edc.populator.EHRGateway;
import com.custodix.insite.local.ehr2edc.populator.PopulationSpecification;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.Query;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.criteria.DemographicTypeCriterion;
import com.custodix.insite.local.ehr2edc.query.executor.demographic.domain.Demographic;
import com.custodix.insite.local.ehr2edc.query.executor.demographic.domain.DemographicType;
import com.custodix.insite.local.ehr2edc.query.executor.demographic.query.DemographicQuery;
import com.custodix.insite.local.ehr2edc.query.executor.demographic.query.Demographics;
import com.custodix.insite.local.ehr2edc.query.fhir.FhirDstu2ClientFactory;
import com.custodix.insite.local.ehr2edc.query.fhir.FhirDstu2GatewayFactory;
import com.custodix.insite.local.ehr2edc.query.fhir.patient.FhirDstu2PatientRepository;
import com.custodix.insite.local.ehr2edc.vocabulary.StudyId;
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId;

import ca.uhn.fhir.model.dstu2.resource.Patient;
import ca.uhn.fhir.rest.client.api.IGenericClient;

class FhirDstu2DemographicGatewayFactory implements FhirDstu2GatewayFactory<Demographics, DemographicQuery> {

	private final Map<DemographicType, FhirDstu2DemographicFactory> fhirDstu2DemographicFactoryMap;
	private final FhirDstu2ClientFactory fhirDstu2ClientFactory;
	private final FhirDstu2PatientRepository fhirDstu2PatientRepository;

	public FhirDstu2DemographicGatewayFactory(List<FhirDstu2DemographicFactory> fhirDstu2DemographicFactories,
			FhirDstu2ClientFactory fhirDstu2ClientFactory, FhirDstu2PatientRepository fhirDstu2PatientRepository) {
		fhirDstu2DemographicFactoryMap = fhirDstu2DemographicFactories.stream()
				.collect(Collectors.toMap(FhirDstu2DemographicFactory::supports, identity()));
		this.fhirDstu2ClientFactory = fhirDstu2ClientFactory;
		this.fhirDstu2PatientRepository = fhirDstu2PatientRepository;
	}

	public boolean canHandle(Query query) {
		return query instanceof DemographicQuery && canHandleDemographicType(query);
	}

	private boolean canHandleDemographicType(Query query) {
		return query.getCriteria()
				.demographicType()
				.map(DemographicTypeCriterion::getDemographicType)
				.map(fhirDstu2DemographicFactoryMap::containsKey)
				.orElse(false);
	}

	@Override
	public EHRGateway<Demographics, DemographicQuery> create(DemographicQuery demographicQuery, PopulationSpecification specification) {
		return demographicQuery.getCriteria()
				.demographicType()
				.map(demographicTypeCriterion -> this.create(demographicTypeCriterion.getDemographicType(), specification.getStudyId()))
				.orElseThrow(() -> new IllegalStateException("Cannot create EHRGateway for a demographic query"));
	}

	private FhirDstu2DemographicGateway create(DemographicType demographicType, StudyId studyId) {
		IGenericClient fhirDstu2Client = fhirDstu2ClientFactory.getFhirDstu2Client(studyId);
		return new FhirDstu2DemographicGateway(fhirDstu2Client,
				getDemographicProducerFor(demographicType),
				fhirDstu2PatientRepository
				);
	}

	private BiFunction<Patient, SubjectId, Optional<Demographic>> getDemographicProducerFor(DemographicType demographicType) {
		return fhirDstu2DemographicFactoryMap.get(demographicType)::findDemographic;
	}

}
