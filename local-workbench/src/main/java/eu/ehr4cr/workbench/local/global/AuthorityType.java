/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.ehr4cr.workbench.local.global;

/**
 * @author Eric Renault
 */
public enum AuthorityType {

	VIEW_ACCOUNTS,
	MANAGE_ACCOUNTS,
	VIEW_AUDIT_QUERIES,
	VIEW_PATIENT_FACTS,
	VIEW_SPONSOR_STUDIES,
	VIEW_ACTUATOR_DETAILS,
	MANAGE_PLACEMENT,
	CREATE_COHORTS,
	ASSIGN_INVESTIGATORS,
	ACCEPT_RECRUITMENT_STUDIES,
	DECLINE_RECRUITMENT_STUDIES,
	VIEW_RECRUITMENT_STUDIES,
	ARCHIVE_RECRUITMENT_STUDIES,
	EDIT_RECRUITMENT_STUDIES,
	EXPORT_COHORT_RECRUITMENT_STUDY;

	public String getInnerName() {
		return name();
	}
}
