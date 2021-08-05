package eu.ehr4cr.workbench.local.usecases.checkuserisinvestigator;

import eu.ehr4cr.workbench.local.model.security.User;
import eu.ehr4cr.workbench.local.vocabulary.clinical.StudyIdentifier;

public interface CheckUserIsInvestigator {
	boolean check(User user, Long clinicalStudyId);

	boolean check(User user, StudyIdentifier studyIdentifier);
}
