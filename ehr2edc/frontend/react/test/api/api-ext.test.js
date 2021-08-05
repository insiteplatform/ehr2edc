import React from 'react';
import {
    getEventPopulationReadiness,
    listAvailablePatientFor,
    listAvailableSubjectFor,
    patientRegistrationApiForRegisterSubjectComponent,
    patientRegistrationApiForStudyDetailsComponent,
    subjectApi,
} from "../../src/api/api-ext";
import mockAxios from 'jest-mock-axios';

afterEach(() => {
    mockAxios.reset();
});

describe("Patient registration api for register subject component", () => {

    test("registers a subject correctly", () => {
        const postData = {fakeProperty1: "value_1"};
        const studyId = "MY_STUDY_ID_1";

        patientRegistrationApiForRegisterSubjectComponent("/ehr2edc/studies/${studyId}/subjects").put(studyId, postData);

        expect(mockAxios.put).toHaveBeenCalledWith('/ehr2edc/studies/MY_STUDY_ID_1/subjects', postData);
    });

});

describe("Patient registration api for study detail component", () => {

    test("registers a subject correctly", () => {
        const postData = {fakeProperty1: "value_1"};

        patientRegistrationApiForStudyDetailsComponent("/ehr2edc/studies/MY_STUDY_ID_2/subjects").put(postData);

        expect(mockAxios.put).toHaveBeenCalledWith('/ehr2edc/studies/MY_STUDY_ID_2/subjects', postData);
    });

});

describe("Subject study api", () => {
    test("get a subject correctly", () => {
        const subjectId = "MY_SUBJECT_ID";
        const studyId = "MY_STUDY_ID_1";

        subjectApi("/ehr2edc/studies/${studyId}/subjects/${subjectId}").getByStudyIdAndSubjectId(studyId, subjectId);

        expect(mockAxios.get).toHaveBeenCalledWith('/ehr2edc/studies/MY_STUDY_ID_1/subjects/MY_SUBJECT_ID');
    });
});

describe("List Available SubjectIds for Study", () => {
    test("listAvailableSubjectFor: invalid study", () => {
        expect(() => listAvailableSubjectFor(null)).toThrowError("StudyId expected.");
        expect(() => listAvailableSubjectFor(undefined)).toThrowError("StudyId expected.");
        expect(() => listAvailableSubjectFor({})).toThrowError("StudyId expected.");
        expect(() => listAvailableSubjectFor([])).toThrowError("StudyId expected.");
        expect(() => listAvailableSubjectFor(42)).toThrowError("StudyId expected.");
    });

    test("listAvailableSubjectFor: valid study, calls the correct URL via a GET-method", () => {
        listAvailableSubjectFor("aStudy");

        expect(mockAxios.get).toHaveBeenCalledWith('/ehr2edc/edc/subjects?studyId=aStudy');
    })
});

describe("Get event population readiness for subject id", () => {
    test("Get event population readiness  with invalid subject id", () => {
        expect(() => getEventPopulationReadiness("study-id-098",null)).toThrowError("subjectId expected.");
        expect(() => getEventPopulationReadiness("study-id-098",undefined)).toThrowError("subjectId expected.");
        expect(() => getEventPopulationReadiness("study-id-098",{})).toThrowError("subjectId expected.");
        expect(() => getEventPopulationReadiness("study-id-098",[])).toThrowError("subjectId expected.");
        expect(() => getEventPopulationReadiness("study-id-098",42)).toThrowError("subjectId expected.");
    });

    test("Get event population readiness with invalid study id", () => {
        expect(() => getEventPopulationReadiness(null, "subject-id-098")).toThrowError("studyId expected.");
        expect(() => getEventPopulationReadiness(undefined, "subject-id-098")).toThrowError("studyId expected.");
        expect(() => getEventPopulationReadiness({}, "subject-id-098")).toThrowError("studyId expected.");
        expect(() => getEventPopulationReadiness([], "subject-id-098")).toThrowError("studyId expected.");
        expect(() => getEventPopulationReadiness(42, "subject-id-098")).toThrowError("studyId expected.");
    });

    test("Get event population readiness with valid studyId and  valid subjectId, calls the correct URL via a GET-method", () => {
        getEventPopulationReadiness("study-id-098", "subjectId-123");

        expect(mockAxios.get).toHaveBeenCalledWith('/ehr2edc/studies/study-id-098/subjects/subjectId-123/event-population-readiness');
    })
});

describe("List Available Patients for Study", () => {
    test("listAvailablePatientFor: invalid study", () => {
        expect(() => listAvailablePatientFor(null, "domain")).toThrowError("StudyId and domain are required.");
        expect(() => listAvailablePatientFor(undefined, "domain")).toThrowError("StudyId and domain are required.");
        expect(() => listAvailablePatientFor({}, "domain")).toThrowError("StudyId and domain are required.");
        expect(() => listAvailablePatientFor([], "domain")).toThrowError("StudyId and domain are required.");
        expect(() => listAvailablePatientFor(42, "domain")).toThrowError("StudyId and domain are required.");
    });

    test("listAvailablePatientFor: invalid domain", () => {
        expect(() => listAvailablePatientFor("aStudy", null)).toThrowError("StudyId and domain are required.");
        expect(() => listAvailablePatientFor("aStudy", undefined)).toThrowError("StudyId and domain are required.");
        expect(() => listAvailablePatientFor("aStudy", {})).toThrowError("StudyId and domain are required.");
        expect(() => listAvailablePatientFor("aStudy", [])).toThrowError("StudyId and domain are required.");
        expect(() => listAvailablePatientFor("aStudy", 42)).toThrowError("StudyId and domain are required.");
    });

    test("listAvailablePatientFor: invalid filter", () => {
        expect(() => listAvailablePatientFor("aStudy", "domain", null)).not.toThrowError("StudyId and domain are required.");
        expect(mockAxios.get).toHaveBeenCalledWith('/ehr2edc/ehr/patients?limit=10&patientDomain=domain&studyId=aStudy&filter=');
        expect(() => listAvailablePatientFor("aStudy", "domain", undefined)).not.toThrowError("StudyId and domain are required.");
        expect(mockAxios.get).toHaveBeenCalledWith('/ehr2edc/ehr/patients?limit=10&patientDomain=domain&studyId=aStudy&filter=');
        expect(() => listAvailablePatientFor("aStudy", "domain", {})).not.toThrowError("StudyId and domain are required.");
        expect(mockAxios.get).toHaveBeenCalledWith('/ehr2edc/ehr/patients?limit=10&patientDomain=domain&studyId=aStudy&filter=');
        expect(() => listAvailablePatientFor("aStudy", "domain", [])).not.toThrowError("StudyId and domain are required.");
        expect(mockAxios.get).toHaveBeenCalledWith('/ehr2edc/ehr/patients?limit=10&patientDomain=domain&studyId=aStudy&filter=');
        expect(() => listAvailablePatientFor("aStudy", "domain", 42)).not.toThrowError("StudyId and domain are required.");
        expect(mockAxios.get).toHaveBeenCalledWith('/ehr2edc/ehr/patients?limit=10&patientDomain=domain&studyId=aStudy&filter=');
    });

    test("listAvailablePatientFor without a filter", () => {
        listAvailablePatientFor("aStudy", "domain");
        expect(mockAxios.get).toHaveBeenCalledWith('/ehr2edc/ehr/patients?limit=10&patientDomain=domain&studyId=aStudy&filter=');
    });

    test("listAvailablePatientFor with a empty filter", () => {
        listAvailablePatientFor("aStudy", "domain", "");
        expect(mockAxios.get).toHaveBeenCalledWith('/ehr2edc/ehr/patients?limit=10&patientDomain=domain&studyId=aStudy&filter=');
    });

    test("listAvailablePatientFor with a filter", () => {
        listAvailablePatientFor("aStudy", "domain", "startswith");
        expect(mockAxios.get).toHaveBeenCalledWith('/ehr2edc/ehr/patients?limit=10&patientDomain=domain&studyId=aStudy&filter=startswith');
    });
});