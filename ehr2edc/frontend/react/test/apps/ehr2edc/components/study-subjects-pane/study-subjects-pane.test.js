import React from 'react';
import renderer from 'react-test-renderer';
import {MemoryRouter, StaticRouter} from "react-router-dom";
import StudySubjectsPane from "../../../../../src/apps/ehr2edc/components/study-subjects-pane/study-subjects-pane";
import RemoveSubject from "../../../../../src/apps/ehr2edc/components/study-subjects-pane/remove-subject";
import AddSubject from "../../../../../src/apps/ehr2edc/components/study-subjects-pane/add-subject";
import readDataFromFile from "../../../../__helper__/sample-data-file";

afterEach(() => {
    jest.clearAllMocks();
});

describe("Rendering the component", () => {
    test("Loading the component with no subjects", async () => {
        const app = renderer.create(<StudySubjectsPane canSubjectsBeAdded={true} canSubjectsBeViewed={true}/>);
        const tree = app.toJSON();
        expect(tree).toMatchSnapshot();
        await Promise.resolve();
    });
    test("Loading the component with missing subjects", async () => {
        const app = renderer.create(<StudySubjectsPane subjects={null} canSubjectsBeAdded={true}
                                                       canSubjectsBeViewed={true}/>);
        const tree = app.toJSON();
        expect(tree).toMatchSnapshot();
        await Promise.resolve();
    });
    test("Loading the component with empty subjects", async () => {
        const app = renderer.create(<StudySubjectsPane subjects={[]} canSubjectsBeAdded={true}
                                                       canSubjectsBeViewed={true}/>);
        const tree = app.toJSON();
        expect(tree).toMatchSnapshot();
        await Promise.resolve();
    });
    test("Loading the component when no subjects can be added", async () => {
        const app = renderer.create(<StudySubjectsPane subjects={[]} canSubjectsBeAdded={false}
                                                       canSubjectsBeViewed={true}/>);
        const tree = app.toJSON();
        expect(tree).toMatchSnapshot();
        await Promise.resolve();
    });
    test("When it is not specified whether or not a subject can be added, it renders it as unable to add subjects", async () => {
        const app = shallow(<StudySubjectsPane subjects={[]}/>);
        expect(app.exists("AddSubject[enabled=false]")).toBe(true);
    });
    test("Loading the component with subjects", async () => {
        const app = renderer.create(<MemoryRouter>
            <StudySubjectsPane subjects={mockSubjects()} canSubjectsBeAdded={true} canSubjectsBeViewed={true}/>
        </MemoryRouter>);
        const tree = app.toJSON();
        expect(tree).toMatchSnapshot();
        await Promise.resolve();
    });
    test("Loading the component with response from controller", async () => {
        const {subjects} = readWebSample("getStudy-knownStudy-response-with-investigators-and-subjects");
        const app = renderer.create(<MemoryRouter>
            <StudySubjectsPane subjects={subjects} canSubjectsBeAdded={true} canSubjectsBeViewed={true}/>
        </MemoryRouter>);

        expect(app).toMatchSnapshot();
    });
    test("Loading the component with subjects when no subjects can be added", async () => {
        const app = mount(<StaticRouter>
            <StudySubjectsPane subjects={mockSubjects()} canSubjectsBeAdded={false} canSubjectsBeViewed={true}/>
        </StaticRouter>);

        expect(app.find(".open-add-subject-form").exists()).toBe(true);
        expect(app.find(".open-add-subject-form").hasClass('disabled')).toBe(true);
    });
    test("Loading the component with not viewable subjects", async () => {
        const app = mount(<StaticRouter>
            <StudySubjectsPane subjects={mockSubjects()} canSubjectsBeAdded={false} canSubjectsBeViewed={false}/>
        </StaticRouter>);

        expect(app.find(".error").text()).toBe("User is not an assigned Investigator");
    });

});

describe("Component interaction", () => {
    test("Remove subject", async () => {
        const openFnSpy = jest.spyOn(RemoveSubject.prototype, "open");
        const studyId = "studyID";
        const subjects = mockSubjects();

        const app = mount(<StaticRouter>
            <StudySubjectsPane subjects={subjects}
                               canSubjectsBeViewed={true}
                               studyId={studyId}/>
        </StaticRouter>);
        const removeSubject = app.find(RemoveSubject);

        let remove = app.find(".small-1 .remove-icon").first();
        remove.simulate('click');

        expect(openFnSpy).toHaveBeenCalledWith(studyId, subjects[0].subjectId);
        expect(removeSubject.state("open")).toBe(true);
    });
});

describe("Fetch available subjects", () => {
    const spyOnFetchData = jest.spyOn(StudySubjectsPane.prototype, 'fetchAvailableSubjects');

    beforeEach(() => {
        spyOnFetchData.mockReset();
    });

    test("Pass available subjects to add subject", () => {
        const studyId = "studyID";
        const subjects = mockSubjects();
        const availableSubjects = readDataFromFile("edcstudycontroller/availableSubjectIdsForStudy-response.json")

        const app = shallow(<StudySubjectsPane subjects={subjects}
                                               studyId={studyId}
                                               onRefresh={() => {
                                               }}/>);
        app.setState({availableSubjects: availableSubjects});

        expect(app.state("availableSubjects")).toEqual(availableSubjects);
        expect(app.find("AddSubject").prop("availableSubjects")).toEqual(availableSubjects);
    });

    test("After successful registration, the subject id list gets refreshed", () => {
        const studyId = "studyID";
        const subjects = mockSubjects();
        const app = shallow(<StudySubjectsPane subjects={subjects}
                                               studyId={studyId}
                                               onRefresh={() => {
                                               }}/>);
        expect(spyOnFetchData).toHaveBeenCalledTimes(1);

        app.find("AddSubject").props().onPatientRegistered();

        expect(spyOnFetchData).toHaveBeenCalledTimes(2);
    });
    test("After successful deregistration, the subject id list gets refreshed", () => {
        const studyId = "studyID";
        const subjects = mockSubjects();
        const app = shallow(<StudySubjectsPane subjects={subjects}
                                               studyId={studyId}
                                               onRefresh={() => {
                                               }}/>);
        expect(spyOnFetchData).toHaveBeenCalledTimes(1);

        app.find("RemoveSubject").props().onPatientDeregistered();

        expect(spyOnFetchData).toHaveBeenCalledTimes(2);
    });

    test("The component does not request the available subjectIds from EDC without a studyId", () => {
        const subjects = mockSubjects();
        shallow(<StudySubjectsPane subjects={subjects}
                                   canSubjectsBeViewed={true}
                                   onRefresh={() => {
                                   }}/>);

        expect(spyOnFetchData).not.toHaveBeenCalled();
    });
    test("The component requests the available subjectIds from EDC when a studyId is present", () => {
        const studyId = "studyID";
        const subjects = mockSubjects();
        shallow(<StudySubjectsPane subjects={subjects}
                                   studyId={studyId}
                                   onRefresh={() => {
                                   }}/>);

        expect(spyOnFetchData).toHaveBeenCalledTimes(1);
    });
    test("The component refreshes the available subjectIds from EDC when the studyId changes", () => {
        const studyId = "studyID";
        const subjects = mockSubjects();
        const app = shallow(<StudySubjectsPane subjects={subjects}
                                               studyId={studyId}
                                               onRefresh={() => {
                                               }}/>);
        spyOnFetchData.mockReset();

        app.setProps({studyId: "anotherStudyId"});

        expect(spyOnFetchData).toHaveBeenCalledTimes(1);
    });
});

function mockSubjects() {
    let subjects = [];
    for (let i = 0; i < 50; i++) {
        subjects.push({
            subjectId: "subject-" + (i + 1),
            edcSubjectReference: "ehr-subject-" + (i + 1),
            patientId: {
                source: "patient-source",
                id: "patient-" + (i + 1)
            },
            detailViewable: true,
            consentDateTime: "2017-07-01T08:00:00.00Z"
        })
    }
    return subjects;
}

const fs = require('fs');
const path = require("path");

function readWebSample(jsonSampleFile) {
    const fileContents = fs.readFileSync(
        path.resolve(__dirname, `../../../../../../../infrastructure/web/src/test/resources/samples/studycontroller/${jsonSampleFile}.json`),
        'utf-8');
    return JSON.parse(fileContents);
}