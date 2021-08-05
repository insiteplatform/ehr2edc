import React from 'react';
import EHR2EDC from "../../../src/apps/ehr2edc/ehr2edc";
import {MemoryRouter} from "react-router-dom";
import {StudyDetails} from "../../../src/apps/ehr2edc/components/study-detail/study-details";
import axios from "../../__mocks__/axios";

describe("EHR2EDC Rendering", () => {
    it("Renders AllStudies by default", () => {
        // And: A Study API
        const studyApi = {
            getAll: jest.fn(() => Promise.resolve({data: {studies: []}}))
        };

        // When: The component is mounted without initialEntries
        const app = mount(<MemoryRouter>
            <EHR2EDC studyPageUrl={"/"}
                     studyApi={studyApi}/>
        </MemoryRouter>);

        // Then: AllStudies is rendered
        expect(app.exists('AllStudies')).toBe(true);
    });

    it("Renders AllStudies for the base URL", () => {
        // Given: The root URL '/'
        const entries = ["/"];
        // And: A Study API
        const studyApi = {
            getAll: jest.fn(() => Promise.resolve({data: {studies: []}}))
        };

        // When: The component is mounted
        const app = mount(<MemoryRouter initialEntries={entries}>
            <EHR2EDC studyPageUrl={"/"}
                     studyApi={studyApi}/>
        </MemoryRouter>);

        // Then: AllStudies is rendered
        expect(app.exists('AllStudies')).toBe(true);
    });

    it("Renders StudyDetails for a Study-URL", () => {
        // Given: A studyId-URL
        const entries = ["/studyId"];

        // When: The component is mounted
        const app = mount(<MemoryRouter initialEntries={entries}>
            <EHR2EDC studyPageUrl={"/"}/>
        </MemoryRouter>);

        // Then: AllStudies is rendered
        expect(app.exists('StudyDetails')).toBe(true);
    })
});

describe("Routing", () => {
    it("Appends a trailing slash if it is missing from the base url", () => {
        // Given: A url without trailing slash
        const base = "/a/base/url";

        //When: Rendering the EHR2EDC-component
        const app = shallow(<EHR2EDC studyPageUrl={base} />);

        // Then: A trailing slash is appended to the basename
        expect(app.instance().basename()).toEqual(`${base}/`);
    });

    it("Uses the given base url if it has a trailing slash", async () => {
        // Given: A url with trailing slash
        const base = "/a/base/url/";

        //When: Rendering the EHR2EDC-component
        const app = shallow(<EHR2EDC studyPageUrl={base} />);

        // Then: A trailing slash is appended to the basename
        expect(app.instance().basename()).toEqual(base);
    });

    it("Correctly sets the axios baseUrl", () => {
        shallow(<EHR2EDC />);
        expect(axios.defaults.baseURL).toEqual("/");
        shallow(<EHR2EDC baseUrl={"/aBaseUrl"} />);
        expect(axios.defaults.baseURL).toEqual("/aBaseUrl");
    })
});
