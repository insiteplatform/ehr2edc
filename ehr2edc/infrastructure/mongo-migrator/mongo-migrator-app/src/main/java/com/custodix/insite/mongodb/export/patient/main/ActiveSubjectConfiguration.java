package com.custodix.insite.mongodb.export.patient.main;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.custodix.insite.mongodb.export.patient.application.api.ActivateSubject;
import com.custodix.insite.mongodb.export.patient.application.api.DeactivateSubject;
import com.custodix.insite.mongodb.export.patient.application.api.GetActiveSubjects;
import com.custodix.insite.mongodb.export.patient.application.command.ActivateSubjectCommand;
import com.custodix.insite.mongodb.export.patient.application.command.DeactivateSubjectCommand;
import com.custodix.insite.mongodb.export.patient.application.query.GetActiveSubjectsQuery;
import com.custodix.insite.mongodb.export.patient.domain.repository.ActiveSubjectEHRGateway;
import com.custodix.insite.mongodb.export.patient.domain.repository.PatientNamespaceRepository;
import com.custodix.insite.mongodb.export.patient.infrastructure.activesubject.ActiveSubjectMongoGateway;
import com.custodix.insite.mongodb.export.patient.infrastructure.mongo.repository.ActiveSubjectDocumentRepository;

@Configuration
public class ActiveSubjectConfiguration {

	@Bean
	public ActivateSubject activateSubject(ActiveSubjectEHRGateway activeSubjectEHRGateway, PatientNamespaceRepository patientNamespaceRepository) {
		return new ActivateSubjectCommand(activeSubjectEHRGateway, patientNamespaceRepository);
	}

	@Bean
	public DeactivateSubject deactivateSubject(ActiveSubjectEHRGateway activeSubjectEHRGateway) {
		return new DeactivateSubjectCommand(activeSubjectEHRGateway);
	}

	@Bean
	public GetActiveSubjects getActiveSubjects(ActiveSubjectEHRGateway activeSubjectEHRGateway) {
		return new GetActiveSubjectsQuery(activeSubjectEHRGateway);
	}

	@Bean
	public ActiveSubjectEHRGateway activeSubjectEHRGateway(
			ActiveSubjectDocumentRepository activeSubjectDocumentRepository) {
		return new ActiveSubjectMongoGateway(activeSubjectDocumentRepository);
	}
}
