import React from 'react';
import AddSubjectPane from "../../../../../src/apps/ehr2edc/components/study-subjects-pane/add-subject-pane";

beforeAll(() => {
    Date.now = () => new Date(2019, 2, 3).getTime();
});

const patientIdentifierSources = ["HIS_01"];

describe("Rendering the component", () => {
    test("Rendering the closed state", () => {
        const app = mount(<AddSubjectPane disabled={false} open={false}
                                          patientIdentifierSources={patientIdentifierSources}/>);
        expect(app).toMatchSnapshot();
    });
    test("Rendering the disabled state", () => {
        const app = shallow(<AddSubjectPane disabled={true}
                                            disabledMessage={"This component is disabled."}
                                            patientIdentifierSources={patientIdentifierSources}/>);
        expect(app).toMatchSnapshot();
    });
    test("Rendering the open state with no patient identifier sources", () => {
        const app = mount(<AddSubjectPane disabled={false} open={true}/>);
        expect(app).toMatchSnapshot();
        expect(app.exists(".patient-identifier-source-field")).toBe(false);
    });
    test("Rendering the open state with one patient identifier source", () => {
        const patientIdentifierSources = ["HIS_01"];
        const app = mount(<AddSubjectPane disabled={false} open={true}
                                          patientIdentifierSources={patientIdentifierSources}/>);
        expect(app).toMatchSnapshot();
        expect(app.exists(".patient-identifier-source-field")).toBe(false);
    });
    test("Rendering the open state with multiple patient identifier sources", () => {
        const patientIdentifierSources = ["HIS_01", "source-2", "source-3"];
        const app = mount(<AddSubjectPane disabled={false} open={true}
                                          patientIdentifierSources={patientIdentifierSources}/>);
        expect(app).toMatchSnapshot();
        expect(app.exists(".patient-identifier-source-field")).toBe(true);
    });
});
