package eu.ehr4cr.workbench.local.usecases.checkcohortpatientpermissions;

public interface CheckUserHasPermissionForCohortPatient {
	boolean check(Object principal, Long patientId);
}