package eu.ehr4cr.workbench.local;

public class WebViews {
	public static final String index = "/views/index/index.jsp";
	public static final String demoUnavailable = "/views/index/global/demo-unavailable.jsp";
	public static final String about = "/views/index/global/about.jsp";
	public static final String error = "/views/index/global/error.jsp";

	public static final String login = "/views/auth/login.jsp";
	public static final String registerAccount = "/views/auth/register.jsp";
	public static final String recoverAccount = "/views/auth/recover.jsp";
	public static final String completeInvitation = "/views/auth/completeInvitation.jsp";
	public static final String completeRecovery = "/views/auth/completeRecovery.jsp";
	public static final String unauthorised = "/views/auth/unauthorised.jsp";
	public static final String manageMembers = "/views/index/members/members.jsp";
	public static final String myAccount = "/views/index/account/myAccount.jsp";

	public static final String studies = "/views/index/studies/studies.jsp";
	public static final String study = "/views/index/study/study.jsp";
	public static final String feasibilityDashboard = "/views/index/feasibility/feasibilitydashboard.jsp";
	public static final String feasibilityQueryOverview = "/views/index/feasibility/feasibilitystudydetails.jsp";
	public static final String feasibilityQueryDetails = "/views/index/feasibility/feasibilityquerydetails.jsp";

	public static final String cohortstudies = "/views/index/cohort/cohortstudies.jsp";
	public static final String cohortstudy = "/views/index/cohort/cohortstudy.jsp";
	public static final String cohortdetails = "/views/index/cohort/cohortdetails.jsp";
	public static final String cohortbuilder = "/views/index/cohort/cohortbuilder.jsp";
	public static final String cohortsave = "/views/index/cohort/cohortsave.jsp";
	public static final String cohortanalytics = "/views/index/cohort/cohortanalytics.jsp";
	public static final String cohortExport = "/views/index/cohort/cohortexport.jsp";
	public static final String cohortDefinition = "/views/index/cohort/cohortDefinition.jsp";

	public static final String clinicalStudies = "/views/index/clinical/clinicalstudies.jsp";
	public static final String clinicalStudy = "/views/index/clinical/clinicalstudy.jsp";
	public static final String clinicalOverviewRequest = "/views/index/clinical/tabs/study-overview-request.jsp";
	public static final String clinicalOverviewRecruiting = "/views/index/clinical/tabs/study-overview-recruiting.jsp";
	public static final String clinicalProtocol = "/views/index/clinical/tabs/study-protocol.jsp";
	public static final String clinicalPIAssignment = "/views/index/clinical/tabs/study-assign-investigator.jsp";
	public static final String clinicalRecruiting = "/views/index/clinical/tabs/study-recruiting.jsp";
	public static final String clinicalCoarseFiltering = "/views/index/clinical/tabs/study-coarse-filtering.jsp";
	public static final String clinicalScreening = "/views/index/clinical/patient-screening.jsp";
	public static final String clinicalScreeningManual = "/views/index/clinical/patient-screening-manual.jsp";
	public static final String clinicalScreeningDetails = "/views/index/clinical/patient-screening-details.jsp";
	public static final String clinicalEditScreening = "/views/index/clinical/edit-screening-filters.jsp";
	public static final String clinicalPatientEnrolment = "/views/index/clinical/patient-enrolment.jsp";
	public static final String CLINICAL_COHORT_EXPORT = "/views/index/clinical/cohort-export.jsp";

	public static final String analyses = "/views/index/analysis/analyses.jsp";
	public static final String analysisResult = "/views/index/analysis/analysisresult.jsp";
	public static final String analysisCombinedResults = "/views/index/analysis/analysiscombinedresults.jsp";

	public static final String trialDesignOverview = "/views/index/trialDesign/trialdesignoverview.jsp";
	public static final String trialDesignSettings = "thymeleaf/trialDesign/trialdesignsettings";

	public static final String EHR2EDC_STUDIES = "thymeleaf/ehr2edc/studies";
	public static final String EHR2EDC_STUDY = "thymeleaf/ehr2edc/study";
}