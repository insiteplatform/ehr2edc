package com.custodix.insite.local.ehr2edc.mongo.app.registration;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.custodix.insite.local.ehr2edc.RegistrationRecord;
import com.custodix.insite.local.ehr2edc.RegistrationRecordRepository;

@Repository
public class MongoRegistrationRecordRepository implements RegistrationRecordRepository {

	private RegistrationRecordMongoRepository registrationRecordMongoRepository;

	public MongoRegistrationRecordRepository(RegistrationRecordMongoRepository registrationRecordMongoRepository) {
		this.registrationRecordMongoRepository = registrationRecordMongoRepository;
	}

	@Override
	public void save(RegistrationRecord registration) {
		registrationRecordMongoRepository.save(registration);
	}

	@Repository
	public interface RegistrationRecordMongoRepository extends MongoRepository<RegistrationRecord, Long> {
	}

}
