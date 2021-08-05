describe("List all studies", () => {

    const ERROR_MESSAGE = "An unexpected error occurred! Please try again or contact support if the issue persists.";
    const CONTACT_ADDRESS = "insite-support@custodix.com";

    const STUDY_ID = "STUDY_ID";
    const STUDY_NAME = "STUDY";
    const STUDY_DESCRIPTION = "DESCRIPTION";

    const NO_STUDIES = "No EHR2EDC studies are found.";

    function anApplication() {
        cy.server();
    }

    function withStudies() {
        cy.route({
            method: 'GET',
            url: 'ehr2edc/studies',
            response: 'fx:studycontroller/listAllStudies-knownStudies-response.json'
        })
    }

    function withoutStudies() {
        cy.route({
            method: 'GET',
            url: 'ehr2edc/studies',
            response: 'fx:studycontroller/listAllStudies-noStudies-response.json'
        })
    }

    function withAnError() {
        cy.route({
            method: 'GET',
            url: 'ehr2edc/studies',
            status: 400,
            response: 'fx:controlleradvice/operationOutcome-systemException.json'
        })
    }

    it("Show an error if something went wrong", () => {
        // Given:
        anApplication();
        withAnError();

        // When: Viewing all studies
        cy.visit("/");

        // Then: An error is shown
        const error = () => cy.dataElement("all-studies-error");
        error().should("exist");
        // And: The error message contains the expected description
        error().contains(ERROR_MESSAGE).should("exist");
        // And: The contact-address is used
        error().get(`a[href*='${CONTACT_ADDRESS}']`).should("exist")
    });

    it("Show an empty list of studies", () => {
        // Given:
        anApplication();
        withoutStudies();

        // When: Viewing all studies
        cy.visit("/");

        // Then: No studies are shown
        const noStudies = () => cy.dataElement("all-studies-empty");
        noStudies().should("exist");
        noStudies().contains(NO_STUDIES).should("exist");
    });

    it("Show a list of studies", () => {
        // Given:
        anApplication();
        withStudies();

        // When: Viewing all studies
        cy.visit("/");

        // Then: The study is rendered
        const studyId = () => cy.dataElement("study-id-col");
        studyId().should("exist");
        // And: Contains a link to the details
        studyId().get(`a[href*=${STUDY_ID}]`).contains(STUDY_NAME).should("exist");
        // And: The description is present
        cy.get("[role=gridcell]").contains(STUDY_DESCRIPTION).should("exist");
    });
});