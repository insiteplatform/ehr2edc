import React from 'react';
import renderer from 'react-test-renderer';
import StudyOverviewPane from "../../../../../src/apps/ehr2edc/components/study-overview-pane/study-overview-pane";

describe("Rendering the component", () => {
    test("Loading the company with a study", () => {
        const study = {
            id: "study-1",
            name: "Test study nr. 1",
            description: "Description for 'Test study nr. 1'"
        };
        const app = renderer.create(<StudyOverviewPane study={study} />);
        const tree = app.toJSON();
        expect(tree).toMatchSnapshot();
    });
    test("Loading the company without a study", () => {
        const app = renderer.create(<StudyOverviewPane />);
        const tree = app.toJSON();
        expect(tree).toMatchSnapshot();
    });
});