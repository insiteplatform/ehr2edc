package com.custodix.insite.local.ehr2edc;

import com.custodix.insite.local.ehr2edc.mongo.app.registration.MongoRegistrationRecordRepository;

public class TestRegistrationRecordRepository extends MongoRegistrationRecordRepository {

	private RegistrationRecord lastSaved = null;

	public TestRegistrationRecordRepository(RegistrationRecordMongoRepository registrationRecordMongoRepository) {
		super(registrationRecordMongoRepository);
	}

	@Override
	public void save(RegistrationRecord registration) {
		super.save(registration);
		lastSaved = registration;
	}

	public RegistrationRecord getLastSaved() {
		return lastSaved;
	}

	public void reset() {
		lastSaved = null;
	}
}
