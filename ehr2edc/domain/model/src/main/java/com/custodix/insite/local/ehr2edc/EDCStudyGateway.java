package com.custodix.insite.local.ehr2edc;

import static java.util.Collections.emptyList;

import java.util.List;
import java.util.Optional;

import com.custodix.insite.local.ehr2edc.submitted.SubmittedEvent;
import com.custodix.insite.local.ehr2edc.vocabulary.EDCSubjectReference;
import com.custodix.insite.local.ehr2edc.vocabulary.StudyId;

public interface EDCStudyGateway {

	RegisteredSubjects findRegisteredSubjectIds(StudyId studyId);

	Optional<Boolean> isRegisteredSubject(StudyId studyId, EDCSubjectReference reference);

	void createSubject(Study study, EDCSubjectReference reference);

	String submitReviewedEvent(SubmittedEvent reviewedEvent, Study study);

	final class RegisteredSubjects {
		private final boolean fromEDC;
		private final List<EDCSubjectReference> registeredSubjectReferences;

		private RegisteredSubjects(boolean fromEDC, List<EDCSubjectReference> registeredSubjectReferences) {
			this.fromEDC = fromEDC;
			this.registeredSubjectReferences = registeredSubjectReferences;
		}

		public List<EDCSubjectReference> getRegisteredSubjectReferences() {
			return registeredSubjectReferences;
		}

		public boolean isFromEDC() {
			return fromEDC;
		}

		public static EDCStudyGateway.RegisteredSubjects fromEdc(List<EDCSubjectReference> registeredSubjectIds){
			return new RegisteredSubjects(true, registeredSubjectIds);
		}

		public static EDCStudyGateway.RegisteredSubjects noEdc(){
			return new RegisteredSubjects(false, emptyList());
		}
	}
}
