package com.custodix.insite.local.ehr2edc.ehr.fhir.command.impl;

import com.custodix.insite.local.ehr2edc.ehr.domain.event.EHRSubjectRegistrationStatus;
import com.custodix.insite.local.ehr2edc.ehr.domain.event.EHRSubjectRegistrationStatusUpdated;
import com.custodix.insite.local.ehr2edc.ehr.fhir.command.FhirSubjectRegistration;
import com.custodix.insite.local.ehr2edc.ehr.main.domain.event.DomainEventPublisher;
import com.custodix.insite.local.ehr2edc.query.patient.PatientEHRGatewayFactory;
import com.custodix.insite.local.ehr2edc.shared.annotations.Command;

@Command
public class FhirSubjectRegistrationCommand implements FhirSubjectRegistration {

	private final PatientEHRGatewayFactory patientEHRGatewayFactory;

	public FhirSubjectRegistrationCommand(PatientEHRGatewayFactory patientEHRGateway) {
		this.patientEHRGatewayFactory = patientEHRGateway;
	}

	@Override
	public void register(Request request) {
		if (patientEHRGatewayFactory.isFhir(request.getStudyId())) {
			final EHRSubjectRegistrationStatusUpdated event = EHRSubjectRegistrationStatusUpdated.newBuilder()
					.withPatientId(request.getPatientReference().getId())
					.withNamespace(request.getPatientReference().getSource())
					.withSubjectId(request.getSubjectId().getId())
					.withStatus(EHRSubjectRegistrationStatus.REGISTERED)
					.build();
			DomainEventPublisher.publishEvent(event);
		}
	}
}
