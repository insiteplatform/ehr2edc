import React from 'react';
import StudyPopulatedEvent
    from "../../../../../src/apps/ehr2edc/components/study-populated-event-pane/study-populated-event";
import mockAxios from 'jest-mock-axios';
import * as apiExt from "../../../../../src/api/api-ext"
import readDataFromFile from "../../../../__helper__/sample-data-file";

let onEdcSubjectReferenceChanged;

beforeEach(() => {
    apiExt.getEventPopulationHistory = jest.fn();
    onEdcSubjectReferenceChanged = jest.fn();
});

afterEach(() => {
    mockAxios.reset();
});

describe("StudyPopulatedEvent component", () => {
    test("Render loading state", () => {
        const app = shallow(<StudyPopulatedEvent studyId="study-1" subjectId="subject-1" eventDefinitionId="eventDef-1" onEdcSubjectReferenceChanged={onEdcSubjectReferenceChanged}/>);
        expect(app.find('.search-results-loading').exists()).toBe(true);
        expect(app.find('StudyPopulatedEventOverview').exists()).toBe(false);
        expect(app.find('SearchResultsError').exists()).toBe(false);

        expect(apiExt.getEventPopulationHistory).toHaveBeenCalledWith("study-1", "subject-1", "eventDef-1");
    });
    test("Render fetches edc subject reference and call the call back function onEdcSubjectReferenceChanged", async () => {
        const data = {
            edcSubjectReference: 'edcSubjectReference1'
        };
        letWebMethodReturnData(data, mockAxios.get);
        shallow(<StudyPopulatedEvent studyId="study-1" subjectId="subject-1" eventDefinitionId="eventDef-1"
                                     onEdcSubjectReferenceChanged={onEdcSubjectReferenceChanged}/>);
        await Promise.resolve();

        expect(apiExt.getEventPopulationHistory).toHaveBeenCalledWith("study-1", "subject-1", "eventDef-1");
        expect(mockAxios.get).toHaveBeenCalledWith('/ehr2edc/studies/study-1/subjects/subject-1');
        expect(onEdcSubjectReferenceChanged).toHaveBeenCalledWith('edcSubjectReference1');
    });
    test("Render error state when the history call call fails", async () => {
        letPopulationEventHistoryReturnError();

        const app = shallow(<StudyPopulatedEvent studyId="study-1" subjectId="subject-1" eventDefinitionId="eventDef-1" onEdcSubjectReferenceChanged={onEdcSubjectReferenceChanged}/>);
        await Promise.resolve();
        await Promise.resolve();

        expect(app.find('.search-results-loading').exists()).toBe(false);
        expect(app.find('StudyPopulatedEventOverview').exists()).toBe(false);
        expect(app.find('SearchResultsError').exists()).toBe(true);


        expect(apiExt.getEventPopulationHistory).toHaveBeenCalledWith("study-1", "subject-1", "eventDef-1");
        expect(mockAxios.get).not.toHaveBeenCalledWith('/ehr2edc/studies/study-1/subjects/subject-1/events/populated/event-1');
    });
    test("Render error state when the forms call fails", async () => {
        letPopulationEventHistoryReturnValidData();
        letWebMethodReturnError("Oops, something went wrong", mockAxios.get);

        const app = shallow(<StudyPopulatedEvent studyId="study-1" subjectId="subject-1" eventDefinitionId="eventDef-1" onEdcSubjectReferenceChanged={onEdcSubjectReferenceChanged}/>);
        await Promise.resolve();
        await Promise.resolve();

        expect(app.find('.search-results-loading').exists()).toBe(false);
        expect(app.find('StudyPopulatedEventOverview').exists()).toBe(false);
        expect(app.find('SearchResultsError').exists()).toBe(true);


        expect(apiExt.getEventPopulationHistory).toHaveBeenCalledWith("study-1", "subject-1", "eventDef-1");
        expect(mockAxios.get).toHaveBeenCalledWith('/ehr2edc/studies/study-1/subjects/subject-1/events/populated/event-1');
    });
    test("Render with forms", async () => {
        letPopulationEventHistoryReturnValidData();
        letWebMethodReturnData(readFormData("getEvent-forms-present"), mockAxios.get);
        const app = shallow(<StudyPopulatedEvent studyId="study-1" subjectId="subject-1" eventDefinitionId="eventDef-1"
                                                 onEventChanged={jest.fn()} onEdcSubjectReferenceChanged={onEdcSubjectReferenceChanged}/>);
        await Promise.resolve();
        await Promise.resolve();

        expect(app.find('.search-results-loading').exists()).toBe(false);
        expect(app.find('StudyPopulatedEventOverview').exists()).toBe(true);
        expect(app.find('SearchResultsError').exists()).toBe(false);

        expect(apiExt.getEventPopulationHistory).toHaveBeenCalledWith("study-1", "subject-1", "eventDef-1");
        expect(mockAxios.get).toHaveBeenCalledWith('/ehr2edc/studies/study-1/subjects/subject-1/events/populated/event-1');
    });
    test("Render with no forms", async () => {
        letPopulationEventHistoryReturnValidData();
        letWebMethodReturnData(readFormData("getEvent-forms-empty"), mockAxios.get);

        const app = shallow(<StudyPopulatedEvent studyId="study-1" subjectId="subject-1" eventDefinitionId="eventDef-1"
                                                 onEventChanged={jest.fn()}  onEdcSubjectReferenceChanged={onEdcSubjectReferenceChanged}/>);
        await Promise.resolve();
        await Promise.resolve();

        expect(app.find('.search-results-loading').exists()).toBe(false);
        expect(app.find('StudyPopulatedEventOverview').exists()).toBe(true);
        expect(app.find('SearchResultsError').exists()).toBe(false);


        expect(apiExt.getEventPopulationHistory).toHaveBeenCalledWith("study-1", "subject-1", "eventDef-1");
        expect(mockAxios.get).toHaveBeenCalledWith('/ehr2edc/studies/study-1/subjects/subject-1/events/populated/event-1');
    });
    test("Render with history visible", async () => {
        letPopulationEventHistoryReturnValidData();
        letWebMethodReturnData(readFormData("getEvent-forms-empty"), mockAxios.get);

        const app = shallow(<StudyPopulatedEvent studyId="study-1" subjectId="subject-1" eventDefinitionId="eventDef-1"
                                                 onEventChanged={jest.fn()} historyVisible={true} onEdcSubjectReferenceChanged={onEdcSubjectReferenceChanged}/>);
        await Promise.resolve();
        await Promise.resolve();

        expect(app.find('.search-results-loading').exists()).toBe(false);
        expect(app.find('StudyPopulatedEventOverview').exists()).toBe(true);
        expect(app.find('SearchResultsError').exists()).toBe(false);


        expect(apiExt.getEventPopulationHistory).toHaveBeenCalledWith("study-1", "subject-1", "eventDef-1");
        expect(mockAxios.get).toHaveBeenCalledWith('/ehr2edc/studies/study-1/subjects/subject-1/events/populated/event-1');
    });
    test("Render with history visible and the history details loading", async () => {
        letPopulationEventHistoryReturnValidData();

        const app = shallow(<StudyPopulatedEvent studyId="study-1" subjectId="subject-1" eventDefinitionId="eventDef-1"
                                                 onEventChanged={jest.fn()} historyVisible={true} onEdcSubjectReferenceChanged={onEdcSubjectReferenceChanged}/>);
        await Promise.resolve();

        expect(app.find('.search-results-loading').exists()).toBe(false);
        expect(app.find('SearchResultsError').exists()).toBe(false);
        expect(app.find('StudyPopulatedEventOverview').exists()).toBe(true);
        expect(app.find('StudyPopulatedEventOverview').prop("historyItemLoading")).toBe(true);
        expect(app.find('StudyPopulatedEventOverview').prop("historyItemInError")).toBe(false);

        expect(apiExt.getEventPopulationHistory).toHaveBeenCalledWith("study-1", "subject-1", "eventDef-1");
        expect(mockAxios.get).toHaveBeenCalledWith('/ehr2edc/studies/study-1/subjects/subject-1/events/populated/event-1');
    });
});

describe("Handling events regarding the form item selection", () => {
    async function getStudyEventForms(initialSelection, fileName) {
        letWebMethodReturnData(readFormData(fileName), mockAxios.get);
        const app = shallow(<StudyPopulatedEvent studyId="study-1" subjectId="subject-1" eventDefinitionId="eventDef-1"
                                                 onEventChanged={jest.fn()}
         onEdcSubjectReferenceChanged={onEdcSubjectReferenceChanged}/>);
        await Promise.resolve();
        app.setState({
            selectedFormItems: initialSelection
        });
        app.update();
        return app;
    }

    beforeEach(() => {
        letPopulationEventHistoryReturnValidData();
    });

    test("Handling the onItemSelected event", async () => {
        const app = await getStudyEventForms(["item-3"], "getEvent-forms-present");

        const onItemSelected = app.find("StudyPopulatedEventOverview").props().actions.onItemSelected;
        onItemSelected("item-11");
        app.update();

        expect(app.state("selectedFormItems")).toEqual(["item-3", "item-11"])
    });
    test("Handling the onItemDeselected event", async () => {
        const app = await getStudyEventForms(["item-11", "item-12"], "getEvent-forms-present");

        const onItemDeselected = app.find("StudyPopulatedEventOverview").props().actions.onItemDeselected;
        onItemDeselected("item-12");
        app.update();

        expect(app.state("selectedFormItems")).toEqual(["item-11"])
    });
    test("Handling the onGroupSelected event", async () => {
        const app = await getStudyEventForms(["item-11"], "getEvent-forms-present");

        const onGroupSelected = app.find("StudyPopulatedEventOverview").props().actions.onGroupSelected;
        onGroupSelected("group-2");
        app.update();

        expect(app.state("selectedFormItems")).toEqual(["item-11", "item-3", "item-4", "item-5", "item-6"])
    });
    test("Handling the onGroupDeselected event", async () => {
        const app = await getStudyEventForms(["item-13", "item-3", "item-4", "item-5", "item-6"], "getEvent-forms-present");

        const onGroupDeselected = app.find("StudyPopulatedEventOverview").props().actions.onGroupDeselected;
        onGroupDeselected("group-2");
        app.update();

        expect(app.state("selectedFormItems")).toEqual(["item-13"])
    });
    test("Handling the onFormSelected event", async () => {
        const app = await getStudyEventForms(["item-11"], "getEvent-forms-present");

        const onFormSelected = app.find("StudyPopulatedEventOverview").props().actions.onFormSelected;
        onFormSelected("form-20");
        app.update();

        expect(app.state("selectedFormItems")).toStrictEqual(["item-11", "item-22", "item-23", "item-24", "item-25", "item-26", "item-27", "item-28", "item-30", "item-31", "item-32"])
    });
    test("Handling the onFormDeselected event", async () => {
        const app = await getStudyEventForms(["item-14", "item-22", "item-23", "item-24", "item-25", "item-26", "item-27", "item-28", "item-30", "item-31", "item-32"], "getEvent-forms-present");

        const onFormDeselected = app.find("StudyPopulatedEventOverview").props().actions.onFormDeselected;
        onFormDeselected("form-20");
        app.update();

        expect(app.state("selectedFormItems")).toStrictEqual(["item-14"])
    });

    test("Handling the onItemSelected event with a non-exportable form item", async () => {
        const app = await getStudyEventForms([], "getEvent-form-not-exportable");

        const onItemSelected = app.find("StudyPopulatedEventOverview").props().actions.onItemSelected;
        onItemSelected("item-3");
        app.update();

        expect(app.state("selectedFormItems")).toEqual([])
    });
    test("Handling the onGroupSelected event with a non-exportable form item", async () => {
        const app = await getStudyEventForms([], "getEvent-form-not-exportable");

        const onGroupSelected = app.find("StudyPopulatedEventOverview").props().actions.onGroupSelected;
        onGroupSelected("group-2");
        app.update();

        expect(app.state("selectedFormItems")).toEqual([])
    });
    test("Handling the onFormSelected event with a non-exportable form item", async () => {
        const app = await getStudyEventForms([], "getEvent-form-not-exportable");

        const onFormSelected = app.find("StudyPopulatedEventOverview").props().actions.onFormSelected;
        onFormSelected("form-1");
        app.update();

        expect(app.state("selectedFormItems")).toStrictEqual([])
    });
});

describe("Submitting the form selection", () => {
    let history = [];

    beforeEach(() => {
        history = [];
        const populationHistory = {
            historyItems: [{
                eventId: "populated-event-id-098",
                populationTime: "2019-11-01T14:54:00Z",
                referenceDate: "2015-08-19"
            }]
        };
        letPopulationEventHistoryReturnValidData(populationHistory);
    });

    async function getStudyEventForms(selection) {
        letWebMethodReturnData(readFormData("getEvent-forms-present"), mockAxios.get);
        const app = shallow(<StudyPopulatedEvent studyId="study-1" subjectId="subject-1" eventDefinitionId="eventDef-1"
                                                 eventId="populated-event-id-098"
                                                 onEventChanged={jest.fn()}
                                                 history={history} onEdcSubjectReferenceChanged={onEdcSubjectReferenceChanged}/>);
        await Promise.resolve();
        app.setState({
            selectedFormItems: selection
        });
        app.update();
        return app;
    }

    test("Triggering the onReviewedFormsSend event with an empty selection will send the empty form selection to the back-end", async () => {
        const app = await getStudyEventForms([]);

        const onReviewedFormsSend = app.find("StudyPopulatedEventOverview").props().onReviewedFormsSend;
        onReviewedFormsSend();
        app.update();

        expect(mockAxios.post).toHaveBeenCalledWith('/ehr2edc/studies/study-1/subjects/subject-1/events/submitted',
            {"populatedEventId": "populated-event-id-098", "reviewedForms": []});
    });
    test("Triggering the onReviewedFormsSend event with a single selected item will send the form selection to the back-end", async () => {
        const app = await getStudyEventForms(["item-3"]);

        const onReviewedFormsSend = app.find("StudyPopulatedEventOverview").props().onReviewedFormsSend;
        onReviewedFormsSend();
        app.update();

        expect(mockAxios.post).toHaveBeenCalledWith('/ehr2edc/studies/study-1/subjects/subject-1/events/submitted',
            readSubmitEventData("submit-reviewed-event-single-item"));
    });
    test("Triggering the onReviewedFormsSend event with multiple selected items will send the form selection to the back-end", async () => {
        const app = await getStudyEventForms(["item-3", "item-11", "item-12"]);

        const onReviewedFormsSend = app.find("StudyPopulatedEventOverview").props().onReviewedFormsSend;
        onReviewedFormsSend();
        app.update();

        expect(mockAxios.post).toHaveBeenCalledWith('/ehr2edc/studies/study-1/subjects/subject-1/events/submitted',
            readSubmitEventData("submit-reviewed-event"));
    });
    test("Receiving an error after submitting a form selection will render the errors", async () => {
        const app = await getStudyEventForms(["item-3", "item-11", "item-12"]);
        const error = {
            issues: [{field: "formItems", message: "Duplicate entry"}]
        };
        letWebMethodReturnError(error, mockAxios.post);

        const onReviewedFormsSend = app.find("StudyPopulatedEventOverview").props().onReviewedFormsSend;
        onReviewedFormsSend();
        await Promise.resolve();
        app.update();

        expect(mockAxios.post).toHaveBeenCalledWith('/ehr2edc/studies/study-1/subjects/subject-1/events/submitted',
            readSubmitEventData("submit-reviewed-event"));
        expect(app.find("StudyPopulatedEventOverview").prop("error")).toEqual({response: {data: error}})
    });
    test("Receiving no errors after submitting a form selection will not render any errors", async () => {
        const app = await getStudyEventForms(["item-3", "item-11", "item-12"]);
        letWebMethodReturnData(null, mockAxios.post);

        const onReviewedFormsSend = app.find("StudyPopulatedEventOverview").props().onReviewedFormsSend;
        onReviewedFormsSend();
        await Promise.resolve();
        app.update();

        expect(mockAxios.post).toHaveBeenCalledWith('/ehr2edc/studies/study-1/subjects/subject-1/events/submitted',
            readSubmitEventData("submit-reviewed-event"));
        expect(mockAxios.get).toHaveBeenCalledWith('/ehr2edc/studies/study-1/subjects/subject-1/events/populated/populated-event-id-098');
        expect(app.find("StudyPopulatedEventOverview").prop("error")).toEqual(undefined);
        expect(history).toEqual(["../eventDef-1/populated-event-id-098/reviewed"]);
    });
});

describe("Showing and hiding the populated form history", () => {
    beforeEach(() => {
        [] = [];
        const populationHistory = {
            historyItems: [{
                eventId: "populated-event-id-098",
                populationTime: "2019-11-01T14:54:00Z",
                referenceDate: "2015-08-19"
            }]
        };
        letPopulationEventHistoryReturnValidData(populationHistory);
    });

    async function getStudyEventForms(historyVisible) {
        letWebMethodReturnData(readFormData("getEvent-forms-present"), mockAxios.get);
        const app = mount(<StudyPopulatedEvent studyId="study-1" subjectId="subject-1" eventDefinitionId="eventDef-1"
                                               eventId="populated-event-id-098"
                                               onEventChanged={jest.fn()}
                                               history={[]}
                                               historyVisible={historyVisible} onEdcSubjectReferenceChanged={onEdcSubjectReferenceChanged}/>);
        await Promise.resolve();
        await Promise.resolve();
        app.update();
        return app;
    }

    test("The history is hidden by default", async () => {
        const app = await getStudyEventForms(undefined);

        expect(app.state("historyVisible")).toBe(false);
        expect(app.find('.show-history').exists()).toBe(true);
        expect(app.find('.hide-history').exists()).toBe(false);
    });

    test("Clicking the 'Show population history'-button when the history is hidden will show the history panel", async () => {
        const app = await getStudyEventForms(false);
        app.find(".show-history").simulate("click");

        expect(app.state("historyVisible")).toBe(true);
        expect(app.find('.show-history').exists()).toBe(false);
        expect(app.find('.hide-history').exists()).toBe(true);
    });

    test("Clicking the 'Hide population history'-button when the history is shown will hide the history panel", async () => {
        const app = await getStudyEventForms(true);
        app.find(".hide-history").simulate("click");

        expect(app.state("historyVisible")).toBe(false);
        expect(app.find('.show-history').exists()).toBe(true);
        expect(app.find('.hide-history').exists()).toBe(false);
    });
});

describe("Handling the events regarding the populated events history", () => {

    let history = [];

    beforeEach(() => {
        history = [];
        const populationHistory = {
            historyItems: [{
                eventId: "populated-event-id-098",
                populationTime: "2019-11-01T14:54:00Z",
                referenceDate: "2015-08-19"
            }, {
                eventId: "populated-event-id-099",
                populationTime: "2018-12-26T14:54:00Z",
                referenceDate: "2015-09-15"
            }]
        };
        letPopulationEventHistoryReturnValidData(populationHistory);
    });

    async function getStudyEventForms() {
        letWebMethodReturnData(readFormData("getEvent-forms-present"), mockAxios.get);
        const app = shallow(<StudyPopulatedEvent studyId="study-1" subjectId="subject-1" eventDefinitionId="eventDef-1"
                                                 eventId="populated-event-id-098"
                                                 onEventChanged={jest.fn()}
                                                 history={history}
                                                 historyVisible={true} onEdcSubjectReferenceChanged={onEdcSubjectReferenceChanged}/>);
        await Promise.resolve();
        await Promise.resolve();
        app.update();
        return app;
    }

    test("Selecting another history item re-renders the details part", async () => {
        const app = await getStudyEventForms();

        const onSelectedHistoryItemChanged = app.find('StudyPopulatedEventOverview').props().onSelectedHistoryItemChanged;
        onSelectedHistoryItemChanged("populated-event-id-099");
        app.update();

        expect(app.state("eventId")).toEqual("populated-event-id-099")
    });

    test("Selecting another history item deselects all form items", async () => {
        // Given: a StudyPopulatedEvent with a selected form item
        const app = await getStudyEventForms();
        app.setState({
            selectedFormItems: ["item-1"]
        });

        // When: the selected history item is changed
        const onSelectedHistoryItemChanged = app.find('StudyPopulatedEventOverview').props().onSelectedHistoryItemChanged;
        onSelectedHistoryItemChanged("populated-event-id-099");

        // Then: the form item is deselected
        expect(app.state("selectedFormItems")).toEqual([])
    });
});

function letPopulationEventHistoryReturnValidData(data = readEventPopulationHistoryData("multiple-history-items-response")) {
    const response = {data: data};
    apiExt.getEventPopulationHistory.mockImplementation((subjectId, eventDefinitionId) => Promise.resolve(response));
}

function letPopulationEventHistoryReturnError() {
    const response = {data: "Something went wrong"};
    apiExt.getEventPopulationHistory.mockImplementation((subjectId, eventDefinitionId) => Promise.reject(response));
}

function letWebMethodReturnError(errorMessage, method) {
    let error = {
        response: {
            data: errorMessage
        }
    };
    method.mockImplementation(() => Promise.reject(error));
}

function letWebMethodReturnData(data, method) {
    const response = {data: data};
    method.mockImplementation((url) => Promise.resolve(response));
}

function readEventPopulationHistoryData(file) {
    return readDataFromFile("populatedeventhistorycontroller/" + file + ".json");
}

function readSubmitEventData(file) {
    return readDataFromFile("reviewedeventcontroller/" + file + ".json");
}

function readFormData(file) {
    return readDataFromFile("eventcontroller/" + file + ".json");
}