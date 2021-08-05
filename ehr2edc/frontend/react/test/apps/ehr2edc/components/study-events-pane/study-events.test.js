'use strict';

import React from 'react';
import renderer from 'react-test-renderer';
import StudyEvents from "../../../../../src/apps/ehr2edc/components/study-events-pane/study-events";
import {StaticRouter} from "react-router-dom";
import mockAxios from 'jest-mock-axios';
import readDataFromFile from "../../../../__helper__/sample-data-file";
import Notification from "../../../../../src/common/components/notification";

const studyId = "STUDY-1";
const subjectId = "SUBJECT-1";

const eventApi = {};
const subjectObservationSummaryApi = {};
const onEdcSubjectReferenceChanged = jest.fn();

function letEventWebserviceReturnError() {
    const error = {
        response: {
            data: {
                "issues": [{
                    "reference": null,
                    "field": null,
                    "message": "Events failed to load."
                }]
            }
        }
    };
    eventApi.getOne = () => Promise.reject(error);
}

function letObservationSummaryWebserviceReturnError() {
    const error = {response: {data: "Observation summary failed to load.", headers: {}}};
    subjectObservationSummaryApi.getOne = () => Promise.reject(error);
}

function letGetEventPopulationReadinessIsReady() {
    const response = {data: readDataFromFile("studysubjectcontroller/geteventpopulationreadiness/isReadyForPopulation.json")};
    StudyEvents.prototype.eventPopulationReadinessProxy = jest.fn();
    StudyEvents.prototype.eventPopulationReadinessProxy.mockImplementationOnce((studyId, subjectId) => Promise.resolve(response));
}

function letGetEventPopulationReadinessIsNotReadyBecauseMigrationRunning() {
    const response = {data: readDataFromFile("studysubjectcontroller/geteventpopulationreadiness/isNotReadyBecauseSubjectMigrationHasStarted.json")};
    StudyEvents.prototype.eventPopulationReadinessProxy = jest.fn();
    StudyEvents.prototype.eventPopulationReadinessProxy.mockImplementationOnce((studyId, subjectId) => Promise.resolve(response));
}

function letGetSubjectMigrationReturnsMigrationIsUndefined() {
    StudyEvents.prototype.eventPopulationReadinessProxy = jest.fn();
    StudyEvents.prototype.eventPopulationReadinessProxy.mockImplementationOnce((studyId, subjectId) => Promise.resolve());
}

function letGetEventPopulationReadinessIsNotReadyBecauseMigrationNotExisting() {
    const response = {data: readDataFromFile("studysubjectcontroller/geteventpopulationreadiness/isNotReadyBecauseSubjectMigrationNotExisting.json")};
    StudyEvents.prototype.eventPopulationReadinessProxy = jest.fn();
    StudyEvents.prototype.eventPopulationReadinessProxy.mockImplementationOnce((subjectId) => Promise.resolve(response));
}

function letWebserviceReturnEventData(filePath) {
    const response = {data: readDataFromFile("studyeventdefinitionscontroller/" + filePath + ".json")};
    eventApi.getOne = () => Promise.resolve(response);
}

function letWebMethodReturnData(data, method) {
    const response = {data: data};
    method.mockImplementationOnce((url) => Promise.resolve(response));
}

function letWebserviceReturnSubjectObservationSummaryData(filePath) {
    const response = {data: readDataFromFile("subjectobservationsummarycontroller/" + filePath)};
    subjectObservationSummaryApi.getOne = () => Promise.resolve(response);
}

describe("Component gets rendered well", () => {
    test("Component in the loading state gets rendered well", async () => {
        letGetEventPopulationReadinessIsReady();
        letWebserviceReturnEventData("listEventsForStudy-noEvents-response");
        letWebserviceReturnSubjectObservationSummaryData("getSubjectObservationSummary-noObservations.json");
        const app = renderer.create(<StudyEvents studyId={studyId} subjectId={subjectId} eventApi={eventApi}
                                                 subjectObservationSummaryApi={subjectObservationSummaryApi}
                                                 onEdcSubjectReferenceChanged={onEdcSubjectReferenceChanged}/>);

        const tree = app.toJSON();
        expect(tree).toMatchSnapshot();
        await Promise.resolve();
    });
    test("Component in the error state gets rendered well", async () => {
        letGetEventPopulationReadinessIsReady();
        letEventWebserviceReturnError();
        letObservationSummaryWebserviceReturnError();
        const app = renderer.create(<StudyEvents studyId={studyId} subjectId={subjectId} eventApi={eventApi}
                                                 subjectObservationSummaryApi={subjectObservationSummaryApi}
                                                 onEdcSubjectReferenceChanged={onEdcSubjectReferenceChanged}/>);

        await Promise.resolve();
        const tree = app.toJSON();
        expect(tree).toMatchSnapshot();

    });
    test("Component in the error state and a custom error message gets rendered well", async () => {
        letGetEventPopulationReadinessIsReady();
        letEventWebserviceReturnError();
        letObservationSummaryWebserviceReturnError();
        const app = renderer.create(<StudyEvents studyId={studyId} subjectId={subjectId} eventApi={eventApi}
                                                 subjectObservationSummaryApi={subjectObservationSummaryApi}
                                                 errorMessage="Test error message"
                                                 onEdcSubjectReferenceChanged={onEdcSubjectReferenceChanged}/>);

        await Promise.resolve();
        const tree = app.toJSON();
        expect(tree).toMatchSnapshot();
    });

    test("Component renders subject migration error when the subject migration is running and they are no event errors", async () => {
        letGetEventPopulationReadinessIsNotReadyBecauseMigrationRunning();
        letWebserviceReturnEventData("listEventsForStudy-knownEvents-response");
        letWebserviceReturnSubjectObservationSummaryData("getSubjectObservationSummary-noObservations.json");
        const app = mount(<StaticRouter>
            <StudyEvents studyId={studyId} subjectId={subjectId} eventApi={eventApi}
                         subjectObservationSummaryApi={subjectObservationSummaryApi}
                         onEdcSubjectReferenceChanged={onEdcSubjectReferenceChanged}/>
        </StaticRouter>);
        await Promise.resolve();
        app.update();
        expect(app.find('.events-error-container').text()).toEqual("The events are currently not ready for data point fetching.");
        expectElementToContainASpinner(app.find('.events-error-container'));
    });
    test("Component does not render subject migration error when the subject migration is ended and they are no event errors", async () => {
        letGetEventPopulationReadinessIsReady();
        letWebserviceReturnEventData("listEventsForStudy-knownEvents-response");
        letWebserviceReturnSubjectObservationSummaryData("getSubjectObservationSummary-noObservations.json");
        const app = mount(<StaticRouter>
            <StudyEvents studyId={studyId} subjectId={subjectId} eventApi={eventApi}
                         subjectObservationSummaryApi={subjectObservationSummaryApi}
                         onEdcSubjectReferenceChanged={onEdcSubjectReferenceChanged}/>
        </StaticRouter>);
        await Promise.resolve();
        app.update();
        expect(app.find('.events-error-container').exists()).not.toEqual(true)
    });
    test("Component does not render subject migration error when the subject migration is running and they are events errors", async () => {
        letGetEventPopulationReadinessIsNotReadyBecauseMigrationRunning();
        letEventWebserviceReturnError();
        letWebserviceReturnSubjectObservationSummaryData("getSubjectObservationSummary-noObservations.json");
        const app = shallow(<StaticRouter>
            <StudyEvents studyId={studyId} subjectId={subjectId} eventApi={eventApi}
                         subjectObservationSummaryApi={subjectObservationSummaryApi}
                         onEdcSubjectReferenceChanged={onEdcSubjectReferenceChanged}/>
        </StaticRouter>);
        await Promise.resolve();
        app.update();
        expect(app.find('.events-error-container').exists()).not.toEqual(true)
    });
    test("Component does not render subject migration error when the subject migration is running and they are no events", async () => {
        letGetEventPopulationReadinessIsNotReadyBecauseMigrationRunning();
        letWebserviceReturnEventData("listEventsForStudy-noEvents-response");
        letWebserviceReturnSubjectObservationSummaryData("getSubjectObservationSummary-noObservations.json");
        const app = mount(<StaticRouter>
            <StudyEvents studyId={studyId} subjectId={subjectId} eventApi={eventApi}
                         subjectObservationSummaryApi={subjectObservationSummaryApi}
                         onEdcSubjectReferenceChanged={onEdcSubjectReferenceChanged}/>
        </StaticRouter>);
        await Promise.resolve();
        app.update();
        expect(app.find('.events-error-container').exists()).not.toEqual(true)
    });
    test("Component does not render subject migration error when the subject migration is undefined", async () => {
        letGetSubjectMigrationReturnsMigrationIsUndefined();
        letWebserviceReturnEventData("listEventsForStudy-knownEvents-response");
        letWebserviceReturnSubjectObservationSummaryData("getSubjectObservationSummary-noObservations.json");
        const app = shallow(
            <StudyEvents studyId={studyId} subjectId={subjectId} eventApi={eventApi}
                         subjectObservationSummaryApi={subjectObservationSummaryApi}
                         onEdcSubjectReferenceChanged={onEdcSubjectReferenceChanged}/>);
        await Promise.resolve();
        app.update();
        expect(app.find('.events-error-container').exists()).not.toEqual(true)
    });
    test("Component with valid events gets rendered well", async () => {
        letGetEventPopulationReadinessIsReady();
        letWebserviceReturnEventData("listEventsForStudy-lotsOfKnownEvents-response");
        letWebserviceReturnSubjectObservationSummaryData("getSubjectObservationSummary-noObservations.json");
        const app = renderer.create(<StaticRouter>
            <StudyEvents studyId={studyId} subjectId={subjectId} eventApi={eventApi}
                         subjectObservationSummaryApi={subjectObservationSummaryApi}
                         onEdcSubjectReferenceChanged={onEdcSubjectReferenceChanged}/>
        </StaticRouter>);
        await Promise.resolve();

        const tree = app.toJSON();
        expect(tree).toMatchSnapshot();
    });
    test("Component with unpopulated events gets rendered well", async () => {
        letGetEventPopulationReadinessIsReady();
        letWebserviceReturnEventData("listEventsForStudy-knownEvents-notPopulated");
        letWebserviceReturnSubjectObservationSummaryData("getSubjectObservationSummary-noObservations.json");
        const app = renderer.create(<StaticRouter>
            <StudyEvents studyId={studyId} subjectId={subjectId} eventApi={eventApi}
                         subjectObservationSummaryApi={subjectObservationSummaryApi}
                         onEdcSubjectReferenceChanged={onEdcSubjectReferenceChanged}/>
        </StaticRouter>);

        await Promise.resolve();
        const tree = app.toJSON();
        expect(tree).toMatchSnapshot();
    });
    test("Component with valid events without forms", async () => {
        letGetEventPopulationReadinessIsReady();
        letWebserviceReturnEventData("listEventsForStudy-knownEvents-no-forms-response");
        letWebserviceReturnSubjectObservationSummaryData("getSubjectObservationSummary-noObservations.json");
        const app = renderer.create(<StaticRouter>
            <StudyEvents studyId={studyId} subjectId={subjectId} eventApi={eventApi}
                         subjectObservationSummaryApi={subjectObservationSummaryApi}
                         onEdcSubjectReferenceChanged={onEdcSubjectReferenceChanged}/>
        </StaticRouter>);

        await Promise.resolve();
        const tree = app.toJSON();
        expect(tree).toMatchSnapshot();
    });
    test("Component with valid events without queries", async () => {
        letGetEventPopulationReadinessIsReady();
        letWebserviceReturnEventData("listEventsForStudy-knownEvents-no-queries-response");
        letWebserviceReturnSubjectObservationSummaryData("getSubjectObservationSummary-noObservations.json");
        const app = renderer.create(<StaticRouter>
            <StudyEvents studyId={studyId} subjectId={subjectId} eventApi={eventApi}
                         subjectObservationSummaryApi={subjectObservationSummaryApi}
                         onEdcSubjectReferenceChanged={onEdcSubjectReferenceChanged}/>
        </StaticRouter>);

        await Promise.resolve();
        const tree = app.toJSON();
        expect(tree).toMatchSnapshot();
    });
    test("Component with no events gets rendered well", async () => {
        letGetEventPopulationReadinessIsReady();
        letWebserviceReturnEventData("listEventsForStudy-noEvents-response");
        letWebserviceReturnSubjectObservationSummaryData("getSubjectObservationSummary-noObservations.json");
        const app = renderer.create(<StudyEvents studyId={studyId} subjectId={subjectId} eventApi={eventApi}
                                                 subjectObservationSummaryApi={subjectObservationSummaryApi}
                                                 onEdcSubjectReferenceChanged={onEdcSubjectReferenceChanged}/>);

        await Promise.resolve();
        const tree = app.toJSON();
        expect(tree).toMatchSnapshot();
    });
    test("Component with no events and a custom empty message gets rendered well", async () => {
        letGetEventPopulationReadinessIsReady();
        letWebserviceReturnEventData("listEventsForStudy-noEvents-response");
        letWebserviceReturnSubjectObservationSummaryData("getSubjectObservationSummary-noObservations.json");
        const app = renderer.create(<StudyEvents studyId={studyId} subjectId={subjectId} eventApi={eventApi}
                                                 subjectObservationSummaryApi={subjectObservationSummaryApi}
                                                 emptyMessage="Test empty message"
                                                 onEdcSubjectReferenceChanged={onEdcSubjectReferenceChanged}/>);

        await Promise.resolve();
        const tree = app.toJSON();
        expect(tree).toMatchSnapshot();
    });
    test("Component with no events and a custom studyPageUrl gets rendered well", async () => {
        letGetEventPopulationReadinessIsReady();
        letWebserviceReturnEventData("listEventsForStudy-noEvents-response");
        letWebserviceReturnSubjectObservationSummaryData("getSubjectObservationSummary-noObservations.json");
        const app = renderer.create(<StudyEvents studyId={studyId}
                                                 subjectId={subjectId}
                                                 eventApi={eventApi}
                                                 subjectObservationSummaryApi={subjectObservationSummaryApi}
                                                 studyPageUrl="/custom"
                                                 onEdcSubjectReferenceChanged={onEdcSubjectReferenceChanged}/>);

        await Promise.resolve();
        const tree = app.toJSON();
        expect(tree).toMatchSnapshot();
    });
    test("Component fetches edc subject reference and call the call back function onEdcSubjectReferenceChanged", async () => {
        letGetEventPopulationReadinessIsReady();
        const data = {
            edcSubjectReference: 'edcSubjectReference1'
        };
        const callback = jest.fn();
        letWebserviceReturnEventData("listEventsForStudy-noEvents-response");
        letWebserviceReturnSubjectObservationSummaryData("getSubjectObservationSummary-noObservations.json");
        letWebMethodReturnData(data, mockAxios.get);
        shallow(<StudyEvents studyId="study-1"
                             subjectId="subject-1"
                             onEdcSubjectReferenceChanged={callback}
                             eventApi={eventApi}
                             subjectObservationSummaryApi={subjectObservationSummaryApi}/>);
        await Promise.resolve();

        expect(mockAxios.get).toHaveBeenCalledWith('/ehr2edc/studies/study-1/subjects/subject-1');
        expect(callback).toHaveBeenCalledWith('edcSubjectReference1');
    });

    function expectElementToContainASpinner(element) {
        expect(element.find('i').exists()).toEqual(true);
        expect(element.find('i').hasClass('fa')).toEqual(true);
        expect(element.find('i').hasClass('fa-circle-o-notch')).toEqual(true);
        expect(element.find('i').hasClass('fa-spin')).toEqual(true);
    }

});

describe("The api has been called correctly", () => {
    afterEach(() => {
        jest.clearAllMocks()
    });

    test("The subjectObservationSummary api has been called for the correct event", () => {
        letGetEventPopulationReadinessIsReady();
        letWebserviceReturnEventData("listEventsForStudy-noEvents-response");
        letWebserviceReturnSubjectObservationSummaryData("getSubjectObservationSummary-noObservations.json");

        const spy = jest.spyOn(subjectObservationSummaryApi, "getOne");
        shallow(<StudyEvents studyId={studyId} eventApi={eventApi}
                             subjectObservationSummaryApi={subjectObservationSummaryApi}
                             subjectId="subject-1"
                             onEdcSubjectReferenceChanged={onEdcSubjectReferenceChanged}/>);

        expect(spy).toHaveBeenCalled();
        expect(spy).toHaveBeenCalledWith("subject-1");
    });

    test("The subject migration api has been called for the correct event", () => {
        letWebserviceReturnEventData("listEventsForStudy-noEvents-response");
        letWebserviceReturnSubjectObservationSummaryData("getSubjectObservationSummary-noObservations.json");

        const spy = jest.spyOn(StudyEvents.prototype, "eventPopulationReadinessProxy");
        shallow(<StudyEvents studyId={studyId} eventApi={eventApi}
                             subjectObservationSummaryApi={subjectObservationSummaryApi}
                             subjectId="subject-1"
                             onEdcSubjectReferenceChanged={onEdcSubjectReferenceChanged}/>);

        expect(spy).toHaveBeenCalled();
        expect(spy).toHaveBeenCalledWith("STUDY-1", "subject-1");
    })
});

describe("Selecting an event", () => {
    test("Fetch Data Points button opens a modal dialog for the matching event", async () => {
        letGetEventPopulationReadinessIsReady();
        letWebserviceReturnEventData("listEventsForStudy-lotsOfKnownEvents-response");
        letWebserviceReturnSubjectObservationSummaryData("getSubjectObservationSummary-noObservations.json");
        const app = mount(<StaticRouter>
            <StudyEvents studyId={studyId} subjectId={subjectId} eventApi={eventApi}
                         subjectObservationSummaryApi={subjectObservationSummaryApi}
                         onEdcSubjectReferenceChanged={onEdcSubjectReferenceChanged}/>
        </StaticRouter>);

        await Promise.resolve();
        app.update();
        app.find(".fetch-event.fetch-event-event-3").simulate('click', {});

        expect(app.find('PopulateEventDialog').props().open).toBe(true);
        expect(app.find('PopulateEventDialog').props().selectedEvent).toEqual("event-3");
    });
});

describe("Handling the onFetchCompleted event", () => {
    async function loadStudyEventsComponent() {
        const app = mount(<StaticRouter>
            <StudyEvents studyId={studyId} subjectId={subjectId} eventApi={eventApi}
                         subjectObservationSummaryApi={subjectObservationSummaryApi}
                         onEdcSubjectReferenceChanged={onEdcSubjectReferenceChanged}/>
        </StaticRouter>);

        await Promise.resolve();
        app.update();
        return app;
    }

    test("When the fetch is completed, the forms get reloaded", async () => {
        letGetEventPopulationReadinessIsReady();
        letWebserviceReturnEventData("listEventsForStudy-lotsOfKnownEvents-response");
        letWebserviceReturnSubjectObservationSummaryData("getSubjectObservationSummary-noObservations.json");
        const spy = jest.spyOn(eventApi, "getOne");
        const app = await loadStudyEventsComponent();

        app.find(".fetch-event.fetch-event-event-3").simulate('click', {});
        app.find("PopulateEventDialog").props().onFetchCompleted(10);
        await Promise.resolve();
        app.update();

        expect(spy).toHaveBeenCalledTimes(2);
        expect(spy).toHaveBeenCalledWith(subjectId);
    });

    test("When the fetch is completed with populated items, a success notification is shown", async () => {
        letGetEventPopulationReadinessIsReady();
        letWebserviceReturnEventData("listEventsForStudy-lotsOfKnownEvents-response");
        letWebserviceReturnSubjectObservationSummaryData("getSubjectObservationSummary-noObservations.json");
        const app = await loadStudyEventsComponent();

        app.find(".fetch-event.fetch-event-event-3").simulate('click', {});
        app.find("PopulateEventDialog").props().onFetchCompleted(10);
        await Promise.resolve();
        app.update();

        expect(app.find(Notification)).toMatchSnapshot();
    });

    test("When the fetch is completed without any populated items, a warning notification is shown", async () => {
        letGetEventPopulationReadinessIsReady();
        letWebserviceReturnEventData("listEventsForStudy-lotsOfKnownEvents-response");
        letWebserviceReturnSubjectObservationSummaryData("getSubjectObservationSummary-noObservations.json");
        const app = await loadStudyEventsComponent();

        app.find(".fetch-event.fetch-event-event-3").simulate('click', {});
        app.find("PopulateEventDialog").props().onFetchCompleted(0);
        await Promise.resolve();
        app.update();

        expect(app.find(Notification)).toMatchSnapshot();
    });
});

describe("Handling the onClear event", () => {
    async function loadStudyEventsComponent() {
        const app = mount(<StaticRouter>
            <StudyEvents studyId={studyId} subjectId={subjectId} eventApi={eventApi}
                         subjectObservationSummaryApi={subjectObservationSummaryApi}
                         onEdcSubjectReferenceChanged={onEdcSubjectReferenceChanged}/>
        </StaticRouter>);

        await Promise.resolve();
        app.update();
        return app;
    }

    test("When the onClear event is thrown, selected form is reset", async () => {
        letGetEventPopulationReadinessIsReady();
        letWebserviceReturnEventData("listEventsForStudy-lotsOfKnownEvents-response");
        letWebserviceReturnSubjectObservationSummaryData("getSubjectObservationSummary-noObservations.json");
        const app = await loadStudyEventsComponent();

        app.find(".fetch-event.fetch-event-event-3").simulate('click', {});
        app.find("PopulateEventDialog").props().onClear();
        app.update();

        expect(app.find("StudyEvents").state().selectedEvent).toEqual("");
    })
});

describe("Set state correctly", () => {
    test("Set state when the subject migration is ended.", async () => {
        letGetEventPopulationReadinessIsReady();
        letWebserviceReturnEventData("listEventsForStudy-noEvents-response");
        letWebserviceReturnSubjectObservationSummaryData("getSubjectObservationSummary-noObservations.json");
        const app = shallow(<StudyEvents studyId={studyId} subjectId={subjectId} eventApi={eventApi}
                                         subjectObservationSummaryApi={subjectObservationSummaryApi}
                                         onEdcSubjectReferenceChanged={onEdcSubjectReferenceChanged}/>);

        await Promise.resolve();
        expect(app.state().populationNotReadyReason).toEqual(null);
        expect(app.state().isReadyForPopulation).toEqual(true);
    });

    test("Set state when the subject migration is started.", async () => {
        letGetEventPopulationReadinessIsNotReadyBecauseMigrationRunning();
        letWebserviceReturnEventData("listEventsForStudy-noEvents-response");
        letWebserviceReturnSubjectObservationSummaryData("getSubjectObservationSummary-noObservations.json");
        const app = shallow(<StudyEvents studyId={studyId} subjectId={subjectId} eventApi={eventApi}
                                         subjectObservationSummaryApi={subjectObservationSummaryApi}
                                         onEdcSubjectReferenceChanged={onEdcSubjectReferenceChanged}/>);

        await Promise.resolve();
        expect(app.state().populationNotReadyReason).toEqual("SUBJECT_MIGRATION_STARTED");
        expect(app.state().isReadyForPopulation).toEqual(false);
    });

    test("Set state.populationNotReadyReason to `undefined` when the subject migration is has failed.", async () => {
        letGetEventPopulationReadinessIsNotReadyBecauseMigrationNotExisting();
        letWebserviceReturnEventData("listEventsForStudy-noEvents-response");
        letWebserviceReturnSubjectObservationSummaryData("getSubjectObservationSummary-noObservations.json");
        const app = shallow(<StudyEvents studyId={studyId} subjectId={subjectId} eventApi={eventApi}
                                         subjectObservationSummaryApi={subjectObservationSummaryApi}
                                         onEdcSubjectReferenceChanged={onEdcSubjectReferenceChanged}/>);

        await Promise.resolve();
        expect(app.state().populationNotReadyReason).toEqual("SUBJECT_MIGRATION_NOT_EXISTING");
        expect(app.state().isReadyForPopulation).toEqual(false);
    })
});

describe('Polling the migration state', () => {
    const migrationStartedresponse = {data: readDataFromFile("studysubjectcontroller/geteventpopulationreadiness/isNotReadyBecauseSubjectMigrationHasStarted.json")};
    const migrationNotExistingresponse = {data: readDataFromFile("studysubjectcontroller/geteventpopulationreadiness/isNotReadyBecauseSubjectMigrationNotExisting.json")};
    const migrationEndedresponse = {data: readDataFromFile("studysubjectcontroller/geteventpopulationreadiness/isReadyForPopulation.json")};
    const migrationFailedResponse = {
        "issues": [{
            "reference": "bb3a62cc-454d-4ba6-bad8-5be23eda4ed7",
            "field": null,
            "message": "Cannot create EventPopulationReadiness for a subject migration with status 'FAILED'"
        }]
    };
    const flushPromises = () => new Promise(setImmediate);
    let getEventPopulationReadiness, fetchObservationSummary;
    const pollingPeriod = 3000;

    beforeEach(() => {
        jest.useFakeTimers();

        getEventPopulationReadiness = StudyEvents.prototype.eventPopulationReadinessProxy = jest.fn();
        fetchObservationSummary = StudyEvents.prototype.fetchSubjectObservationSummary = jest.fn();
    });

    afterEach(() => {
        jest.clearAllMocks();
    });

    test("Keep polling migration state when the migration is started until migration is not running anymore", async () => {
        letGetEventPopulationReadinessReturnResponse(migrationStartedresponse, migrationStartedresponse, migrationEndedresponse);
        letWebserviceReturnEventData("listEventsForStudy-noEvents-response");
        letWebserviceReturnSubjectObservationSummaryData("getSubjectObservationSummary-noObservations.json");

        const app = shallow(<StudyEvents studyId={studyId} subjectId={subjectId} eventApi={eventApi}
                                         subjectObservationSummaryApi={subjectObservationSummaryApi}
                                         onEdcSubjectReferenceChanged={onEdcSubjectReferenceChanged}/>);

        expectPollingRunningStatus(app, 1, 0);
        expectPollingRunningStatus(app, 2, pollingPeriod);
        expectPollingNotRunningStatus(app, 3, pollingPeriod);
        expectPollingNotRunningStatus(app, 3, pollingPeriod);

    });

    test("Keep polling migration state when the migration is not existing until migration is not running anymore", async () => {
        letGetEventPopulationReadinessReturnResponse(migrationNotExistingresponse, migrationNotExistingresponse, migrationEndedresponse);

        const app = shallow(<StudyEvents studyId={studyId} subjectId={subjectId} eventApi={eventApi}
                                         subjectObservationSummaryApi={subjectObservationSummaryApi}
                                         onEdcSubjectReferenceChanged={onEdcSubjectReferenceChanged}/>);

        expectPollingRunningStatus(app, 1, 0);
        expectPollingRunningStatus(app, 2, pollingPeriod);
        expectPollingNotRunningStatus(app, 3, pollingPeriod);
        expectPollingNotRunningStatus(app, 3, pollingPeriod);

    });

    test("Keep polling migration state until migration failed", async () => {
        letGetEventPopulationReadinessReturnResponse(migrationStartedresponse, migrationStartedresponse);
        letGetEventPopulationReadinessReturnErrors(migrationFailedResponse);

        const app = shallow(<StudyEvents studyId={studyId} subjectId={subjectId} eventApi={eventApi}
                                         subjectObservationSummaryApi={subjectObservationSummaryApi}
                                         onEdcSubjectReferenceChanged={onEdcSubjectReferenceChanged}/>);

        expectPollingRunningStatus(app, 1, 0);
        expectPollingRunningStatus(app, 2, pollingPeriod);
        expectPollingErrorStatus(app, 2, pollingPeriod);
        expectPollingErrorStatus(app, 2, pollingPeriod);

    });

    function expectSubjectMigrationIsNotRunning(app) {
        expect(app.state().isSubjectMigrationRunning).toEqual(false);
    }

    function letGetEventPopulationReadinessReturnResponse(...reponses) {
        reponses.forEach(function (response) {
            StudyEvents.prototype.eventPopulationReadinessProxy.mockImplementationOnce((studyId, subjectId) => Promise.resolve(response))
        });
    }

    function letGetEventPopulationReadinessReturnErrors(...errors) {
        errors.forEach(function (error) {
            StudyEvents.prototype.eventPopulationReadinessProxy.mockImplementationOnce((studyId, subjectId) => Promise.reject(error))
        });
    }

    function expectSubjectMigrationStillRunning(app) {
        expect(app.state().isSubjectMigrationRunning).toEqual(true);
    }

    async function expectPollingRunningStatus(app, numberOfCalls, pollingTime) {
        await flushPromises();
        jest.advanceTimersByTime(pollingTime);
        expect(fetchObservationSummary).toHaveBeenCalledTimes(1);
        expect(getEventPopulationReadiness).toHaveBeenCalledTimes(numberOfCalls);
        expectSubjectMigrationStillRunning(app);
    }

    async function expectPollingNotRunningStatus(app, numberOfCalls, pollingTime) {
        await flushPromises();
        jest.advanceTimersByTime(pollingTime);
        expect(fetchObservationSummary).toHaveBeenCalledTimes(2);
        expect(getEventPopulationReadiness).toHaveBeenCalledTimes(numberOfCalls);
        expectSubjectMigrationIsNotRunning(app);
    }

    async function expectPollingErrorStatus(app, numberOfCalls, pollingPeriod) {
        await flushPromises();
        jest.advanceTimersByTime(pollingPeriod);
        expect(fetchObservationSummary).toHaveBeenCalledTimes(1);
        expect(getEventPopulationReadiness).toHaveBeenCalledTimes(numberOfCalls);
        expect(app.state().inError).toEqual(true);
    }
});