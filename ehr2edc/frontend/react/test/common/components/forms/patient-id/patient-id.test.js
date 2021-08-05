import PatientIdField from "../../../../../src/common/components/forms/patient-id/patient-id";
import React from "react";

describe("Rendering the PatientId input", () => {
    test("Renders without availablePatientIds", () => {
        const field = shallow(<PatientIdField/>);

        expect(field).toMatchSnapshot();
    });

    test("Renders with availablePatientIds", () => {
        const field = shallow(<PatientIdField availablePatients={{
            patientIds: ["PATIENT-1", "PATIENT-2"]
        }}/>);

        expect(field).toMatchSnapshot();
    });

    test("Renders with errors", () => {
        const field = shallow(<PatientIdField valid={false} availablePatients={{
            patientIds: ["PATIENT-1", "PATIENT-2"]
        }}/>);

        expect(field).toMatchSnapshot();
    })
});

describe("Interaction with parent component", () => {
    test("Callback is triggered onChange", () => {
        const callback = jest.fn();
        const field = shallow(<PatientIdField onChange={callback}/>);

        field.instance().patientIdChange("changedId");

        expect(callback).toHaveBeenCalledWith("changedId");
    });

    test('callback is triggered with value of the input field when the value is in the auto complete list', () => {
        const callback = jest.fn();
        const patientIdField = mount(<PatientIdField onChange={callback} availablePatients={{
            patientIds: ["PATIENT-1231", "PATIENT-1232", "PATIENT-1233"]
        }}/>);

        const input = patientIdField.find('input');
        input.simulate('change', {target: {value: 'PATIENT-1233'}})

        expect(callback).toHaveBeenCalledWith("PATIENT-1233");
    });

    test('callback is triggered with value of the input field when the value is NOT in the auto complete list', () => {
        const callback = jest.fn();
        const patientIdField = mount(<PatientIdField onChange={callback} availablePatients={{
            patientIds: ["PATIENT-1231", "PATIENT-1232", "PATIENT-1233"]
        }}/>);

        const input = patientIdField.find('input');
        input.simulate('change', {target: {value: 'PATIENT-1235'}});

        expect(callback).toHaveBeenCalledWith("PATIENT-1235");
    });
});
