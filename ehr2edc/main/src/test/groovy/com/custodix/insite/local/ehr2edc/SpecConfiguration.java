package com.custodix.insite.local.ehr2edc;

import static org.mockito.Mockito.mock;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.custodix.insite.local.GetUsersController;
import com.custodix.insite.local.ehr2edc.application.TestGetUsersController;
import com.custodix.insite.local.ehr2edc.infra.edc.api.SpecificEDCStudyGateway;
import com.custodix.insite.local.ehr2edc.mongo.app.registration.MongoRegistrationRecordRepository;

@Configuration
public class SpecConfiguration {

	@Bean
	TestEventPublisher eventPublisher() {
		return new TestEventPublisher();
	}

	@Bean
	GetUsersController getUsersController() {
		return new TestGetUsersController();
	}

	@Bean
	TestRegistrationRecordRepository registrationRecordRepository(
			MongoRegistrationRecordRepository.RegistrationRecordMongoRepository registrationRecordMongoRepository) {
		return new TestRegistrationRecordRepository(registrationRecordMongoRepository);
	}

	@Bean
	InMemoryGetCurrentUserController currentUserController() {
		return new InMemoryGetCurrentUserController();
	}

	@Bean
	@Primary
	SpecificEDCStudyGateway specificEDCStudyGateway() {
		return mock(SpecificEDCStudyGateway.class);
	}
}
