import React from 'react';
import SubjectIdField from "../../../../../src/common/components/forms/subject-id/subject-id";

describe("Rendering the SubjectId input", () => {
    test("Renders without availableSubjectIds", () => {
        const field = shallow(<SubjectIdField/>);

        expect(field).toMatchSnapshot();
        expect(field.find("AutocompleteInput")).toHaveLength(0);
        expect(field.find("input[name='subjectIdentifier']")).toHaveLength(1);
    });

    test("Renders with availableSubjectIds", () => {
        const field = shallow(<SubjectIdField availableSubjects={{
            fromEDC: true,
            subjectIds: ["SUB-1", "SUB-2"]
        }}/>);

        expect(field).toMatchSnapshot();
        expect(field.find("AutocompleteInput")).toHaveLength(1);
        expect(field.find("input[name='subjectIdentifier']")).toHaveLength(0);
    });

    test("Renders disabled field with availableSubjectIds", () => {
        const field = shallow(<SubjectIdField availableSubjects={{
            fromEDC: true,
            subjectIds: ["SUB-1", "SUB-2"]
        }} disabled={true}/>);

        expect(field).toMatchSnapshot();
        expect(field.find("AutocompleteInput")).toHaveLength(0);
        expect(field.find("input[name='subjectIdentifier']")).toHaveLength(1);
    })
});

describe("Interaction with parent component", () => {
    const changedId = "changedId";
    test("Callback is triggered onChange", () => {
        const callback = jest.fn();
        const field = shallow(<SubjectIdField onChange={callback}/>);

        field.instance().subjectIdChange(changedId);

        expect(callback).toHaveBeenCalledWith(changedId);
    });
    test("When the subject id field has changed, the value gets updated", () => {
        const callback = jest.fn();
        const field = shallow(<SubjectIdField onChange={callback}/>);

        field.find(".subject-identifier-field").simulate("change", {target: {value: changedId}});

        expect(callback).toHaveBeenCalledWith(changedId);
    });
    test("When the subject id field has lost focus, the value gets trimmed", () => {
        const callback = jest.fn();
        const field = shallow(<SubjectIdField onChange={callback}/>);

        field.find(".subject-identifier-field").simulate("blur", {target: {value: ` ${changedId}\t`}});

        expect(callback).toHaveBeenCalledWith(changedId);
    });
});