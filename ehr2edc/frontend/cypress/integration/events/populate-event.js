import moment from 'moment'

describe("Events - Populate Event", () => {

    const populateEventBtn = "populate-event-btn";
    const subjectIdContainer = "subject-id-container";
    const eventIdContainer = "event-id-container";
    const populationGuidance = "population-guidance";
    const dateInputContainer = "date-input-container";

    const observationsPresentGuidance = "This graph shows the number of available clinical observations for the current subject over time. " +
        "Please select a reference date for populating the event on the graph. If this event is a scheduled visit, the selected date should " +
        "match or closely follow the date at which the visit was performed. The system will attempt to extract the defined data points for " +
        "the selected event from the clinical observations available for the selected reference date. This action can be repeated for the " +
        "same event with a different reference date, e.g. when no suitable data could be found for the selected date.";
    const noObservationsGuidance = "Please select a reference date for populating the event on the graph. If this event is a scheduled visit, " +
        "the selected date should match or closely follow the date at which the visit was performed. The system will attempt to extract the " +
        "defined data points for the selected event from the clinical observations available for the selected reference date. This action can " +
        "be repeated for the same event with a different reference date, e.g. when no suitable data could be found for the selected date.";

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

    function aKnownSubject() {
        cy.server().route({
            method: 'GET',
            url: '/ehr2edc/studies/*/subjects/*/event-population-readiness',
            response: 'fx:studysubjectcontroller/geteventpopulationreadiness/isReadyForPopulation.json'
        }).route({
            method: 'GET',
            url: 'ehr2edc/studies/*/subjects/*',
            response: 'fx:studysubjectcontroller/getSubjectInStudy-knownStudy-knownSubject.json'
        }).route({
            method: 'GET',
            url: 'ehr2edc/studies/*/subjects/*/events',
            response: 'fx:studyeventdefinitionscontroller/listEventsForStudy-knownEvents-notPopulated.json'
        });
    }

    function aKnownSubjectWithoutObservations() {
        aKnownSubject();
        cy.server().route(({
            method: 'GET',
            url: `ehr2edc/studies/*/subjects/*/observations`,
            response: 'fx:subjectobservationsummarycontroller/getSubjectObservationSummary-noObservations.json',
        }));
    }

    function aKnownSubjectWithObservations() {
        aKnownSubject();
        cy.server().route(({
            method: 'GET',
            url: `ehr2edc/studies/*/subjects/*/observations`,
            response: 'fx:subjectobservationsummarycontroller/getSubjectObservationSummary.json',
        }));
    }

    beforeEach(() => {
        anApplication();
        aStudy();
    });

    it("Opens the dialog", () => {
        // Given: A known subject without observations
        aKnownSubjectWithoutObservations();

        // And: The Events-view is opened
        cy.visit(`aStudy/subjects/aSubject/events`);

        // When: Clicking the 'Populate Event'-button
        cy.dataElement(populateEventBtn).first().click({force: true});

        // Then: A modal should be visible
        cy.get(".ReactModal__Content").should("be.visible");
    });

    it("Prefills the subject ID", () => {
        // Given: A known subject without observations
        aKnownSubjectWithoutObservations();
        // And: The Events-view is opened
        const subjectId = `aSubject`;
        cy.visit(`aStudy/subjects/${subjectId}/events`);

        // When: Clicking the 'Populate Event'-button
        cy.dataElement(populateEventBtn).first().click({force: true});

        // Then: A modal should be visible
        cy.dataElement(subjectIdContainer).contains(subjectId).should("be.visible");
    });

    it("Prefills the event ID", () => {
        // Given: A known subject without observations
        aKnownSubjectWithoutObservations();
        // And: The Events-view is opened
        const subjectId = `aSubject`;
        cy.visit(`aStudy/subjects/${subjectId}/events`);

        // When: Clicking the 'Populate Event'-button
        cy.dataElement(populateEventBtn).first().click({force: true});

        // Then: A modal should be visible
        cy.dataElement(eventIdContainer).contains("event-1").should("exist");
    });

    it("Provides relevant guidance if no ObservationSummary is available", () => {
        // Given: A known subject without observations
        aKnownSubjectWithoutObservations();
        // And: The Events-view is opened
        cy.visit(`aStudy/subjects/aSubject/events`);

        // When: Clicking the 'Populate Event'-button
        cy.dataElement(populateEventBtn).first().click({force: true});

        // And: There should be guidance text present...
        cy.dataElement(populationGuidance).should('exist');
        // ...explaining the user should select a referencedate
        cy.dataElement(populationGuidance).should(guidance => {
            expect(guidance).to.contain(noObservationsGuidance);
        })
    });

    it("Provides a datepicker if no ObservationSummary is available", () => {
        // Given: A known subject without observations
        aKnownSubjectWithoutObservations();
        // And: The Events-view is opened
        cy.visit(`aStudy/subjects/aSubject/events`);

        // When: Clicking the 'Populate Event'-button
        cy.dataElement(populateEventBtn).first().click({force: true});

        // Then: There should be an input field for the referencedate showing today's date by default...
        const datepickerInput = () => cy.dataElement(dateInputContainer).find("input");
        datepickerInput().should("exist")
            .and("have.value", moment(Date.now()).format("LL"));
        // ... opening a datepicker when selected
        datepickerInput().click();
        cy.get(".bp3-datepicker").should("exist");
        // ... with a selectable month
        cy.get(".bp3-datepicker-month-select > select").select("11");
        // ... and a selectable year
        cy.get(".bp3-datepicker-year-select > select").select("2018");
        // ... and a selectable day
        cy.get(".bp3-datepicker-day-wrapper").contains("10").click();
        // ... which updates the inputfield's value accordingly
        datepickerInput().should("have.value",
            moment(new Date(2018, 11, 10)).format("LL"))
    });

    it("The Datepicker does not allow clearing the selection", () => {
        // Given: A known subject without observations
        aKnownSubjectWithoutObservations();
        // And: The Events-view is opened
        cy.visit(`aStudy/subjects/aSubject/events`);

        // When: Clicking the 'Populate Event'-button
        cy.dataElement(populateEventBtn).first().click({force: true});

        // Then: There should be an input field for the referencedate showing today's date by default...
        const datepickerInput = () => cy.dataElement(dateInputContainer).find("input");
        datepickerInput().should("exist")
            .and("have.value", moment(Date.now()).format("LL"));
        // ... opening a datepicker when selected
        datepickerInput().click();
        cy.get("[aria-selected=true]").click();
        // ... which updates the inputfield's value accordingly
        datepickerInput().should("have.value",
            moment(Date.now()).format("LL"))
    });

    it("Provides relevant guidance if an ObservationSummary is available", () => {
        // Given: A known subject without observations
        aKnownSubjectWithObservations();
        // And: The Events-view is opened
        cy.visit(`aStudy/subjects/aSubject/events`);

        // When: Clicking the 'Populate Event'-button
        cy.dataElement(populateEventBtn).first().click({force: true});

        // Then: There should be guidance text present...
        cy.dataElement(populationGuidance).should('exist');
        // ...explaining the user should select a referencedate
        cy.dataElement(populationGuidance).get("p:first-child").should(guidance => {
            expect(guidance).to.contain(observationsPresentGuidance);
        })
    });

    it("Provides a charted summary overview if an ObservationSummary is available", () => {
        // Given: A known subject without observations
        aKnownSubjectWithObservations();
        // And: The Events-view is opened
        cy.visit(`aStudy/subjects/aSubject/events`);

        // When: Clicking the 'Populate Event'-button
        cy.dataElement(populateEventBtn).first().click({force: true});

        // Then: There should be a chart
        cy.dataElement(dateInputContainer).should("exist");
        cy.dataElement(dateInputContainer).get(".recharts-wrapper").should("exist");
    });
});