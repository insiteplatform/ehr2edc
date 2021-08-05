import React from 'react';
import RemoveSubject from "../../../../../src/apps/ehr2edc/components/study-subjects-pane/remove-subject";
import sinon from 'sinon';
import mockAxios from 'jest-mock-axios';

beforeAll(() => {
    Date.now = () => new Date(2019, 2, 3).getTime();
});

describe("Rendering the component", () => {
    const spyOnOpen = sinon.spy(RemoveSubject.prototype, 'open');

    beforeEach(() => {
        spyOnOpen.resetHistory();
    });

    test("Rendering the closed state", () => {
        const app = mount(<RemoveSubject/>);
        expect(app).toMatchSnapshot();
    });

    test("Rendering the opened state", () => {
        const app = shallow(<RemoveSubject/>);
        app.instance().setState({
            open: true,
            studyId: "SID_001",
            subjectId: "SUBJ_042",
        });
        app.update();
        expect(app).toMatchSnapshot();
    });
});

describe("Interacting with the component", () => {
    test("Cancelling the opened modal", () => {
        const app = mount(<RemoveSubject/>);
        app.instance().setState({
            open: true,
            studyId: "SID_001",
            subjectId: "SUBJ_042",
        });
        app.update();
        expect(app.state().open).toEqual(true);
        app.find(".modal-cancel").simulate("click", {});
        app.update();
        expect(app.state().open).toEqual(false);
    });
    test("Closing the opened modal", () => {
        const app = mount(<RemoveSubject/>);
        app.instance().setState({
            open: true,
            studyId: "SID_001",
            subjectId: "SUBJ_042",
        });
        app.update();
        app.find(".modal-close-icon").simulate("click", {});
        app.update();
        expect(app.state().open).toEqual(false);
    });
});

describe("Handling server-side responses", () => {
    let onPatientDeregistered;
    let app;

    let url;
    let request;

    beforeEach(() => {
        onPatientDeregistered = jest.fn();
        app = mount(<RemoveSubject onPatientDeregistered={onPatientDeregistered}/>);
        app.instance().setState({
            open: true,
            studyId: "SID_001",
            subjectId: "SUBJ_042",
        });
        app.update();
        url = `/ehr2edc/studies/SID_001/subjects/SUBJ_042`;
        request = {
            data: {
                dataCaptureStopReason: "CONSENT_RETRACTED",
                endDate: new Date(Date.now()),
                studyId: "SID_001",
                subjectId: "SUBJ_042"
            }
        };
    });

    function letWebserviceSucceed() {
        mockAxios.delete.mockImplementation(() => Promise.resolve());
    }

    function letWebserviceReturnError(error) {
        mockAxios.delete.mockImplementation(() => Promise.reject(error));
    }

    test("Handling a server side error", async () => {
        Element.prototype.scrollIntoView = jest.fn();
        let error = {
            response: {
                data: {
                    "issues": [{
                        "reference": null,
                        "field": null,
                        "message": "A SERVER SIDE ERROR"
                    }]
                }
            }
        };
        letWebserviceReturnError(error);
        app.find(".modal-ok").simulate("click", {});
        await Promise.resolve();

        app.update();

        expect(app.state("error")).toEqual(error);
        expect(mockAxios.delete).toHaveBeenCalledWith(url, request);
        expect(onPatientDeregistered.mock.calls.length).toEqual(0);
        expect(app).toMatchSnapshot();
    });

    test("After successful registration, the onPatientRegistered gets executed", async () => {
        letWebserviceSucceed();
        app.find(".modal-ok").simulate("click", {});
        await Promise.resolve();

        app.update();

        expect(app.state("error")).toEqual(undefined);
        expect(mockAxios.delete).toHaveBeenCalledWith(url, request);
        expect(onPatientDeregistered.mock.calls.length).toEqual(1);
        expect(app).toMatchSnapshot();
    });
});
