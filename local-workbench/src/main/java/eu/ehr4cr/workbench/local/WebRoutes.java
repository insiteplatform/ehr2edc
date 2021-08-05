package eu.ehr4cr.workbench.local;

import org.springframework.core.env.Environment;

public class WebRoutes {
	public static final String root = "/";
	public static final String home = "/home";
	public static final String error = "/error";
	public static final String login = "/auth/login";
	public static final String oidcLogin = "/oidc-login";
	public static final String logout = "/auth/logout";
	public static final String registerAccount = "/auth/register";
	public static final String recoverAccount = "/auth/recover";
	public static final String manageMembers = "/app/members/manage";
	public static final String assignMembers = "/app/members/assign";
	public static final String deleteMember = "/app/members/delete";
	public static final String recoverMember = "/app/members/recover";
	public static final String acceptMember = "/app/members/accept";
	public static final String myAccount = "/app/account";
	public static final String MY_ACCOUNT_PASSWORD = "/app/account#password";
	public static final String updateProfile = "/app/account/profile/update";
	public static final String updatePassword = "/app/account/password/update";
	public static final String updatePhysician = "/app/account/physician/update";
	public static final String accountQuestion = "/app/account/question";
	public static final String about = "/app/about";
	public static final String inviteUser = "/app/invite";
	public static final String reinviteUser = "/app/reinvite";
	public static final String completeInvitation = "/auth/completeInvitation";
	public static final String completeRecovery = "/auth/completeRecovery";
	public static final String hawtioActuator = "/actuator/hawtio";

	public static final String terminology_categories = "/app/terminology/categories";
	public static final String feasibilityDashboard = "/app/feasibility/";
	public static final String cohortStudies = "/app/cohort/studies";
	public static final String cohortStudy = "/app/cohort/studies/study";
	public static final String cohortStudyCreate = "/app/cohort/studies/create";
	public static final String cohortStudyUpdate = "/app/cohort/studies/update";
	public static final String cohortStudyBusy = "/app/cohort/studies/busy";
	public static final String cohortStudyDelete = "/app/cohort/studies/delete";
	public static final String cohortDetails = "/app/cohort/studies/details";
	public static final String cohortPatientFacts = "/app/cohort/studies/cohortPatientFacts";
	public static final String cohortPatientSources = "/app/cohort/studies/cohortPatientSources";
	public static final String cohortBuilder = "/app/cohort/studies/study/{cohortStudyId}/builder";
	public static final String cohortImport = "/app/cohort/studies/study/builder/import";
	public static final String cohortReuse = "/app/cohort/studies/study/builder/reuse";
	public static final String cohortSave = "/app/cohort/studies/study/save";
	public static final String cohortSaveCancel = "/app/cohort/studies/study/saveCancel";
	public static final String cohortSources = "/app/cohort/studies/study/sources";
	public static final String cohortUpdate = "/app/cohort/studies/study/updateCohort";
	public static final String cohortDelete = "/app/cohort/studies/study/deleteCohort";
	public static final String cohortExport = "/app/cohort/studies/study/exportCohort";
	public static final String cohortExportList = "/app/cohort/studies/study/cohortExportList";
	public static final String cohortExportDownload = "/app/cohort/studies/study/cohortExportDownload";
	public static final String cohortExportCancel = "/app/cohort/studies/study/cohortExportCancel";
	public static final String cohortExportDelete = "/app/cohort/studies/study/cohortExportDelete";
	public static final String cohortCompareSave = "/app/cohort/studies/study/compareCohortsSave";
	public static final String cohortAnalytics = "/app/cohort/studies/study/cohortAnalytics";
	public static final String cohortAnalyticsDatabase = "/app/cohort/studies/study/cohortAnalyticsDatabase";
	public static final String cohortAnalyticsStatus = "/app/cohort/studies/study/cohortAnalyticsStatus";
	public static final String cohortRecalculate = "/app/cohort/studies/study/cohortRecalculate";
	public static final String cohortFiltersDownload = "/app/cohort/studies/study/cohortFiltersDownload/{cohortId}";
	public static final String cohortDefinition = "/app/cohort/studies/study/cohortDefinition";

	public static final String patientIdentifiers = "/app/patient/{patientId}/identifiers";

	public static final String analysis = "/app/cohort/studies/study/analysis";
	public static final String analysisDelete = "/app/cohort/studies/study/deleteAnalysis";
	public static final String analysisStatus = "/app/cohort/studies/study/analysisStatus";
	public static final String analysisResult = "/app/cohort/studies/study/analysisResult";
	public static final String analysisCombinedResults = "/app/cohort/studies/study/analysisCombinedResults";

	public static final String feasibilityStudyDashboard = "/app/feasibility/studies";
	public static final String feasibilityStudyDetails = "/app/feasibility/studies/details";
	public static final String feasibilityQueryOverview = "/app/feasibility/studies/queries";
	public static final String feasibilityQueryDetails = "/app/feasibility/studies/details/query";
	public static final String feasibilityQueryExportExcel = "/app/feasibility/studies/details/query/exportExcel";

	public static final String clinicalStudyActivateScreening = "/app/clinical/studies/study/screening/activate";
	public static final String clinicalStudyArchiveScreening = "/app/clinical/studies/study/screening/archive";
	public static final String clinicalStudyEditScreening = "/app/clinical/studies/study/screening/edit";
	public static final String CLINICAL_STUDY_RECALCULATE_SCREENING = "/app/clinical/studies/study/screening/recalculate";
	public static final String clinicalStudySponsorFilters = "/app/clinical/studies/study/screening/sponsorFilters";
	public static final String clinicalStudyAccept = "/app/clinical/studies/study/accept";
	public static final String clinicalStudyDecline = "/app/clinical/studies/study/decline";

	public static final String clinicalStudiesList = "/app/clinical/studies";
	public static final String clinicalStudy = "/app/clinical/studies/study";
	public static final String clinicalStudyOverview = "/app/clinical/studies/study/overview";
	public static final String clinicalStudyInvestigators = "/app/clinical/studies/study/investigators";
	public static final String clinicalStudyRecruiting = "/app/clinical/studies/study/recruiting";
	public static final String clinicalStudyProtocol = "/app/clinical/studies/study/protocol";
	public static final String clinicalStudyFiltering = "/app/clinical/studies/study/filtering";
	public static final String clinicalStudyMessaging = "/app/clinical/studies/study/messaging";
	public static final String clinicalStudyDownloadProtocol = "/app/clinical/studies/study/protocol/download";
	public static final String clinicalStudyScreening = "/app/clinical/studies/study/screening";
	public static final String clinicalStudyScreeningManual = "/app/clinical/studies/study/screening/manual";
	public static final String CLINICAL_STUDY_COHORT_EXPORT = "/app/clinical/studies/study/cohort/export";
	public static final String CLINICAL_STUDY_COHORT_EXPORT_LIST = "/app/clinical/studies/study/cohort/exports";
	public static final String CLINICAL_STUDY_COHORT_EXPORT_DOWNLOAD = "/app/clinical/studies/study/cohort/export/download";
	public static final String CLINICAL_STUDY_COHORT_EXPORT_CANCEL = "/app/clinical/studies/study/cohort/export/cancel";
	public static final String CLINICAL_STUDY_COHORT_EXPORT_DELETE = "/app/clinical/studies/study/cohort/export/delete";

	public static final String CLINICAL_STUDY_SCREENING_COHORTS = "/app/clinical/studies/study/screening/cohorts";
	public static final String clinicalStudyScreeningStatus = "/app/clinical/studies/study/screening/status";
	public static final String clinicalStudyEnrolmentStatus = "/app/clinical/studies/study/enrolment/status";
	public static final String clinicalStudyScreeningPatientDetails = "/app/clinical/studies/study/screening/patientDetails";
	public static final String clinicalStudyPatientFacts = "/app/clinical/studies/study/patient/facts";
	public static final String clinicalStudyScreeningMovePatients = "/app/clinical/studies/study/screening/movePatients";
	public static final String clinicalStudyScreeningSources = "/app/clinical/studies/study/screening/sources";
	public static final String clinicalStudyEnrolment = "/app/clinical/studies/study/enrolment";

	public static final String filterSetEdit = "/app/filterset/editFilter";
	public static final String filterSetImport = "/app/filterset/importFilter";
	public static final String filterSetRemove = "/app/filterset/removeFilter";
	public static final String filterSetReoder = "/app/filterset/reorderFilter";
	public static final String filterSetInit = "/app/filterset/newFilter";
	public static final String filterSetSourceCohort = "/app/filterset/changeSource/cohort";
	public static final String filterSetSourceClinical = "/app/filterset/changeSource/clinical";
	public static final String filterSetRefDate = "/app/filterset/changeRefDate";
	public static final String filterSetStatus = "/app/filterset/statusFilter";
	public static final String filterSetShowPC = "/app/filterset/showPatientCount";
	public static final String filterSetRelease = "/app/filterset/release";
	public static final String filterSetRefresh = "/app/filterset/refresh";
	public static final String filterSetEmpty = "/app/filterset/emptyFilter";

	public static final String filterBlocksSave = "/app/filterblocks/save";
	public static final String filterBlocksDelete = "/app/filterblocks/delete";
	public static final String filterBlocksSearch = "/app/filterblocks/search";
	public static final String filterBlocksExport = "/app/filterblocks/export";

	public static final String trialDesign = "/app/trialDesign";
	public static final String trialDesignAccept = "/app/trialDesign/accept";
	public static final String trialDesignReject = "/app/trialDesign/reject";
	public static final String trialDesignSettings = "/app/trialDesign/settings";
	public static final String TRIAL_DESIGN_SETTINGS_API = "/api/trialDesign/settings";

	public static final String notificationsUnread = "/app/notifications/new";
	public static final String notificationsAll = "/app/notifications/all";
	public static final String notificationsReadSingle = "/app/notifications/{notificationId}/read";

	public static final String features = "/assets/index/js/features";
    public static final String TERMINOLOGY_API_GET_TERM = "/api/terminology/{codeSystemNamespace}/{code}";
    public static final String TERMINOLOGY_API_GET_ROOT = "/api/terminology";
    public static final String TERMINOLOGY_API_SEARCH = "/api/terminology";

	public static final String EHR2EDC_STUDIES = "/app/ehr2edc/studies";
	public static final String EHR2EDC_STUDY = "/app/ehr2edc/studies/{studyId}";

	private static final String SHOW_INSITE_STUDIES_TAB_PROPERTY_KEY = "menubar.showInsiteStudiesTab";


	public static String getHomeURL(Environment env) {
		return WebRoutes.EHR2EDC_STUDIES;
	}

	public static String replacePathVariable(String url, String variableId, Object variableValue) {
		return url.replace("{" + variableId + "}", String.valueOf(variableValue));
	}

	private static boolean isShowInsiteStudiesTab(Environment env) {
		return env.getProperty(SHOW_INSITE_STUDIES_TAB_PROPERTY_KEY, Boolean.class, true);
	}
}