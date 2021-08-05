package eu.ehr4cr.workbench.local.testingutils.argumentmatchers;

import org.mockito.ArgumentMatcher;

import eu.ehr4cr.workbench.local.vocabulary.clinical.StudyIdentifier;

public class StudyIdentifierArgumentMatcher implements ArgumentMatcher<StudyIdentifier> {
	private final Long studyId;

	public StudyIdentifierArgumentMatcher(Long studyId) {
		this.studyId = studyId;
	}

	@Override
	public boolean matches(final StudyIdentifier studyIdentifier) {
		return studyId.equals(studyIdentifier.getId());
	}
}
