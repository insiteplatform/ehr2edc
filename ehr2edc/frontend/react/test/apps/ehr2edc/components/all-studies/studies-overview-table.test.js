import React from 'react';
import renderer from 'react-test-renderer';
import StudiesOverviewTable from "../../../../../src/apps/ehr2edc/components/all-studies/studies-overview-table";
import {MemoryRouter} from "react-router-dom";

describe("Component rendering", () => {
    test("Table gets rendered well with no data", () => {
        const table = renderer.create(<MemoryRouter>
            <StudiesOverviewTable/>
        </MemoryRouter>);
        const tree = table.toJSON();
        expect(tree).toMatchSnapshot();
    });
    test("Table gets rendered well with data", () => {
        const data = generateTestData();
        const table = renderer.create(<MemoryRouter>
            <StudiesOverviewTable data={data}/>
        </MemoryRouter>);
        const tree = table.toJSON();
        expect(tree).toMatchSnapshot();
    });
});

function generateTestData() {
    return [generateStudy(1), generateStudy(2), generateStudy(3)];
}

function generateStudy(index) {
    return {id: "TEST-" + index, name: "Test name " + index, description: "Test description" + index};
}