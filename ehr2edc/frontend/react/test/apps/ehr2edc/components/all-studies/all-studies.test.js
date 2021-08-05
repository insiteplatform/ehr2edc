'use strict';

import React from 'react';
import AllStudies, {
    mapDispatchToProps,
    mapStateToProps
} from "../../../../../src/apps/ehr2edc/components/all-studies/all-studies";
import readDataFromFile from "../../../../__helper__/sample-data-file";
import {fetchStudies} from "../../../../../src/apps/ehr2edc/actions/study.actions";
import {StaticRouter} from "react-router-dom";

describe("AllStudies rendering", () => {
    test("Loading state", () => {
        // Expect: AllStudies without 'studies' or 'error' to render the loading state
        expect(mount(<AllStudies/>)).toMatchSnapshot();
    });
    test("Empty state", () => {
        // Given: An empty list of studies
        const studies = studiesFrom("listAllStudies-noStudies-response");

        // When: Rendering the component
        const app = shallow(<AllStudies studies={studies}/>);

        // Then: The empty state is rendered
        expect(app).toMatchSnapshot();
    });
    test("Custom empty message", () => {
        // Given: An empty list of studies
        const studies = studiesFrom("listAllStudies-noStudies-response");
        // And: A custom 'emptyMessage'
        const emptyMessage = "Test empty message";

        // When: Rendering the component
        const app = mount(<AllStudies studies={studies} emptyMessage={emptyMessage}/>);

        // Then: The empty state is rendered
        expect(app).toMatchSnapshot();
        expect(app.find(".blank-state-button").text()).toEqual(emptyMessage);
    });
    test("Valid study data json gets rendered well", () => {
        // Given: A list of studies
        const studies = studiesFrom("listAllStudies-knownStudies-response");

        // When: The AllStudies-component is rendered
        const component = mount(<StaticRouter><AllStudies studies={studies}/></StaticRouter>);

        // Then: An OverviewTable component is rendered
        expect(component).toMatchSnapshot();

        const table = component.find("StudiesOverviewTable");
        expect(table.exists()).toBe(true);
        // And: The table contains the studies
        expect(table.prop("data")).toEqual(studies);
    });
    test("Error state", () => {
        // Expect: Expect an error to render the error-state
        expect(mount(<AllStudies error={error}/>)).toMatchSnapshot();
    });
    test("Error state with a custom message", () => {
        // Given: a custom error message
        const errorMessage = "Test error message";

        // When: rendering a component in the error state
        const app = mount(<AllStudies error={error} errorMessage={errorMessage}/>);

        // Then: The error-component is rendered
        const errorComp = app.find("SearchResultsError");
        expect(errorComp.exists()).toBe(true);
        // And: The custom message is used
        expect(errorComp.prop("message")).toEqual(errorMessage);
    });

    const error = {
        response: {
            data: {
                "issues": [{
                    "reference": null,
                    "field": null,
                    "message": "EHR2EDC Studies failed to load."
                }]
            }
        }
    };

    function studiesFrom(filePath) {
        return readDataFromFile(`studycontroller/${filePath}.json`).studies;
    }
});

describe("AllStudies - mapStateToProps", () => {
    it("Defaults to loading state if no study-state is present", () => {
        // Given: A state without study-node
        const state = {some: "prop"};

        // When: Mapping the state to props
        const props = mapStateToProps(state);

        // Then: The studies and error props are undefined
        expect(props).toEqual({
            studies: undefined,
            error: undefined
        });
    });
    it("Defaults to loading state if no 'all studies'-state is present", () => {
        // Given: A state without study-node
        const state = {
            study: {
                some: "prop"
            }
        };

        // When: Mapping the state to props
        const props = mapStateToProps(state);

        // Then: The studies and error props are undefined
        expect(props).toEqual({
            studies: undefined,
            error: undefined
        });
    });
    it("Retrieves studies from a successful response", () => {
        // Given: A state containing studies
        const studies = {
            studies: [
                "study-1",
                "study-2"
            ]
        };
        const state = {
            study: {
                all: {
                    data: studies,
                    error: undefined
                }
            }
        };

        // When: Mapping the state to props
        const props = mapStateToProps(state);

        // Then: The studies are retrieved from the store
        expect(props).toEqual({
            studies: studies.studies,
            error: undefined
        });
    });
    it("Retrieves error from a rejected request", () => {
        // Given: A state containing studies
        const error = "some error";
        const state = {
            study: {
                all: {
                    data: undefined,
                    error: error
                }
            }
        };

        // When: Mapping the state to props
        const props = mapStateToProps(state);

        // Then: The error is retrieved from the store
        expect(props).toEqual({
            studies: undefined,
            error: error
        });
    });
});

describe("AllStudies - mapDispatchToProps", () => {
    it("Should dispatch the fetchStudies", () => {
        // Given: A dispatch function
        const dispatch = jest.fn();

        // When: Connecting the actions
        const props = mapDispatchToProps(dispatch);
        // And: Calling the fetchStudies-action
        props.actions.fetchStudies();

        // Then: Dispatch was called with the fetchStudies actionCreator
        expect(dispatch).toHaveBeenCalledWith(fetchStudies());
    })
});

describe("AllStudies redux integration", () => {
    it("Should dispatch the fetch action on mount", () => {
        // Given: A fetch-action
        const actions = {
            fetchStudies: jest.fn()
        };

        // When: Mounting the component
        mount(<AllStudies actions={actions}/>);

        // Then: The action was called
        expect(actions.fetchStudies).toHaveBeenCalled();
    })
});
