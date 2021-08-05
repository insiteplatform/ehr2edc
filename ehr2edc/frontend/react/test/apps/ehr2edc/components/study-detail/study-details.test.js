import React from 'react';
import renderer from 'react-test-renderer';
import {mapStateToProps, StudyDetails} from "../../../../../src/apps/ehr2edc/components/study-detail/study-details";
import {StaticRouter} from "react-router-dom";
import readDataFromFile from "../../../../__helper__/sample-data-file";

const studyId = "STUDY-1";

'use strict';
const studyApi = {};
const potentialInvestigatorApi = {};
const patientIdentifierSourcesApi = {};
const studyDetails = {
    studyApi: studyApi,
    potentialInvestigatorApi: potentialInvestigatorApi,
    patientIdentifierSourcesApi: patientIdentifierSourcesApi,
    studyId: studyId
};

function letStudyReturnError() {
    const error = {
        response: {
            data: {
                "issues": [{
                    "reference": null,
                    "field": null,
                    "message": "Study failed to load."
                }]
            }, headers: {}
        }
    };
    studyApi.getOne = () => Promise.reject(error);
}

function letPotentialInvestigatorReturnError() {
    const error = {
        response: {
            data: {
                "issues": [{
                    "reference": null,
                    "field": null,
                    "message": "Investigators failed to load."
                }]
            }, headers: {}
        }
    };
    potentialInvestigatorApi.getAll = () => Promise.reject(error);
}

function letWebserviceReturnStudyData(filePath) {
    const response = {data: readDataFromFile("studycontroller/" + filePath + ".json")};
    studyApi.getOne = () => Promise.resolve(response);
}

function letWebserviceReturnPotentialInvestigatorData(filePath) {
    const response = {data: readDataFromFile("userscontroller/" + filePath + ".json")};
    potentialInvestigatorApi.getAll = () => Promise.resolve(response);
}

function letWebserviceReturnPatientIdentifierSourcesApi(filePath) {
    const response = {data: readDataFromFile("patientdomaincontroller/" + filePath + ".json")};
    patientIdentifierSourcesApi.getAll = () => Promise.resolve(response);
}

function letPatientIdentifierSourcesApiReturnError() {
    const error = {};
    patientIdentifierSourcesApi.getAll = () => Promise.reject(error);
}

beforeEach(() => {
    letWebserviceReturnStudyData("getStudy-knownStudy-response-without-investigators-or-subjects");
    letWebserviceReturnPotentialInvestigatorData("unassigned-response");
    letWebserviceReturnPatientIdentifierSourcesApi("patientIdentifierSources");
});

describe("Rendering the component", () => {
    test("Loading state", async () => {
        const app = renderer.create(<StaticRouter>
            <StudyDetails {...studyDetails}/>
        </StaticRouter>);

        const tree = app.toJSON();
        expect(tree).toMatchSnapshot();
        await Promise.resolve();
    });
    test("Error state", async () => {
        letStudyReturnError();
        const app = renderer.create(<StaticRouter>
            <StudyDetails {...studyDetails}/>
        </StaticRouter>);

        await Promise.resolve();
        const tree = app.toJSON();
        expect(tree).toMatchSnapshot();
    });
    test("Error state with custom error message", async () => {
        letStudyReturnError();
        const app = renderer.create(<StaticRouter>
            <StudyDetails {...studyDetails}
                          errorMessage="Test error message"/>
        </StaticRouter>);

        await Promise.resolve();
        const tree = app.toJSON();
        expect(tree).toMatchSnapshot();
    });
    test("Valid study data json gets rendered well", async () => {
        const app = renderer.create(<StaticRouter>
            <StudyDetails {...studyDetails}/>
        </StaticRouter>);

        await Promise.resolve();
        const tree = app.toJSON();
        expect(tree).toMatchSnapshot();
    });
    test("Valid study data json gets rendered well with a custom studyPageUrl", async () => {
        const studyPageUrl = "/studyPage";
        const app = renderer.create(<StaticRouter>
            <StudyDetails {...studyDetails}
                          studyPageUrl={studyPageUrl}/>
        </StaticRouter>);

        await Promise.resolve();
        await Promise.resolve();
        const tree = app.toJSON();
        expect(tree).toMatchSnapshot();
    });
    test("Loading state when the study data is retrieved and the potential investigators aren't", async () => {
        const app = renderer.create(<StaticRouter>
            <StudyDetails {...studyDetails}/>
        </StaticRouter>);
        await Promise.resolve();
        const tree = app.toJSON();

        expect(tree).toMatchSnapshot();
    });
    test("Error state when only the potential investigators call fails", async () => {
        letPotentialInvestigatorReturnError();
        const app = renderer.create(<StaticRouter>
            <StudyDetails {...studyDetails}/>
        </StaticRouter>);

        await Promise.resolve();
        const tree = app.toJSON();
        expect(tree).toMatchSnapshot();
    });
    test("Error state when only the patient identifier sources call fails", async () => {
        letPatientIdentifierSourcesApiReturnError();
        const app = renderer.create(<StaticRouter>
            <StudyDetails {...studyDetails}
            />
        </StaticRouter>);

        await Promise.resolve();
        const tree = app.toJSON();
        expect(tree).toMatchSnapshot();
    });
});

describe("Firing the onInvestigatorAdd event", () => {
    let investigatorApi = {};
    const investigatorToAdd = readDataFromFile("studyinvestigatorscontroller/addInvestigators-single-investigator.json");

    beforeEach(() => {
        investigatorApi.create = (investigator) => Promise.resolve();
        studyApi.getOne = () => Promise.resolve();
    });

    test("Firing the onInvestigatorAdd event saves the correct investigator", () => {
        // Given: The component is mounted
        const app = shallow(<StudyDetails {...studyDetails}
                                          investigatorApi={investigatorApi}/>);
        app.instance().assignInvestigator = jest.fn();

        // When: Firing the onInvestigatorAdd-event
        app.state("onInvestigatorAdd")(investigatorToAdd);

        // Then: The investigator was saved
        expect(app.instance().assignInvestigator).toHaveBeenCalledTimes(1);
        expect(app.instance().assignInvestigator).toHaveBeenCalledWith(investigatorToAdd);
    });
});

describe("Firing the onRefresh event", () => {
    let investigatorApi = {};

    beforeEach(() => {
        investigatorApi = {
            create: (investigator) => Promise.resolve()
        };
    });

    test("Firing the onRefresh event refreshes the study data", () => {
        // Given: The component is mounted
        const app = shallow(<StudyDetails {...studyDetails}
                                          investigatorApi={investigatorApi}/>);
        app.instance().fetchStudy = jest.fn();

        // When: Firing the onRefresh-event
        app.state("onRefresh")();

        // Then: The study is fetched
        expect(app.instance().fetchStudy).toHaveBeenCalled();
    });
});

describe("Firing the onEdcSubjectReferenceChanged event", () => {

    test("Firing the onEdcSubjectReferenceChanged event updates edcSubjectReference in breadcrumbParams", () => {
        const app = shallow(<StudyDetails/>);
        const onEdcSubjectReferenceChanged = app.state("onEdcSubjectReferenceChanged");

        onEdcSubjectReferenceChanged("EDC_SUBJECT_REFERENCE_1");

        expect(app.state('breadcrumbParams')).toEqual({
            edcSubjectReference: "EDC_SUBJECT_REFERENCE_1",
            eventName: "",
            studyName: ""
        })
    });
});

describe("Firing the onEventChanged event", () => {

    test("Firing the onEventChanged event updates event name in breadcrumbParams", () => {
        const app = shallow(<StudyDetails/>);
        const onEventChanged = app.state("onEventChanged");

        onEventChanged({name: "EVENT_NAME_1"});

        expect(app.state('breadcrumbParams')).toEqual({
            edcSubjectReference: "",
            eventName: "EVENT_NAME_1",
            studyName: ""
        })
    });
});

describe("mapStateToProps", () => {
    test("When itemProvenance is empty, hideSidebar is false", () => {
        const state = {itemProvenance: {}};

        expect(mapStateToProps(state)).toEqual({hideSidebar: false});
    });
    test("When itemProvenance is present, hideSidebar is true", () => {
        const state = {itemProvenance: {key: "value"}};

        expect(mapStateToProps(state)).toEqual({hideSidebar: true});
    });
});