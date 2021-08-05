import React from 'react';
import AddSubject from "../../../../../src/apps/ehr2edc/components/study-subjects-pane/add-subject";
import mockAxios from 'jest-mock-axios';

beforeAll(() => {
    Element.prototype.scrollIntoView = jest.fn();
    Date.now = () => new Date(2019, 2, 3).getTime();
});
afterEach(() => {
    jest.clearAllMocks();
    mockAxios.reset();
});

const fs = require('fs');
const path = require("path");
const studySubjectControllerFolder = "../../../../../../../infrastructure/web/src/test/resources/samples/studysubjectcontroller/";
const ehrPatientsControllerFolder = "../../../../../../../infrastructure/web/src/test/resources/samples/ehrpatientscontroller/";

const patientIdentifierSources = ["HIS_01"];

describe("Rendering the component", () => {
    test("The component is disabled by default", () => {
        const app = shallow(<AddSubject patientIdentifierSources={patientIdentifierSources}/>);
        expect(app).toMatchSnapshot();
    });
    test("The component is closed when enabled and open is unspecified", () => {
        const app = shallow(<AddSubject enabled={true}/>);
        expect(app.state("open")).toBe(false);
    });
});

describe("Switching between the open and closed state", () => {
    test("Clicking the add button puts the component in the open state", () => {
        const app = mount(<AddSubject enabled={true} patientIdentifierSources={patientIdentifierSources}/>);

        app.find(".open-add-subject-form").simulate("click", {});

        expect(app.state("open")).toBe(true);
    });
    test("Clicking the cancel button puts the component in the closed state", () => {
        const app = mount(<AddSubject enabled={true} patientIdentifierSources={patientIdentifierSources}/>);
        app.find(".open-add-subject-form").simulate("click");
        app.update();

        app.find(".modal-cancel").simulate("click", {});

        expect(app.state("open")).toBe(false);
    });
    test("Clicking the close icon puts the component in the closed state", () => {
        const app = mount(<AddSubject enabled={true} patientIdentifierSources={patientIdentifierSources}/>);
        app.find(".open-add-subject-form").simulate("click");
        app.update();

        app.find(".modal-close-icon").simulate("click", {});

        expect(app.state("open")).toBe(false);
    });
});

describe("Client-side validation", () => {
    const patientRegistrationApi = {
        put: () => Promise.resolve()
    };

    function fillInFormCompletely(app) {
        app.find(".first-name-field").simulate("change", {target: {value: "first-name"}});
        app.find(".last-name-field").simulate("change", {target: {value: "last-name"}});
        app.find("input.birth-date-field").simulate("change", {target: {value: new Date(2000, 6, 17)}});
        app.find(".subject-identifier-field").simulate("change", {target: {value: "subject-id"}});
        app.find(".patient-identifier-field").simulate("change", {target: {value: "patient-id"}});
        app.find("input.consent-date-field").simulate("change", {target: {value: new Date(2010, 6, 17)}});
    }

    test("Pressing ok without filling in the form displays error messages", () => {
        const app = mount(<AddSubject enabled={true} patientIdentifierSources={patientIdentifierSources}/>);
        app.find(".open-add-subject-form").simulate("click");
        app.update();

        app.find(".modal-ok").simulate("click", {});
        app.update();

        expect(app.state("errorMessages")).toHaveLength(2);
        expect(app.state("errorMessages")).toContain("Patient identifier is required", "Subject identifier is required");
    });

    test("Pressing ok after filling in the form completely displays no error messages", () => {
        const app = mount(<AddSubject enabled={true} patientRegistrationApi={patientRegistrationApi}
                                      studyId="study-id"
                                      patientIdentifierSources={patientIdentifierSources}/>);
        app.setState({
            fetchingSubjectIds: false
        });
        app.find(".open-add-subject-form").simulate("click");
        app.update();

        fillInFormCompletely(app);
        app.find(".modal-ok").simulate("click", {});
        app.update();

        expect(app.state("errorMessages")).toHaveLength(0);
    });

    test("Leaving out the subject identifier adds an error message", () => {
        const app = mount(<AddSubject enabled={true} patientIdentifierSources={patientIdentifierSources}/>);
        app.find(".open-add-subject-form").simulate("click");
        app.update();

        fillInFormCompletely(app);
        app.find(".subject-identifier-field").simulate("change", {target: {value: ""}});
        app.find(".modal-ok").simulate("click", {});
        app.update();

        expect(app.state("errorMessages")).toContain("Subject identifier is required");
    });
    test("Leaving out the patient identifier adds an error message", () => {
        const app = mount(<AddSubject enabled={true} patientIdentifierSources={patientIdentifierSources}/>);
        app.find(".open-add-subject-form").simulate("click");
        app.update();

        fillInFormCompletely(app);
        app.find(".patient-identifier-field").simulate("change", {target: {value: ""}});
        app.find(".modal-ok").simulate("click", {});
        app.update();

        expect(app.state("errorMessages")).toContain("Patient identifier is required");
    });
    test("Selecting another patient identifier source won't result in an error", () => {
        const patientIdentifierSources = ["HIS_01", "source-2", "source-3"];
        const app = mount(<AddSubject enabled={true} patientIdentifierSources={patientIdentifierSources}
                                      patientRegistrationApi={patientRegistrationApi}/>);
        app.find(".open-add-subject-form").simulate("click");
        app.update();

        fillInFormCompletely(app);
        app.find(".patient-identifier-source-field").simulate("change", {target: {value: "source-3"}});
        app.find(".modal-ok").simulate("click", {});
        app.update();

        expect(app.state("errorMessages")).toEqual([]);
        expect(app.state("patientIdentifierSource")).toBe("source-3");
    });
});

describe("Handling server-side responses", () => {
    const DATE_OF_CONSENT = new Date("2019-04-12T00:00:00Z");
    const PATIENT_ID = "PID_01";
    const STUDY_ID = "SID_01";
    const EDC_SUBJECT_REFERENCE = "SUB_01_REF";
    const PATIENT_ID_SRC = "HIS_01";
    const patientRegistrationApi =
        {
            shouldError: false,
            error: {},
            registerSubjectRequest: {},
            put: function (_body) {
                this.registerSubjectRequest = JSON.parse(JSON.stringify(_body));

                if (this.shouldError) {
                    return Promise.reject(this.error);
                }

                return Promise.resolve({})
            },
        };
    let onPatientRegistered;

    function getFilledInAddSubjectForm() {
        onPatientRegistered = jest.fn();
        const app = mount(<AddSubject enabled={true} patientRegistrationApi={patientRegistrationApi}
                                      studyId={STUDY_ID}
                                      patientSource={PATIENT_ID_SRC}
                                      patientId={PATIENT_ID}
                                      onPatientRegistered={onPatientRegistered}
                                      patientIdentifierSources={patientIdentifierSources}/>);
        app.setState({
            fetchingSubjectIds: false
        });
        app.find(".open-add-subject-form").simulate("click");
        app.update();
        app.find(".subject-identifier-field").simulate("change", {target: {value: EDC_SUBJECT_REFERENCE}});
        app.find(".patient-identifier-field").simulate("change", {target: {value: PATIENT_ID}});
        app.find('[className="consent-date-field"]').first().props().onChange(DATE_OF_CONSENT);
        return app;
    }

    function letWebserviceReturnError(error) {
        patientRegistrationApi.shouldError = true;
        patientRegistrationApi.error = error;
    }

    test("Handling a non-descript server side error", async () => {
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
        const app = getFilledInAddSubjectForm();
        app.find(".modal-ok").simulate("click", {});
        await Promise.resolve();

        app.update();

        expect(app).toMatchSnapshot();
        expect(app.state("error")).toEqual(error);
    });

    test("Handling a constraint validation server side error", async () => {
        let error = {
            response: {
                data: {
                    "issues": [{
                        "reference": null,
                        "field": "subjectId.id",
                        "message": "subject id error"
                    }, {
                        "reference": null,
                        "field": "patientId.id",
                        "message": "patient id error"
                    }, {
                        "reference": null,
                        "field": "dateOfConsent",
                        "message": "subject id error"
                    }]
                }
            }
        };
        letWebserviceReturnError(error);
        const app = getFilledInAddSubjectForm();
        app.find(".modal-ok").simulate("click", {});
        await Promise.resolve();

        app.update();

        expect(app.find(".subject-identifier-field").first().hasClass("form-field-error")).toBe(true);
        expect(app.find(".patient-identifier-field").first().hasClass("form-field-error")).toBe(true);
        expect(app.find(".consent-date-field").first().hasClass("form-field-error")).toBe(true);
    });

    test("After successful registration, the onPatientRegistered gets executed", async () => {
        patientRegistrationApi.shouldError = false;
        const app = getFilledInAddSubjectForm();
        app.find(".modal-ok").simulate("click", {});
        await Promise.resolve();

        app.update();

        //Put in comment until E2E-630 is implemented: Only partially check the request until first name last name and birth date is implemented
        //expect(patientRegistrationApi.registerSubjectRequest).toEqual(readSubjectJsonSample("registerSubject-request"))
        tempAssertRegisterSubjectRequestUntilE2e630Implemented(patientRegistrationApi);
    });

    test("Previous error messages are cleared before resubmit", async () => {
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

        const app = getFilledInAddSubjectForm();
        app.find(".modal-ok").simulate("click", {});
        await Promise.resolve();
        app.update();

        app.find(".patient-identifier-field").simulate("change", {target: {value: ""}});
        app.find(".modal-ok").simulate("click", {});
        expect(app.state("error")).toBeUndefined();

        app.find(".patient-identifier-field").simulate("change", {target: {value: PATIENT_ID}});
        app.find(".modal-ok").simulate("click", {});
        expect(app.state("errorMessages")).toHaveLength(0);
    });
});

describe("Integrating with registered EDC-system", () => {
    test("The component is disabled when the EDC returned no available SubjectIds", () => {
        const availableSubjects = {
            fromEDC: true,
            subjectIds: []
        };
        const addSubject = shallow(<AddSubject enabled={true} availableSubjects={availableSubjects}/>);

        expect(addSubject.instance().modalState().disabled).toBe(true);
    });
    test("The component is enabled when no EDC result is available", () => {
        const availableSubjects = {
            fromEDC: false,
            subjectIds: []
        };
        const addSubject = shallow(<AddSubject enabled={true} availableSubjects={availableSubjects}/>);

        expect(addSubject.instance().modalState().disabled).toBe(false);
        expect(addSubject.find("AutocompleteInput").exists()).toBe(false)
    });
    test("The component is enabled when EDC returns available SubjectIds", () => {
        const availableSubjects = {
            fromEDC: true,
            subjectIds: ["SUB-1", "SUB-2"]
        };
        const addSubject = shallow(<AddSubject enabled={true} availableSubjects={availableSubjects}/>);

        expect(addSubject.instance().modalState().disabled).toBe(false);
    });
    test("The component is disabled during the call to the EDC", () => {
        const addSubject = shallow(<AddSubject enabled={true} fetchAvailableSubjectsLoading={true}/>);

        expect(addSubject.instance().disabledMessage()).toEqual("Retrieving available identifiers from the linked EDC...");
        expect(addSubject.instance().modalState().disabled).toBe(true);
    });
});

describe("Retrieving available patientIds", () => {
    const patientId_data = readPatientJsonSample("availablePatientIdsForStudy-response");

    test("The component retrieves the available patientIds if it has a studyId, patientDomain and patientId-filter, and passes it to the form", () => {
        // Given: An AddSubject with valid properties
        const studyId = "aStudyId";
        const source = "source";
        const filter = "pid";
        const addSubject = mount(<AddSubject enabled={true} studyId={studyId}/>);

        // When: Changing the state with fields which triggers a call for available PatientIds
        addSubject.setState({
            patientIdentifierSource: source,
            patientIdentifier: filter
        });

        // Then: The component retrieves the available patientIds
        expect(mockAxios.get).toHaveBeenCalledWith(`/ehr2edc/ehr/patients?limit=10&patientDomain=${source}&studyId=${studyId}&filter=${filter}`);
        mockAxios.mockResponse({
            data: patientId_data,
            status: 200,
            statusText: 'OK',
            headers: {},
            config: {},
        });
        addSubject.update();
        // And: updates its state
        expect(addSubject.state("availablePatients")).toEqual(patientId_data);
        // And: passes the id's to the form component
        expect(addSubject.find("AddSubjectPane").prop("availablePatients")).toEqual(patientId_data)
    });

    test("The component retrieves the available patientIds if patientDomain changes", () => {
        const mockFn = jest.spyOn(AddSubject.prototype, "callAvailablePatientsAPI");

        const addSubject = mount(<AddSubject enabled={true} studyId="aStudyId"/>);
        addSubject.setState({
            patientIdentifierSource: "source",
            patientIdentifier: "pid"
        });
        addSubject.setState({
            patientIdentifierSource: "new-source",
        });

        expect(mockFn).toHaveBeenCalledTimes(2);
    });

    test("The component does not retrieve the available patientIds if it has no studyId", () => {
        const mockFn = jest.spyOn(AddSubject.prototype, "callAvailablePatientsAPI");

        const addSubject = mount(<AddSubject enabled={true}/>);
        addSubject.setState({
            patientIdentifierSource: "source",
            patientIdentifier: "pid"
        });

        expect(mockFn).toHaveBeenCalledTimes(0);
    });

    test("The component does not retrieve the available patientIds if it has no patient identifier source", () => {
        const mockFn = jest.spyOn(AddSubject.prototype, "callAvailablePatientsAPI");

        const addSubject = mount(<AddSubject enabled={true} studyId="aStudyId"/>);
        addSubject.setState({
            patientIdentifier: "pid"
        });

        expect(mockFn).toHaveBeenCalledTimes(0);
    });

    test("The component retrieves the available patientIds if it has no patientId filter", () => {
        const mockFn = jest.spyOn(AddSubject.prototype, "callAvailablePatientsAPI");

        const addSubject = mount(<AddSubject enabled={true} studyId="aStudyId"/>);
        addSubject.setState({
            patientIdentifierSource: "source",
        });

        expect(mockFn).toHaveBeenCalledTimes(1);
    });

    test("The component retrieves the available patientIds if the patientId filter is empty", () => {
        const mockFn = jest.spyOn(AddSubject.prototype, "callAvailablePatientsAPI");

        const addSubject = mount(<AddSubject enabled={true} studyId="aStudyId"/>);
        addSubject.setState({
            patientIdentifierSource: "source",
            patientIdentifier: ""
        });

        expect(mockFn).toHaveBeenCalledTimes(1);
    });
});


function readSubjectJsonSample(jsonSampleFile) {
    const fileContents = fs.readFileSync(path.resolve(__dirname, studySubjectControllerFolder + jsonSampleFile + ".json"), 'utf-8');
    return JSON.parse(fileContents);
}

function readPatientJsonSample(jsonSampleFile) {
    const fileContents = fs.readFileSync(path.resolve(__dirname, ehrPatientsControllerFolder + jsonSampleFile + ".json"), 'utf-8');
    return JSON.parse(fileContents);
}

function tempAssertRegisterSubjectRequestUntilE2e630Implemented(patientRegistrationApi) {
    let registerSubjectRequest = readSubjectJsonSample("registerSubject-request");
    expect(patientRegistrationApi.registerSubjectRequest.patientId).toEqual(registerSubjectRequest.patientId);
    expect(patientRegistrationApi.registerSubjectRequest.studyId).toEqual(registerSubjectRequest.studyId);
    expect(patientRegistrationApi.registerSubjectRequest.edcSubjectReference).toEqual(registerSubjectRequest.edcSubjectReference);
    expect(patientRegistrationApi.registerSubjectRequest.dateOfConsent).toEqual(registerSubjectRequest.dateOfConsent);
}