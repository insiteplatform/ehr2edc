package com.custodix.insite.local.ehr2edc.query.fhir.vitalsign;

import static java.util.Collections.emptyList;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.custodix.insite.local.ehr2edc.populator.EHRGateway;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.Query;
import com.custodix.insite.local.ehr2edc.query.executor.vitalsign.domain.VitalSign;
import com.custodix.insite.local.ehr2edc.query.executor.vitalsign.query.VitalSignQuery;
import com.custodix.insite.local.ehr2edc.query.executor.vitalsign.query.VitalSigns;
import com.custodix.insite.local.ehr2edc.query.fhir.patient.FhirDstu2PatientRepository;
import com.custodix.insite.local.ehr2edc.query.fhir.vitalsign.postprocessing.VitalSignPostProcessor;
import com.custodix.insite.local.ehr2edc.query.fhir.vocabulary.FhirResourceId;
import com.custodix.insite.local.ehr2edc.vocabulary.PatientCDWReference;

import ca.uhn.fhir.rest.client.api.IGenericClient;

class FhirDstu2VitalSignGateway implements EHRGateway<VitalSigns, VitalSignQuery> {

	private final IGenericClient fhirDstu2Client;
	private final List<FhirDstu2VitalSignQuery> fhirDstu2VitalSignQueries;
	private final FhirDstu2PatientRepository fhirDstu2PatientRepository;

	FhirDstu2VitalSignGateway(IGenericClient fhirDstu2Client, List<FhirDstu2VitalSignQuery> fhirDstu2VitalSignQueries,
			FhirDstu2PatientRepository fhirDstu2PatientRepository) {
		this.fhirDstu2Client = fhirDstu2Client;
		this.fhirDstu2VitalSignQueries = fhirDstu2VitalSignQueries;
		this.fhirDstu2PatientRepository = fhirDstu2PatientRepository;
	}

	@Override
	public boolean canHandle(Query<?> query) {
		return query instanceof VitalSignQuery;
	}

	@Override
	public VitalSigns execute(VitalSignQuery vitalSignQuery, LocalDate referenceDate) {
		PatientCDWReference patientCDWReference = getPatientCDWReference(vitalSignQuery);
		Optional<FhirResourceId> fhirPatientResourceId = findFhirPatientResourceId(patientCDWReference);
		return fhirPatientResourceId.map(id -> getVitalSigns(vitalSignQuery, referenceDate, id))
				.orElse(new VitalSigns(emptyList()));
	}

	private VitalSigns getVitalSigns(VitalSignQuery query, LocalDate referenceDate,
			FhirResourceId fhirPatientResourceId) {
		return fhirDstu2VitalSignQueries.stream()
				.flatMap(fhirDstu2VitalSignQuery -> process(query, referenceDate, fhirDstu2VitalSignQuery,
						fhirPatientResourceId))
				.collect(Collectors.collectingAndThen(Collectors.toList(), VitalSigns::new));
	}

	private Stream<VitalSign> process(VitalSignQuery query, LocalDate referenceDate,
			FhirDstu2VitalSignQuery fhirDstu2VitalSignQuery, FhirResourceId fhirPatientResourceId) {
		Stream<VitalSign> vitalSigns = fhirDstu2VitalSignQuery.query(fhirDstu2Client, query, referenceDate,
				fhirPatientResourceId);
		return new VitalSignPostProcessor().process(vitalSigns, query);
	}

	private Optional<FhirResourceId> findFhirPatientResourceId(PatientCDWReference patientCDWReference) {
		return fhirDstu2PatientRepository.findFhirResourceId(fhirDstu2Client, patientCDWReference);
	}

	private PatientCDWReference getPatientCDWReference(VitalSignQuery vitalSignQuery) {
		return vitalSignQuery.getCriteria()
				.subject()
				.getPatientCDWReference();
	}
}
