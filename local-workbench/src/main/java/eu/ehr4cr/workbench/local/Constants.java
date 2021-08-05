package eu.ehr4cr.workbench.local;

/**
 * Container to hold all static constants for the application. Only use for
 * things that will never change. For configuration, use either a database table
 * or the core.application property file.
 */
public abstract class Constants {

	public static final int STRING_SHORT = 80;
	public static final int STRING_LONG = 256;
	public static final int STRING_HUGE = 2000;

	public static final String PROCESS_KEY = "patientRecruitment";

	public static final String PROCESS_VARIABLE_STUDY_ID = "study_id";
	public static final String TASK_VARIABLE_APPROVED = "approved";
	public static final String TASK_VARIABLE_PATIENT_ABORT = "abort";
	public static final String TASK_VARIABLE_PATIENT_ID = "patient_id";

	public static final String ISO8601PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";

	public static final String LOCAL_WORKBENCH_EVENTS_DESTINATION = "application.events";
	public static final String EHR2EDC_EVENTS_DESTINATION = "ehr2edc.events";
	public static final String MONGO_MIGRATOR_EVENTS_DESTINATION = "mongo.migrator.events";
	public static final String EHR_EVENTS_DESTINATION = "ehr.events";
	public static final String EHR2EDC_EHR_EPIC_EVENTS_DESTINATION = "ehr2edc.ehr.epic.events";
}
