import React from 'react';
import renderer from 'react-test-renderer';
import StudyInvestigatorsPane
    from "../../../../../src/apps/ehr2edc/components/study-investigators-pane/study-investigators-pane";

describe("Rendering the component", () => {
    const fs = require('fs');
    const path = require("path");
    const webserviceResponseFolder = "../../../../../../../infrastructure/web/src/test/resources/samples/studycontroller/";
    const study = readStudyData("getStudy-knownStudy-response-with-investigators-and-subjects");
    const study_removableInvestigators =
        readStudyData("getStudy-knownStudy-response-with-removable-investigators-and-subjects");

    test("Loading the component with no investigators", async () => {
        const app = renderer.create(<StudyInvestigatorsPane/>);
        const tree = app.toJSON();
        expect(tree).toMatchSnapshot();
        await Promise.resolve();
    });
    test("Loading the component with missing investigators", async () => {
        const app = renderer.create(<StudyInvestigatorsPane investigators={null}/>);
        const tree = app.toJSON();
        expect(tree).toMatchSnapshot();
        await Promise.resolve();
    });
    test("Loading the component with empty investigators", async () => {
        const app = renderer.create(<StudyInvestigatorsPane investigators={[]}/>);
        const tree = app.toJSON();
        expect(tree).toMatchSnapshot();
        await Promise.resolve();
    });
    test("Loading the component with investigators", async () => {
        const investigators = study.investigators;
        const app = renderer.create(<StudyInvestigatorsPane investigators={investigators}/>);
        const tree = app.toJSON();
        expect(tree).toMatchSnapshot();
        await Promise.resolve();
    });
    test("Loading the component with removable investigators", async () => {
        const investigators = study_removableInvestigators.investigators;
        const app = renderer.create(<StudyInvestigatorsPane investigators={investigators}/>);
        const tree = app.toJSON();
        expect(tree).toMatchSnapshot();
        await Promise.resolve();
    });
    test("Reloading an empty component with investigators", async () => {
        const app = shallow(<StudyInvestigatorsPane investigators={[]} removable={false}/>);
        app.setProps({investigators: study.investigators});
        expect(app).toMatchSnapshot();
        await Promise.resolve();
    });
    test("As an DRM user, I can unassign investigator from a study", async () => {
        const investigators = study_removableInvestigators.investigators;
        const unassignCallback = jest.fn();
        const studyInvestigatorsPaneComponent = mount(
            <StudyInvestigatorsPane
                investigators={investigators}
                onInvestigatorUnassign={unassignCallback}/>
        );
        expect(studyInvestigatorsPaneComponent).toMatchSnapshot();
        studyInvestigatorsPaneComponent.find(".unassign-icon").simulate('click');
        expect(unassignCallback).toHaveBeenCalledWith(investigators[0]);
    });

    function readStudyData(fileName) {
        const fileContents = fs.readFileSync(path.resolve(__dirname, webserviceResponseFolder + "/" + fileName + ".json"), 'utf-8');
        return JSON.parse(fileContents);
    }
});