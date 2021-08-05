const onInvestigatorAddSpy = jest.fn();
const onInvestigatorUnassignSpy = jest.fn();
const onEdcSubjectReferenceChangedSpy = jest.fn();
const onRefreshSpy = jest.fn();

export const state = {
    studyPageUrl: "/app/ehr2edc/studies",
    errorMessage: "EHR2EDC Study failed to load.",
    studyApi: {},
    eventApi: {
        getOne: () => Promise.resolve()
    },
    potentialInvestigatorApi: {},
    patientRegistrationApi: {},
    investigatorApi: {},
    patientIdentifierSourcesApi: {},
    subjectObservationSummaryApi: {
        getOne: () => Promise.resolve()
    },
    subjectMigrationApi: {
        getOne: () => Promise.resolve()
    },
    potentialInvestigators: [],
    isStudyLoading: false,
    isPotentialInvestigatorsLoading: false,
    isPatientIdentifierSourcesLoading: false,
    inError: false,
    showDetails: false,
    study: {
        id: "EHRTED14856(DEV)",
        name: "EHRTED14856(DEV)",
        description: "",
        investigators: [],
        subjects: [],
        permissions: {
            canSubjectsBeViewed: true,
            canSubjectsBeAdded: true,
            canSendToEDC: true
        }
    },
    populateEventActionText: "Populate Event",
    patientIdentifierSources: [
        "ES_12OCTUBRE_EHR",
        "ES_12OCTUBRE_IUP",
        "ES_12OCTUBRE_COD"
    ],
    contactAddress: "insite-dev-support@custodix.com",
    breadcrumbParams: {
        edcSubjectReference: ""
    },
    onInvestigatorAdd: onInvestigatorAddSpy,
    onInvestigatorUnassign: onInvestigatorUnassignSpy,
    onEdcSubjectReferenceChanged: onEdcSubjectReferenceChangedSpy,
    onRefresh: onRefreshSpy
};