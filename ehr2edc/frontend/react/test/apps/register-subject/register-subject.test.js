import React from 'react';
import renderer from 'react-test-renderer';
import RegisterSubject from "../../../src/apps/register-subject/register-subject";
import sinon from 'sinon';
import readDataFromFile from "../../__helper__/sample-data-file";

const DATE_OF_CONSENT = "2019-04-12";

describe("Rendering the component", () => {
    'use strict';

    const spyOnFetchStudies = sinon.spy(RegisterSubject.prototype, 'fetchStudies');
    const spyOnRegister = sinon.spy(RegisterSubject.prototype, 'registerPatientForStudy');
    const spyOnMount = sinon.spy(RegisterSubject.prototype, 'componentDidMount');
    const spyOnUpdate = sinon.spy(RegisterSubject.prototype, 'componentDidUpdate');

    const patientStudyApi = {};

    const patientRegistrationApi =
        {
            studyId: {},
            registerSubjectRequest: {},
            put: function (_studyId, _body) {
                this.studyId = _studyId;
                this.registerSubjectRequest = JSON.parse(JSON.stringify(_body));
                return Promise.resolve({ status: 201 })
            },
        };


    const onClose = jest.fn();
    const DEFAULT_PATIENT_ID_SRC = "MASTER_PATIENT_INDEX";

    beforeEach(() => {
        spyOnFetchStudies.resetHistory();
        spyOnMount.resetHistory();
        spyOnUpdate.resetHistory();
        spyOnRegister.resetHistory();
    });

    test("The page is rendered", async () => {
        letWebserviceReturnStudyDataForPatient("allStudiesForPatient-noStudies-response");
        const app = renderer.create(
            <RegisterSubject
                patientStudyApi={patientStudyApi}
                patientRegistrationApi={patientRegistrationApi}
                patientSource={DEFAULT_PATIENT_ID_SRC}
                onClose={onClose}
            />
        );
        expect(spyOnMount.callCount).toEqual(1);
        expect(spyOnUpdate.callCount).toEqual(0);
        expect(spyOnFetchStudies.callCount).toEqual(1);

        await Promise.resolve();

        const tree = app.toJSON();
        expect(tree).toMatchSnapshot();
    });

    test("The page is rendered with a patientId", async () => {
        letWebserviceReturnStudyDataForPatient("allStudiesForPatient-response");
        const patientId = "PID_001";
        const app = renderer.create(
            <RegisterSubject
                patientStudyApi={patientStudyApi}
                patientRegistrationApi={patientRegistrationApi}
                patientSource={DEFAULT_PATIENT_ID_SRC}
                patientId={patientId}
                onClose={onClose}
            />
        );
        expect(spyOnMount.callCount).toEqual(1);
        expect(spyOnUpdate.callCount).toEqual(0);
        expect(spyOnFetchStudies.callCount).toEqual(1);

        await Promise.resolve();

        const tree = app.toJSON();
        expect(tree).toMatchSnapshot();
    });

    test("The page is rendered changing the patientId", async () => {
        letWebserviceReturnStudyDataForPatient("allStudiesForPatient-onlyAvailableStudy-response");
        const patientId = "PID_001";
        const wrapper = shallow(
            <RegisterSubject
                patientStudyApi={patientStudyApi}
                patientRegistrationApi={patientRegistrationApi}
                patientSource={DEFAULT_PATIENT_ID_SRC}
                patientId={patientId}
                onClose={onClose}
            />
        );
        expect(spyOnMount.callCount).toEqual(1);
        expect(spyOnUpdate.callCount).toEqual(0);
        expect(spyOnFetchStudies.callCount).toEqual(1);

        expect(wrapper.state().patientId).toEqual(patientId);
        expect(wrapper.state().inError).toBe(false);
        expect(wrapper.state().isLoading).toBe(true);

        await Promise.resolve();

        expect(wrapper.state().availableStudies.length).toEqual(1);
        expect(wrapper.state().registeredStudies.length).toEqual(0);

        letWebserviceReturnStudyDataForPatient("allStudiesForPatient-onlyRegisteredStudy-response");

        wrapper.setProps({patientId: 'PID_002'});
        wrapper.update();
        expect(spyOnMount.callCount).toEqual(1);
        expect(spyOnUpdate.callCount).toEqual(2);
        expect(spyOnFetchStudies.callCount).toEqual(2);
        expect(wrapper.state().patientId).toEqual('PID_002');
        expect(wrapper.state().inError).toBe(false);
        expect(wrapper.state().isLoading).toBe(true);

        await Promise.resolve();

        expect(wrapper.state().availableStudies.length).toEqual(0);
        expect(wrapper.state().registeredStudies.length).toEqual(1);
    });

    test("Register a Patient as a Subject to a Study", async () => {
        letWebserviceReturnStudyDataForPatient("allStudiesForPatient-response");
        const PATIENT_ID = "PID_01";
        const STUDY_ID = "SID_01";
        const SUBJECT_ID = "SUB_01_REF";
        const PATIENT_ID_SRC = "HIS_01";
        const panel = mount(
            <RegisterSubject
                patientStudyApi={patientStudyApi}
                patientRegistrationApi={patientRegistrationApi}
                patientSource={PATIENT_ID_SRC}
                patientId={PATIENT_ID}
                onClose={onClose}
            />
        );
        expect(spyOnMount.callCount).toEqual(1);
        expect(spyOnUpdate.callCount).toEqual(0);
        expect(spyOnFetchStudies.callCount).toEqual(1);
        expect(spyOnRegister.callCount).toEqual(0);

        await Promise.resolve();

        expect(panel.state('patientId')).toEqual(PATIENT_ID);
        expect(panel.state('isLoading')).toBe(false);
        expect(panel.state('inError')).toBe(false);
        expect(panel.state('availableStudies').length).toEqual(1);
        expect(panel.state('registeredStudies').length).toEqual(1);

        const availableStudies = panel.find('ListStudies#availableStudies').first();
        availableStudies.prop('onSubjectRegistration')(STUDY_ID, SUBJECT_ID, DATE_OF_CONSENT);
        availableStudies.update();

        expect(spyOnRegister.callCount).toEqual(1);

        await Promise.resolve();

        expect(patientRegistrationApi.studyId).toEqual('SID_01');
        //Put in comment until E2E-630 is implemented: Only partially check the request until first name last name and birth date is implemented
        //expect(patientRegistrationApi.registerSubjectRequest).toEqual(readDataFromFile("studysubjectcontroller/registerSubject-request" + ".json"));
        tempAssertRegisterSubjectRequestUntilE2e630Implemented(patientRegistrationApi);

        expect(panel.state('patientId')).toEqual(PATIENT_ID);
        expect(panel.state('isLoading')).toBe(true);
        expect(panel.state('inError')).toBe(false);
        expect(panel.state('availableStudies').length).toEqual(0);
        expect(panel.state('registeredStudies').length).toEqual(0);
    });

    function letWebserviceReturnStudyDataForPatient(filePath) {
        const response = {data: readDataFromFile("/patientcontroller/" + filePath + ".json")};
        patientStudyApi.getAll = () => Promise.resolve(response);
    }

    function tempAssertRegisterSubjectRequestUntilE2e630Implemented(patientRegistrationApi) {
        let registerSubjectRequest = readDataFromFile("studysubjectcontroller/registerSubject-request" + ".json");
        expect(patientRegistrationApi.registerSubjectRequest.patientId).toEqual(registerSubjectRequest.patientId);
        expect(patientRegistrationApi.registerSubjectRequest.studyId).toEqual(registerSubjectRequest.studyId);
        expect(patientRegistrationApi.registerSubjectRequest.edcSubjectReference).toEqual(registerSubjectRequest.edcSubjectReference);
        expect(patientRegistrationApi.registerSubjectRequest.dateOfConsent).toEqual(registerSubjectRequest.dateOfConsent);
    }
});