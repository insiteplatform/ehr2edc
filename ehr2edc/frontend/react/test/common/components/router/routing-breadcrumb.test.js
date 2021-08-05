import renderer from "react-test-renderer";
import React from "react";
import {BreadcrumbLink} from "../../../../src/common/components/router/routing-breadcrumb";
import {MemoryRouter} from "react-router-dom";

describe("Breadcrumb correctly render route ", () => {
    test("The component generated current route correctly based on current location", async () =>  {
        const path = '/app/ehr2edc/studies/:studyId/subjects/:subjectId/eventdefinitions/:eventdefinitionId';
        const app = renderer.create(
            <MemoryRouter initialEntries={["/app/ehr2edc/studies/studyId_1/subjects/subjectId_1/eventdefinitions/eventDefinitionId_1"]}>
                <BreadcrumbLink path={path} title='currentRouteTitle'/>
            </MemoryRouter>
            );
        expect(app.toJSON()).toMatchSnapshot();
    });

    test("The component generated ancestor route correctly based on the router", async () =>  {
        const path = '/app/ehr2edc/studies/:studyId/subjects/:subjectId/eventdefinitions/:eventdefinitionId/';

        const app = renderer.create(
            <MemoryRouter initialEntries={["/app/ehr2edc/studies/studyId_1/subjects/subjectId_1/eventdefinitions/eventDefinitionId_1/forms"]} keyLength={0}>
                <BreadcrumbLink path={path} title='aAncestorTitle'/>
            </MemoryRouter>
        );

        expect(app.toJSON()).toMatchSnapshot();
    });
});

describe("Breadcrumb correctly render the title", () => {

    let consoleSpy;

    beforeEach(() => {
        if (typeof consoleSpy === "function") {
            consoleSpy.mockRestore();
        }
    });

    test("The component generates the title correctly based on router matches props", async () =>  {
        const path = '/app/ehr2edc/studies/:studyId/subjects/:subjectId/eventdefinitions/:eventdefinitionId';
        const app = mount(
            <MemoryRouter initialEntries={["/app/ehr2edc/studies/studyId_1/subjects/subjectId_1/eventdefinitions/eventDefinitionId_1"]}>
                <BreadcrumbLink path={path} title=':subjectId'/>
            </MemoryRouter>
        );

        expect(app.exists('li')).toBeTruthy();
        expect(app.find('li').text()).toBe("subjectId_1");
    });

    test("The component generates the title correctly based on breadcrumbParameters", async () =>  {
        consoleSpy = jest.spyOn(console, "error").mockImplementation(); // Suppress react-router error
        const path = '/app/ehr2edc/studies/:studyId/subjects/:subjectId/eventdefinitions/:eventdefinitionId';
        const breadcrumbParams = {
            edcSubjectReference: 'edcSubjectReference_1'
        };

        const app = mount(
            <MemoryRouter initialEntries={["/app/ehr2edc/studies/studyId_1/subjects/subjectId_1/eventdefinitions/eventDefinitionId_1/form"]}>
                <BreadcrumbLink path={path} title=':edcSubjectReference' breadcrumbParams={breadcrumbParams}/>
            </MemoryRouter>
        );

        expect(app.exists('li')).toBeTruthy();
        expect(app.find('li').text()).toBe("edcSubjectReference_1")
    });
});