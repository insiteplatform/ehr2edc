package eu.ehr4cr.workbench.local.model.clinicalStudy;

public enum ClinicalStudyRole {
	STUDY_COORDINATOR(true), STUDY_INVESTIGATOR(true), STUDY_ASSIGNEE(true);

	private final boolean validUserRole;

	ClinicalStudyRole(boolean validUserRole) {
		this.validUserRole = validUserRole;
	}

	public boolean isValidUserRole() {
		return validUserRole;
	}

}