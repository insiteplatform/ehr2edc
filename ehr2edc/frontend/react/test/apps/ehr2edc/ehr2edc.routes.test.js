import React from 'react'
import {MemoryRouter, Route} from "react-router-dom";
import {state} from "./components/study-detail/study-details.state";
import {ehr2edcRoutes} from "../../../src/apps/ehr2edc/ehr2edc.routes";

const pathVariables = {
    match: {
        params: {
            "studyId": "STUDY",
            "subjectId": "SID",
            "eventId": "EID",
            "eventDefinitionId": "EDID"
        }
    }
};

/*
afterAll(() => {
    const testAllRoutesError = "When running the entire testsuite, all configured routes should have been visited";
    const allPaths = ehr2edcRoutes.map(config => config.path);
    expect(RouteTesting.testedRoutes, testAllRoutesError).toEqual(expect.arrayContaining(allPaths));
    expect(RouteTesting.testedRoutes.length, testAllRoutesError).toEqual(allPaths.length);
});
*/

describe("Renders all routes correctly", () => {
    test("Renders the StudyOverviewPane", async () => {
        const app = await RouteTesting.mountRouteWithTitle(":studyName");

        const component = RouteAsserts.find(app, "StudyOverviewPane");
        RouteAsserts.propMatchesState(component, "study")
    });

    test("Renders the StudyInvestigatorsPane", async () => {
        const app = await RouteTesting.mountRouteWithTitle("Investigators");

        const component = RouteAsserts.find(app, "StudyInvestigatorsPane");
        RouteAsserts.propMatchesState(component, "investigators", "study.investigators");
        RouteAsserts.propMatchesState(component, "potentialInvestigators");
        RouteAsserts.propMatchesState(component, "onInvestigatorAdd");
        RouteAsserts.propMatchesState(component, "onInvestigatorUnassign");
        RouteAsserts.propMatchesState(component, "addInProgress");
        RouteAsserts.propMatchesState(component, "removeInProgress");
    });

    test("Renders the StudySubjectsPane", async () => {
        const app = await RouteTesting.mountRouteWithTitle("Participants");

        const component = RouteAsserts.find(app, "StudySubjectsPane");
        RouteAsserts.propMatchesState(component,"patientRegistrationApi");
        RouteAsserts.propMatchesState(component,"contactAddress");
        RouteAsserts.propMatchesState(component,"onRefresh");
        RouteAsserts.propMatchesState(component,"patientIdentifierSources");

        RouteAsserts.propMatchesState(component,"studyId", "study.id");
        RouteAsserts.propMatchesState(component,"subjects", "study.subjects");
        RouteAsserts.propMatchesState(component,"canSubjectsBeAdded", "study.permissions.canSubjectsBeAdded");
    });

    test("Renders the StudyEvents", async () => {
        const app = await RouteTesting.mountRouteWithTitle("Events");

        const component = RouteAsserts.find(app, "StudyEvents");
        RouteAsserts.propMatchesState(component, "eventApi");
        RouteAsserts.propMatchesState(component, "subjectObservationSummaryApi");
        RouteAsserts.propMatchesState(component, "subjectMigrationApi");
        RouteAsserts.propMatchesState(component, "studyPageUrl");
        RouteAsserts.propMatchesState(component, "populateEventActionText");
        RouteAsserts.propMatchesState(component, "onEdcSubjectReferenceChanged");

        RouteAsserts.propMatchesState(component, "studyId", "study.id");

        RouteAsserts.propMatches(component, "subjectId", pathVariables.match.params.subjectId);
    });

    test("Renders the StudyPopulatedEvent", async () => {
        const app = await RouteTesting.mountRouteWithTitle("Populated Event");

        const component = RouteAsserts.find(app, "StudyPopulatedEvent");
        RouteAsserts.propMatchesState(component, "onEdcSubjectReferenceChanged");

        RouteAsserts.propMatchesState(component, "studyId", "study.id");
        RouteAsserts.propMatchesState(component, "canSendToEDC", "study.permissions.canSubjectsBeAdded");
        RouteAsserts.propMatchesState(component, "contact", "contactAddress");

        RouteAsserts.propMatches(component, "subjectId", pathVariables.match.params.subjectId);
        RouteAsserts.propMatches(component, {});
    });

    test("Renders the StudyReviewedEvent with history hidden", async () => {
        const app = await RouteTesting.mountRouteWithTitle("Submitted Event");

        const component = RouteAsserts.find(app, "StudyReviewedEvent");
        RouteAsserts.propMatchesState(component, "onEdcSubjectReferenceChanged");

        RouteAsserts.propMatchesState(component, "studyId", "study.id");

        RouteAsserts.propMatches(component, "subjectId", pathVariables.match.params.subjectId);
        RouteAsserts.propMatches(component, "eventDefinitionId", pathVariables.match.params.eventDefinitionId);
        RouteAsserts.propMatches(component, "eventId", pathVariables.match.params.eventId);
        RouteAsserts.propMatches(component, "historyVisible", false);
    });
});

describe("Redirects correctly", () => {
    test("Redirects to StudyEvents", async () => {
        const app = await RouteTesting.shallowRedirectWithTitle(":edcSubjectReference");
        RouteAsserts.redirectsTo(app, `/${pathVariables.match.params.studyId}/subjects/${pathVariables.match.params.subjectId}/events`);
    });

    test("Redirects to StudyPopulatedEvent", async () => {
        const app = await RouteTesting.shallowRedirectWithTitle(":eventName");
        RouteAsserts.redirectsTo(app, `/${pathVariables.match.params.studyId}/subjects/${pathVariables.match.params.subjectId}/events/${pathVariables.match.params.eventDefinitionId}/forms`);
    });

});

const RouteTesting = function () {
    const _seenRoutes = [];
    const _findRouterProps = function (title) {
        const routeConfig = ehr2edcRoutes.find(
            route => route.navigation.title === title
        );
        const effectiveRoute = _buildEffectiveRouteFrom(routeConfig);
        return {routeConfig, effectiveRoute};
    };
    const _buildEffectiveRouteFrom = function (routeConfig) {
        let path = routeConfig.path;
        _seenRoutes.push(path);
        for (let key of Object.keys(pathVariables.match.params)) {
            path = path.replace(`:${key}`, pathVariables.match.params[key])
        }
        return path;
    };

    return {
        testedRoutes: _seenRoutes,
        mountRouteWithTitle: async function (title) {
            const {routeConfig, effectiveRoute} = _findRouterProps(title);
            return mount(<MemoryRouter initialEntries={[effectiveRoute]}>
                <Route path={routeConfig.path} render={routeConfig.render(state)}/>
            </MemoryRouter>);
        },
        shallowRedirectWithTitle: async function (title) {
            const {routeConfig, effectiveRoute} = _findRouterProps(title);
            return shallow(<MemoryRouter initialEntries={[effectiveRoute]}>
                {routeConfig.render()(pathVariables)}
            </MemoryRouter>);
        }
    }
}();
const RouteAsserts = function(){
    const _findInState = function (property) {
        let expected = state;
        if (!!property) {
            for (let prop of property.split(".")) {
                expected = expected[prop]
            }
        }
        return expected;
    };
    return {
        find: function (app, selector) {
            expect(app.exists(selector), `Could not find '${selector}'`).toBe(true);
            return app.find(selector);
        },
        redirectsTo: function (app, path) {
            const toProp = app.find("Redirect").prop('to');
            expect(toProp).toEqual(path);
        },
        propMatchesState: function (component, propName, stateName = propName) {
            const msg = `Expected component.props(${propName}) to match state[${stateName}]`;
            expect(component.prop(propName), msg).toEqual(_findInState(stateName));
        },
        propMatches: function (component, propName, expected) {
            const msg = `Expected component.props(${propName}) to match '${expected}'`;
            expect(component.prop(propName), msg).toEqual(expected);
        }
    }
}();
