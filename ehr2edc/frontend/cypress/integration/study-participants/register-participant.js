
describe("Study - Participants - register", () => {

    const registerParticipantBtn = "register-participant-closed-btn";

    function anApplication() {
        cy.server();
    }

    function aStudy() {
        cy.route({
            method: 'GET',
            url: 'ehr2edc/studies/*',
            response: 'fx:studycontroller/getStudy-knownStudy-response-with-writable-edc.json'
        }).route({
            method: 'GET',
            url: 'ehr2edc/users?**',
            response: 'fx:userscontroller/unassigned-response.json'
        }).route({
            method: 'GET',
            url: 'ehr2edc/patients/domains?**',
            response: 'fx:patientdomaincontroller/patientIdentifierSources.json'
        });
    }

    function someEhrParticipants() {
        cy.server().route({
            method: 'GET',
            url: '/ehr2edc/ehr/patients*',
            response: 'fx:ehrpatientscontroller/availablePatientIdsForStudy-response.json'
        });
    }

    function someEdcSubjects() {
        cy.server().route({
            method: 'GET',
            url: '/ehr2edc/edc/subjects*',
            response: 'fx:edcstudycontroller/availableSubjectIdsForStudy-response.json'
        });
    }

    beforeEach(() => {
        anApplication();
        aStudy();
    });

    it("Opens the dialog", () => {
        // Given: some ehr patients/participants and some edc subjects
        someEhrParticipants();
        someEdcSubjects();

        // And: The study subjects view is opened
        cy.visit(`aStudy/subjects`);

        // When: Clicking the 'Register Participant'-button
        cy.dataElement(registerParticipantBtn).first().click({force: true});

        // Then: A modal should be visible
        cy.get(".ReactModal__Content").should("be.visible");
    });

    it("Has all fields", () => {
        // Given: some ehr patients/participants and some edc subjects
        someEhrParticipants();
        someEdcSubjects();

        // And: The study subjects view is opened
        cy.visit(`aStudy/subjects`);

        // When: Clicking the 'Register Participant'-button
        cy.dataElement(registerParticipantBtn).first().click({force: true});

        const dialog = cy.get(".ReactModal__Content");
        dialog.get(".first-name-field").should("exist");
        dialog.get(".last-name-field").should("exist");
        dialog.get(".birth-date-field").should("exist");
        dialog.get(".subject-identifier-field").should("exist");
        dialog.get(".patient-identifier-source-field").should("exist");
        dialog.get(".patient-identifier-field").should("exist");
        dialog.get(".consent-date-field").should("exist");
    });
});
