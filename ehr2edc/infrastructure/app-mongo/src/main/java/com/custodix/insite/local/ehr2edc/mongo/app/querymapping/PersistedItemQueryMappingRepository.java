package com.custodix.insite.local.ehr2edc.mongo.app.querymapping;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersistedItemQueryMappingRepository extends MongoRepository<PersistedItemQueryMapping, String> {

}
