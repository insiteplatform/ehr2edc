import React from 'react';
import renderer from 'react-test-renderer';
import StudyReviewedEvent
    from "../../../../../src/apps/ehr2edc/components/study-reviewed-event-pane/study-reviewed-event";
import mockAxios from 'jest-mock-axios';
import {StaticRouter} from "react-router-dom";
import * as apiExt from "../../../../../src/api/api-ext"
import readDataFromFile from "../../../../__helper__/sample-data-file";

const samplesFolder = "reviewedeventcontroller/";

afterEach(() => {
    mockAxios.reset();
});

describe("Fetching the subject", () => {
    test("The component calls the callback function when the subject call succeeds", async () => {
        letSubjectReturnValidData();
        const onEdcSubjectReferenceChanged = jest.fn();
        shallow(<StudyReviewedEvent studyId="study-1" subjectId="subject-1" eventDefinitionId="eventDef-1"
                                    eventId="event-1" onEdcSubjectReferenceChanged={onEdcSubjectReferenceChanged}/>);
        await Promise.resolve();

        expect(apiExt.getSubject).toHaveBeenCalledWith('study-1', 'subject-1');
        expect(onEdcSubjectReferenceChanged).toHaveBeenCalledWith('edcSubjectReference1');
    });
    test("The component doesn't call the callback function when the subject call fails", async () => {
        letSubjectReturnError();
        const onEdcSubjectReferenceChanged = jest.fn();
        shallow(<StudyReviewedEvent studyId="study-1" subjectId="subject-1" eventDefinitionId="eventDef-1"
                                    eventId="event-1" onEdcSubjectReferenceChanged={onEdcSubjectReferenceChanged}/>);
        await Promise.resolve();

        expect(apiExt.getSubject).toHaveBeenCalledWith('study-1', 'subject-1');
        expect(onEdcSubjectReferenceChanged).not.toHaveBeenCalled();
    });
});

describe("Rendering the history listing", () => {

    let onEdcSubjectReferenceChanged;

    beforeEach(() => {
        apiExt.getAllSubmittedEvents = jest.fn();
        apiExt.getSubmittedEvent = jest.fn();
        onEdcSubjectReferenceChanged = jest.fn();

        letSubjectReturnValidData();
    });

    async function getReviewedFormWithSubject() {
        const app = renderer.create(<StaticRouter>
            <StudyReviewedEvent studyId="study-1" subjectId="subject-1" eventDefinitionId="eventDef-1"
                                eventId="event-1" onEdcSubjectReferenceChanged={onEdcSubjectReferenceChanged}/>
        </StaticRouter>);
        await Promise.resolve();
        return app;
    }

    test("The history listing gets rendered in a loading state when the back-end hasn't responded yet", async () => {
        const app = shallow(<StaticRouter>
            <StudyReviewedEvent studyId="study-1" subjectId="subject-1" eventDefinitionId="eventDef-1"
                                eventId="event-1" onEdcSubjectReferenceChanged={onEdcSubjectReferenceChanged}/>
        </StaticRouter>);

        expect(app).toMatchSnapshot();
    });

    test("The history listing gets rendered in an error state when the back-end returns an error", async () => {
        getSubmittedEventsShouldReturnError("Something went wrong");
        const app = await getReviewedFormWithSubject();

        expect(app.toJSON()).toMatchSnapshot();

        expect(apiExt.getSubject).toHaveBeenCalledWith('study-1', 'subject-1');
        expect(apiExt.getAllSubmittedEvents).toHaveBeenCalledWith('study-1', 'subject-1', 'event-1');
        expect(apiExt.getSubmittedEvent).not.toHaveBeenCalled();
    });

    test("The history listing gets rendered when the back-end returns history items", async () => {
        getSubmittedEventsShouldReturnData(readDataFromFile(samplesFolder + "submittedFormHistory-present.json"));
        getSubmittedEventShouldReturnData(readDataFromFile(samplesFolder + "getSubmittedEvent.json"));
        const app = await getReviewedFormWithSubject();

        await Promise.resolve();
        expect(app.toJSON()).toMatchSnapshot();

        expect(apiExt.getSubject).toHaveBeenCalledWith('study-1', 'subject-1');
        expect(apiExt.getAllSubmittedEvents).toHaveBeenCalledWith('study-1', 'subject-1', 'event-1');
        expect(apiExt.getSubmittedEvent).toHaveBeenCalled();
    });

    test("The history listing gets rendered when the back-end returns no history items", async () => {
        getSubmittedEventsShouldReturnData(readDataFromFile(samplesFolder + "submittedFormHistory-empty.json"));
        const app = await getReviewedFormWithSubject();

        await Promise.resolve();
        expect(app.toJSON()).toMatchSnapshot();

        expect(apiExt.getSubject).toHaveBeenCalledWith('study-1', 'subject-1');
        expect(apiExt.getAllSubmittedEvents).toHaveBeenCalledWith('study-1', 'subject-1', 'event-1');
        expect(apiExt.getSubmittedEvent).not.toHaveBeenCalled();
    });
});

describe("Rendering the history details", () => {
    let onEdcSubjectReferenceChanged;

    beforeEach(() => {
        apiExt.getAllSubmittedEvents = jest.fn();
        apiExt.getSubmittedEvent = jest.fn();
        onEdcSubjectReferenceChanged = jest.fn();

        letSubjectReturnValidData();
        getSubmittedEventsShouldReturnData(readDataFromFile(samplesFolder + "submittedFormHistory-present.json"));
        getSubmittedEventShouldReturnData(readDataFromFile(samplesFolder + "getSubmittedEvent.json"));
    });

    async function getReviewedFormWithSubjectAndHistory() {
        const app = renderer.create(<StaticRouter>
            <StudyReviewedEvent studyId="study-1" subjectId="subject-1" eventDefinitionId="eventDef-1"
                                eventId="event-1" onEdcSubjectReferenceChanged={onEdcSubjectReferenceChanged}/>
        </StaticRouter>);
        await Promise.resolve();
        await Promise.resolve();
        return app;
    }

    test("The history details get rendered in an error state when the back-end returns an error", async () => {
        getSubmittedEventShouldReturnError("Something went wrong");
        const app = await getReviewedFormWithSubjectAndHistory();

        await Promise.resolve();
        expect(app.toJSON()).toMatchSnapshot();

        expect(apiExt.getSubject).toHaveBeenCalled();
        expect(apiExt.getAllSubmittedEvents).toHaveBeenCalled();
        expect(apiExt.getSubmittedEvent).toHaveBeenCalledWith("study-1", "subject-1", 'edc9bfbc-ec43-4894-bc1c-c0871b935407');
    });

    test("The history details get rendered when the back-end returns valid details", async () => {
        getSubmittedEventShouldReturnData(readDataFromFile(samplesFolder + "getSubmittedEvent.json"));
        const app = await getReviewedFormWithSubjectAndHistory();

        await Promise.resolve();
        expect(app.toJSON()).toMatchSnapshot();

        expect(apiExt.getSubject).toHaveBeenCalled();
        expect(apiExt.getAllSubmittedEvents).toHaveBeenCalled();
        expect(apiExt.getSubmittedEvent).toHaveBeenCalledWith("study-1", "subject-1", 'edc9bfbc-ec43-4894-bc1c-c0871b935407');
    });

    test("The history details get rendered when the back-end returns valid details, not containing a populator", async () => {
        getSubmittedEventShouldReturnData(readDataFromFile(samplesFolder + "getSubmittedEventNoPopulator.json"));
        const app = await getReviewedFormWithSubjectAndHistory();

        await Promise.resolve();
        expect(app.toJSON()).toMatchSnapshot();

        expect(apiExt.getSubject).toHaveBeenCalled();
        expect(apiExt.getAllSubmittedEvents).toHaveBeenCalled();
        expect(apiExt.getSubmittedEvent).toHaveBeenCalledWith("study-1", "subject-1", 'edc9bfbc-ec43-4894-bc1c-c0871b935407');
    });
});

describe("Changing the selected history item", () => {
    let onEdcSubjectReferenceChanged;

    beforeEach(() => {
        apiExt.getAllSubmittedEvents = jest.fn();
        apiExt.getSubmittedEvent = jest.fn();
        onEdcSubjectReferenceChanged = jest.fn();

        letSubjectReturnValidData();
        getSubmittedEventsShouldReturnData(readDataFromFile(samplesFolder + "submittedFormHistory-present.json"));
        getSubmittedEventShouldReturnData(readDataFromFile(samplesFolder + "getSubmittedEvent.json"));
    });

    async function getReviewedFormWithDetails() {
        const app = mount(<StaticRouter>
            <StudyReviewedEvent studyId="study-1" subjectId="subject-1" eventDefinitionId="eventDef-1"
                                eventId="event-1" onEdcSubjectReferenceChanged={onEdcSubjectReferenceChanged}/>
        </StaticRouter>);
        await Promise.resolve();
        await Promise.resolve();
        app.update();
        return app;
    }

    test("Clicking a history item in the list loads the details of the selected item", async () => {
        const app = await getReviewedFormWithDetails();

        app.find("li:not(.is-active)").simulate("click");
        await Promise.resolve();
        app.update();

        expect(app).toMatchSnapshot();
        expect(apiExt.getSubmittedEvent).toHaveBeenCalledWith("study-1", "subject-1", 'eced7cb3-c74e-42c7-b702-1f4810618968');
    });
});

describe("Showing the visibility of the history listing panel", () => {
    let onEdcSubjectReferenceChanged;

    beforeEach(() => {
        apiExt.getAllSubmittedEvents = jest.fn();
        apiExt.getSubmittedEvent = jest.fn();
        onEdcSubjectReferenceChanged = jest.fn();

        letSubjectReturnValidData();
        getSubmittedEventsShouldReturnData(readDataFromFile(samplesFolder + "submittedFormHistory-present.json"));
        getSubmittedEventShouldReturnData(readDataFromFile(samplesFolder + "getSubmittedEvent.json"));
    });

    async function getReviewedFormWithDetails(historyVisible) {
        const app = mount(<StaticRouter>
            <StudyReviewedEvent studyId="study-1" subjectId="subject-1" eventDefinitionId="eventDef-1" eventId="event-1"
                                historyVisible={historyVisible}
                                onEdcSubjectReferenceChanged={onEdcSubjectReferenceChanged}/>
        </StaticRouter>);
        await Promise.resolve();
        await Promise.resolve();
        await Promise.resolve();
        app.update();
        return app;
    }

    test("The history listing is shown by default", async () => {
        const app = await getReviewedFormWithDetails(undefined);
        expect(app).toMatchSnapshot();
    });

    test("When the listing is shown, the user can click to hide the history listing", async () => {
        const app = await getReviewedFormWithDetails(true);

        app.find(".hide-history").simulate("click");
        app.update();

        expect(app).toMatchSnapshot();
    });

    test("When the listing is hidden, the user can click to show the history listing", async () => {
        const app = await getReviewedFormWithDetails(false);

        app.find(".show-history").simulate("click");
        app.update();

        expect(app).toMatchSnapshot();
    });
});

function letSubjectReturnValidData() {
    const response = {data: {edcSubjectReference: 'edcSubjectReference1'}};
    apiExt.getSubject = jest.fn();
    apiExt.getSubject.mockImplementation((studyId, subjectId) => Promise.resolve(response));
}

function letSubjectReturnError() {
    const error = {response: {data: "Something went wrong"}};
    apiExt.getSubject = jest.fn();
    apiExt.getSubject.mockImplementation((studyId, subjectId) => Promise.reject(error));
}

function getSubmittedEventsShouldReturnData(data) {
    const response = {data: data};
    apiExt.getAllSubmittedEvents.mockImplementation((subjectId, eventId) => Promise.resolve(response));
}

function getSubmittedEventsShouldReturnError(data) {
    const error = {response: {data: data}};
    apiExt.getAllSubmittedEvents.mockImplementation((subjectId, eventId) => Promise.reject(error));
}

function getSubmittedEventShouldReturnData(data) {
    const response = {data: data};
    apiExt.getSubmittedEvent.mockImplementation((subjectId, submittedEventId) => Promise.resolve(response));
}

function getSubmittedEventShouldReturnError(data) {
    const error = {response: {data: data}};
    apiExt.getSubmittedEvent.mockImplementation((subjectId, submittedEventId) => Promise.reject(error));
}