package com.custodix.insite.local.ehr2edc.mongo.app.audit;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.custodix.insite.local.ehr2edc.audit.PopulationContext;
import com.custodix.insite.local.ehr2edc.audit.PopulationContextRepository;

@Repository
public class PopulationContextMongoRepository implements PopulationContextRepository {

	private final PopulationContextSnapshotRepository populationContextSnapshotRepository;

	public PopulationContextMongoRepository(PopulationContextSnapshotRepository populationContextSnapshotRepository) {
		this.populationContextSnapshotRepository = populationContextSnapshotRepository;
	}

	@Override
	public void save(PopulationContext populationContext) {
		populationContextSnapshotRepository.save(PopulationContextDocument.from(populationContext));
	}

	@Repository
	interface PopulationContextSnapshotRepository extends MongoRepository<PopulationContextDocument, String> {
	}
}
