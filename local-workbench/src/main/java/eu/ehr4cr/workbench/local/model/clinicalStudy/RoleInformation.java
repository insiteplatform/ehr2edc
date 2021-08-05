package eu.ehr4cr.workbench.local.model.clinicalStudy;

public class RoleInformation {
	boolean isCoordinator;
	boolean isInvestigator;

	public RoleInformation(boolean isCoordinator, boolean isInvestigator) {
		this.isCoordinator = isCoordinator;
		this.isInvestigator = isInvestigator;
	}

	public boolean isCoordinator() {
		return isCoordinator;
	}

	public boolean isInvestigator() {
		return isInvestigator;
	}
}
