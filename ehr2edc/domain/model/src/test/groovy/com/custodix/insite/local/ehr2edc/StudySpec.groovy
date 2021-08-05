package com.custodix.insite.local.ehr2edc

import com.custodix.insite.local.ehr2edc.shared.exceptions.DomainException
import com.custodix.insite.local.ehr2edc.vocabulary.EDCSubjectReference
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId
import com.custodix.insite.local.ehr2edc.vocabulary.UserIdentifier
import spock.lang.Specification

import static com.custodix.insite.local.ehr2edc.snapshots.StudySnapshotObjectMother.aDefaultStudySnapshotBuilder
import static com.custodix.insite.local.ehr2edc.snapshots.SubjectSnapshotObjectMother.aDefaultSubjectSnapshotBuilder

class StudySpec extends Specification {
    public static final UserIdentifier USER_ID = UserIdentifier.of("42")

    def "Study find the EDC subject reference."(SubjectId studySubjectId, EDCSubjectReference edcSubjectReference) {
        given: "a study containing a subject with id '#studySubjectId' and edcSubjectReference id 'edcSubjectReference'"
        def study = createStudyBy(studySubjectId, edcSubjectReference, USER_ID)

        when: "finding EDC subject reference by subject id  '#studySubjectId'"
        def edcSubjectReferenceFounded = study.getEDCSubjectReference(studySubjectId)

        then: "returns the EDC subject reference "
        edcSubjectReferenceFounded == edcSubjectReference

        where:
        studySubjectId          | edcSubjectReference
        SubjectId.of("123-123") | EDCSubjectReference.of("567-567")
    }

    def "Study throw exception when a EDC Subject reference cannot be found"(SubjectId studySubjectId, SubjectId subjectIdNotInStudy) {
        given: "a study containing a subject with id '#studySubjectId'"
        def study = createStudyBy(studySubjectId)

        when: "finding EDC subject reference by non existing subject id  '#subjectIdNotInStudy' in study"
        study.getEDCSubjectReference(subjectIdNotInStudy)

        then: "an exception is thrown"
        DomainException exception = thrown()
        exception.key == "study.subject.unknownInStudy"
        exception.parameters == ["studyId-123", "567-567"]

        where:
        studySubjectId          | subjectIdNotInStudy
        SubjectId.of("123-123") | SubjectId.of("567-567")
    }

    private Study createStudyBy(SubjectId subjectId, EDCSubjectReference edcSubjectReference, UserIdentifier investigator) {
        def subjectSnapshot = aDefaultSubjectSnapshotBuilder().withSubjectId(subjectId).withEdcSubjectReference(edcSubjectReference).build()
        def additionalSubjectSnapshot = aDefaultSubjectSnapshotBuilder().withSubjectId(SubjectId.of("subjectId")).withEdcSubjectReference(EDCSubjectReference.of("EDCSubjectReference")).build()
        def studySnapshot = aDefaultStudySnapshotBuilder(investigator).withSubjects(Arrays.asList(subjectSnapshot, additionalSubjectSnapshot)).build()
        return Study.restoreSnapshot(studySnapshot)
    }

    private Study createStudyBy(SubjectId subjectId) {
        def subjectSnapshot = aDefaultSubjectSnapshotBuilder().withSubjectId(subjectId).build()
        def studySnapshot = aDefaultStudySnapshotBuilder(USER_ID).withSubjects(Collections.singletonList(subjectSnapshot)).build()
        return Study.restoreSnapshot(studySnapshot)
    }
}
