package com.custodix.insite.local.ehr2edc.mongo.app.study;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.custodix.insite.local.ehr2edc.Study;
import com.custodix.insite.local.ehr2edc.StudyRepository;
import com.custodix.insite.local.ehr2edc.Subject;
import com.custodix.insite.local.ehr2edc.mongo.app.document.StudyDocument;
import com.custodix.insite.local.ehr2edc.shared.exceptions.DomainException;
import com.custodix.insite.local.ehr2edc.snapshots.StudySnapshot;
import com.custodix.insite.local.ehr2edc.vocabulary.PatientCDWReference;
import com.custodix.insite.local.ehr2edc.vocabulary.StudyId;
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId;
import com.custodix.insite.local.ehr2edc.vocabulary.UserIdentifier;

@Repository
public class StudyMongoRepository implements StudyRepository {
	private final StudyMongoSnapshotRepository snapshotRepository;

	public StudyMongoRepository(StudyMongoSnapshotRepository snapshotRepository) {
		this.snapshotRepository = snapshotRepository;
	}

	@Override
	public List<Study> findAll() {
		List<StudyDocument> all = snapshotRepository.findAll();
		return all.stream()
				.map(StudyDocument::toSnapshot)
				.map(Study::restoreSnapshot)
				.collect(Collectors.toList());
	}

	@Override
	public Study getStudyById(StudyId studyId) {
		return Study.restoreSnapshot(snapshotRepository.findById(studyId.getId())
				.map(StudyDocument::toSnapshot)
				.orElseThrow(() -> DomainException.getInstance(DomainException.Type.NOT_EXISTS, studyId)));

	}

	@Override
	public Study findStudyByIdAndInvestigator(StudyId studyId, UserIdentifier investigatorId) {
		return Study.restoreSnapshot(
				snapshotRepository.findByStudyIdAndInvestigatorsUserId(studyId.getId(), investigatorId.getId())
						.map(StudyDocument::toSnapshot)
						.orElseThrow(() -> DomainException.getInstance(DomainException.Type.NOT_EXISTS, studyId)));
	}

	@Override
	public Study getBySubjectId(final SubjectId subjectId) {
		return Study.restoreSnapshot(snapshotRepository.getOneBySubjectsSubjectId(subjectId.getId()).toSnapshot());
	}

	@Override
	public Optional<Study> findBySubjectId(final SubjectId subjectId) {
		return snapshotRepository.findOneBySubjectsSubjectId(subjectId.getId())
				.map(StudyDocument::toSnapshot)
				.map(Study::restoreSnapshot);
	}

	@Override
	public List<Study> findAvailableStudiesForPatient(PatientCDWReference patientCDWReference,
			UserIdentifier investigatorId) {
		return snapshotRepository.findByInvestigatorsUserId(investigatorId.getId())
				.stream()
				.map(StudyDocument::toSnapshot)
				.map(Study::restoreSnapshot)
				.filter(s -> !s.isRegistered(patientCDWReference))
				.collect(Collectors.toList());
	}

	@Override
	public List<Study> findRegisteredStudiesForPatient(PatientCDWReference patientCDWReference,
			UserIdentifier investigatorId) {
		return snapshotRepository.findByInvestigatorsUserId(investigatorId.getId())
				.stream()
				.map(StudyDocument::toSnapshot)
				.map(Study::restoreSnapshot)
				.filter(s -> s.isRegistered(patientCDWReference))
				.collect(Collectors.toList());
	}

	@Override
	public Subject findSubjectByIdAndStudyIdAndInvestigator(SubjectId subjectId, StudyId studyId,
			UserIdentifier userIdentifier) {
		return findStudyByIdAndInvestigator(studyId, userIdentifier).getSubject(subjectId);
	}

	@Override
	public void save(Study study) {
		StudySnapshot studySnapshot = study.toSnapShot();
		snapshotRepository.save(StudyDocument.fromSnapshot(studySnapshot));
	}

	@Override
	public boolean exists(StudyId studyId) {
		return snapshotRepository.findById(studyId.getId())
				.isPresent();
	}

	@Override
	public void delete(Study study) {
		snapshotRepository.deleteById(study.getStudyId().getId());
	}

	@Repository
	public interface StudyMongoSnapshotRepository extends MongoRepository<StudyDocument, String> {
		Optional<StudyDocument> findByStudyIdAndInvestigatorsUserId(String studyId, String investigatorId);

		List<StudyDocument> findByInvestigatorsUserId(String investigatorId);

		StudyDocument getOneBySubjectsSubjectId(String subjectId);

		Optional<StudyDocument> findOneBySubjectsSubjectId(String subjectId);
	}

}
