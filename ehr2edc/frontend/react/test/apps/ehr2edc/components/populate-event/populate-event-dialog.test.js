import React from 'react';
import PopulateEventDialog from "../../../../../src/apps/ehr2edc/components/populate-event/populate-event-dialog";
import RequestError from "../../../../../src/common/components/error/backend-request-error";
import moment from "moment";
import readDataFromFile from "../../../../__helper__/sample-data-file";

'use strict';

const observationSummary = readDataFromFile("subjectobservationsummarycontroller/getSubjectObservationSummary.json");
const populateEventReponse = readDataFromFile("studyeventscontroller/populateEvent-response.json");

beforeEach(() => {
    Date.now = () => new Date(2019, 2, 3).getTime();
});

describe("Rendering the component", () => {
    test("Rendering in the open state without observation summary items", () => {
        const app = mount(<PopulateEventDialog open={true}/>);
        const dateInput = "[data-cy='date-input-container']";
        expect(app.find(dateInput).childAt(0).getElement().type.name).toBe("DateInput");
    });
    test("Rendering in the open state with an empty list of observation summary items", () => {
        const app = shallow(<PopulateEventDialog open={true} subjectObservationSummary={[]}/>);
        const dateInput = "[data-cy='date-input-container']";
        expect(app.find(dateInput).childAt(0).getElement().type.name).toBe("DateInput");
    });
    test("Rendering in the open state with observation summary items", () => {
        const app = mount(<PopulateEventDialog open={true}
                                               subjectObservationSummary={observationSummary.summaryItems}/>);
        expect(app).toMatchSnapshot();

        const appInProgress = mount(<PopulateEventDialog open={true} inProgress={true}
                                               subjectObservationSummary={observationSummary.summaryItems}/>);
        expect(appInProgress).toMatchSnapshot();
    });
    test("Rendering in the closed state", () => {
        const app = mount(<PopulateEventDialog open={false}/>);
        expect(app).toMatchSnapshot();
    });
});

describe("Handling events", () => {
    test("Clicking the OK button fires the handler", () => {
        Element.prototype.scrollIntoView = jest.fn();
        const handlerSpy = jest.spyOn(PopulateEventDialog.prototype, "handleFetchStart");
        const validationSpy = jest.spyOn(PopulateEventDialog.prototype, "validate");
        const app = mount(<PopulateEventDialog open={true}/>);

        app.find(".modal-ok").simulate("click", {});

        expect(handlerSpy).toHaveBeenCalled();
        expect(validationSpy).toHaveBeenCalled();

        handlerSpy.mockClear();
        validationSpy.mockClear();
    });
    test("Clicking the cancel button when the onClose event is present fires the event", () => {
        let onClear = jest.fn();
        const app = mount(<PopulateEventDialog open={true} onClear={onClear}/>);

        app.find(".modal-cancel").simulate("click", {});

        expect(app.state('referenceDate')).toEqual(moment(Date.now()));
        expect(onClear.mock.calls.length).toEqual(1);
    });
    test("Clicking the cancel button resets the state", () => {
        const app = mount(<PopulateEventDialog open={true}/>);

        app.find(".modal-cancel").simulate("click", {});

        expect(app.state("referenceDate")).toEqual(moment(Date.now()));
        expect(app.state("errors")).toEqual([]);
    });
    test("Changing the calendar date updates the state", () => {
        const app = mount(<PopulateEventDialog open={true}/>);

        app.instance().handleChangeFetchDate(moment("2012-05-05"));

        expect(app.state("referenceDate")).toEqual(moment("2012-05-05"));
    });
});

describe("Validation", () => {  /* studyId, selectedEvent, selectedSubject, referenceDate */
    test("studyId=undefined fails validation", () => {
        const dialog = shallow(<PopulateEventDialog/>);

        dialog.instance().validate();

        expect(dialog.state("errors")).toContain("Study identifier is required");
    });
    test("studyId='' fails validation", () => {
        const dialog = shallow(<PopulateEventDialog studyId={""}/>);

        dialog.instance().validate();

        expect(dialog.state("errors")).toContain("Study identifier is required");
    });
    test("valid studyId does not fail validation", () => {
        const dialog = shallow(<PopulateEventDialog studyId={"studyId"}/>);

        dialog.instance().validate();

        expect(dialog.state("errors")).not.toContain("Study identifier is required");
    });
    test("selectedEvent=undefined fails validation", () => {
        const dialog = shallow(<PopulateEventDialog/>);

        dialog.instance().validate();

        expect(dialog.state("errors")).toContain("Event identifier is required");
    });
    test("selectedEvent='' fails validation", () => {
        const dialog = shallow(<PopulateEventDialog selectedEvent={""}/>);

        dialog.instance().validate();

        expect(dialog.state("errors")).toContain("Event identifier is required");
    });
    test("valid selectedEvent does not fail validation", () => {
        const dialog = shallow(<PopulateEventDialog selectedEvent={"event"}/>);

        dialog.instance().validate();

        expect(dialog.state("errors")).not.toContain("Event identifier is required");
    });
    test("selectedSubject=undefined fails validation", () => {
        const dialog = shallow(<PopulateEventDialog/>);

        dialog.instance().validate();

        expect(dialog.state("errors")).toContain("Subject identifier is required");
    });
    test("selectedSubject='' fails validation", () => {
        const dialog = shallow(<PopulateEventDialog selectedSubject={""}/>);

        dialog.instance().validate();

        expect(dialog.state("errors")).toContain("Subject identifier is required");
    });
    test("valid selectedSubject does not fail validation", () => {
        const dialog = shallow(<PopulateEventDialog selectedSubject={"event"}/>);

        dialog.instance().validate();

        expect(dialog.state("errors")).not.toContain("Subject identifier is required");
    });
    test("valid referenceDate does not fail validation", () => {
        const dialog = shallow(<PopulateEventDialog/>);

        expect(dialog.state("errors")).toEqual([]);

        dialog.setState({
            referenceDate: moment("2013-04-20")
        });
        dialog.instance().validate();

        expect(dialog.state("errors")).not.toContain("Reference date is required");
    });
    test("invalid referenceDate fails validation", () => {
        const dialog = shallow(<PopulateEventDialog/>);

        expect(dialog.state("errors")).toEqual([]);

        dialog.setState({
            referenceDate: moment("2099-99-99")
        });
        dialog.instance().validate();

        expect(dialog.state("errors")).toContain("Reference date is required");
    });
    test("valid referenceDate out of observation summary list does not fail validation", () => {
        const dialog = shallow(<PopulateEventDialog subjectObservationSummary={observationSummary.summaryItems}/>);

        expect(dialog.state("errors")).toEqual([]);

        dialog.setState({
            referenceDate: moment.utc("2013-06-08")
        });
        dialog.instance().validate();

        expect(dialog.state("errors")).not.toContain("Reference date is required");
    });
    test("valid referenceDate, not out of observation summary list does not fail validation", () => {
        const dialog = shallow(<PopulateEventDialog subjectObservationSummary={observationSummary.summaryItems}/>);

        expect(dialog.state("errors")).toEqual([]);

        dialog.setState({
            referenceDate: moment.utc("2014-09-17")
        });
        dialog.instance().validate();

        expect(dialog.state("errors")).toContain("Reference date is required");
    });
    test("Form is valid for non-existent errors", () => {
        const dialog = shallow(<PopulateEventDialog/>);

        expect(dialog.state("errors")).toEqual([]);
        expect(dialog.instance().isValid()).toBe(true);
    });
    test("Form is valid for empty errors", () => {
        const dialog = shallow(<PopulateEventDialog/>);

        dialog.setState({
            errors: []
        });
        expect(dialog.instance().isValid()).toBe(true);
    });
    test("Form is invalid for errors present", () => {
        const dialog = shallow(<PopulateEventDialog/>);

        dialog.setState({
            errors: ["form is invalid"]
        });
        expect(dialog.instance().isValid()).toBe(false);
    })
});

describe("Back-end integration", () => {
    test("Builds correct URL", () => {
        const dialog = shallow(<PopulateEventDialog/>);
        dialog.setProps({
            studyId: "STUDY",
            selectedEvent: "EVENT",
            selectedSubject: "SUBJECT"
        });
        dialog.setState({
            referenceDate: moment(new Date(2013, 3, 20, 10, 40))
        });

        expect(dialog.instance().buildUrl())
            .toEqual('/ehr2edc/studies/STUDY/subjects/SUBJECT/events');
    });
    test("Successful request handling", () => {
        const closeSpy = jest.spyOn(PopulateEventDialog.prototype, "clearDialog");
        const onFetchCompleted = jest.fn();
        const dialog = mount(<PopulateEventDialog selectedSubject="SUBJECT" onFetchCompleted={onFetchCompleted}/>);

        dialog.instance().handleSuccessfulRequest({data: populateEventReponse});

        expect(closeSpy).toHaveBeenCalled();
        expect(onFetchCompleted).toHaveBeenCalledWith(1);

        closeSpy.mockClear();
    });
    test("Error-response handling", () => {
        const response = {response: {data: "A SERVER-SIDE ERROR", headers: {}}};
        const dialog = mount(<PopulateEventDialog selectedSubject="SUBJECT"/>);

        dialog.setState({
            error: response
        });

        expect(dialog.instance().isValid()).toBe(false);

        const errors = shallow(dialog.instance().renderErrors(), { disableLifecycleMethods: true });
        expect(errors.find('RequestError').prop("error")).toEqual(response);
    })
});