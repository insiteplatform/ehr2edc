package com.custodix.insite.local.ehr2edc.mongo.app.study;

import org.springframework.stereotype.Repository;

@Repository
public class TestStudyMongoRepository  implements com.custodix.insite.local.ehr2edc.TestStudyRepository {

	private final StudyMongoRepository.StudyMongoSnapshotRepository snapshotRepository;

	public TestStudyMongoRepository(StudyMongoRepository.StudyMongoSnapshotRepository snapshotRepository) {
		this.snapshotRepository = snapshotRepository;
	}

	@Override
	public void deleteAll() {
		snapshotRepository.deleteAll();
	}
}
