import React from "react"
import {Link, Route, Switch} from "react-router-dom";
import {ehr2edcRoutes as routes} from "../../ehr2edc.routes";
import {
    SearchResultsError,
    SearchResultsLoading
} from "../../../../common/components/search-result/search-result.helper"
import {BreadcrumbLink} from "../../../../common/components/router/routing-breadcrumb";
import RequestError from "../../../../common/components/error/backend-request-error";
import Notification from "../../../../common/components/notification";
import {connect} from "react-redux";
import NavigationSidebar from "../navigation-sidebar/navigation-sidebar";
import api from "../../../../api/api";
import {
    eventDefinitionsApi,
    investigatorApi,
    patientIdentifierSourcesApi,
    patientRegistrationApiForStudyDetailsComponent,
    subjectObservationSummaryApi
} from "../../../../api/api-ext";

export class StudyDetails extends React.Component {

    constructor(props) {
        super(props);
        this.notifications = React.createRef();

        //const subjectStudyApi = subjectApi("/ehr2edc/study/${studyId}/subjects/${subjectId}");
        this.state = {
            studyPageUrl: props.hasOwnProperty("studyPageUrl") ? props.studyPageUrl : "/",
            errorMessage: props.hasOwnProperty("errorMessage") ? props.errorMessage : "No study data found",
            studyApi: api.posts("/ehr2edc/studies"),
            eventApi: eventDefinitionsApi(props.studyId),
            potentialInvestigatorApi: api.posts("/ehr2edc/users?status=unassigned&studyId=" + props.studyId),
            patientRegistrationApi: patientRegistrationApiForStudyDetailsComponent("/ehr2edc/studies/" + props.studyId + "/subjects"),

            investigatorApi: investigatorApi(props.studyId),
            patientIdentifierSourcesApi: patientIdentifierSourcesApi(props.studyId),
            subjectObservationSummaryApi: subjectObservationSummaryApi(props.studyId),
            potentialInvestigators: [],
            isStudyLoading: true,
            isPotentialInvestigatorsLoading: true,
            isPatientIdentifierSourcesLoading: true,
            inError: false,
            study: {
                id: props.studyId
            },
            populateEventActionText: props.populateEventActionText,
            patientIdentifierSources: [],
            contactAddress: props.contactAddress ? props.contactAddress : '',
            breadcrumbParams: {
                studyName: '',
                edcSubjectReference: '',
                eventName: ''
            },
            onInvestigatorAdd: investigator => this.assignInvestigator(investigator),
            onInvestigatorUnassign: investigator => this.unassignInvestigator(investigator),
            onEdcSubjectReferenceChanged: edcSubjectReference => this.updateEdcSubjectReference(edcSubjectReference),
            onEventChanged: event => this.updateEventName(event),
            onRefresh: () => this.fetchStudy()
        };
    }

    componentDidMount() {
        this.fetchStudy();
        this.fetchPotentialInvestigators();
        this.fetchPatientIdentifierSources();
    }

    async fetchStudy() {
        try {
            const response = await this.state.studyApi.getOne(this.state.study);
            this.setState({
                isStudyLoading: false,
                study: response.data
            });
            this.updateStudyName(response.data.name);
        } catch (response) {
            this.setState({
                isStudyLoading: false,
                inError: true,
                error: response
            });
        }
    }

    async fetchPotentialInvestigators() {
        try {
            const response = await this.state.potentialInvestigatorApi.getAll();
            this.setState({
                isPotentialInvestigatorsLoading: false,
                potentialInvestigators: response.data.potentialInvestigators
            });
        } catch (response) {
            this.setState({
                isPotentialInvestigatorsLoading: false,
                inError: true,
                error: response,
                potentialInvestigators: []
            })
        }
    }

    async fetchPatientIdentifierSources() {
        try {
            const response = await this.state.patientIdentifierSourcesApi.getAll();
            this.setState({
                isPatientIdentifierSourcesLoading: false,
                patientIdentifierSources: response.data
            });
        } catch (response) {
            this.setState({
                isPatientIdentifierSourcesLoading: false,
                inError: true,
                patientIdentifierSources: []
            })
        }
    }

    async assignInvestigator(investigator) {
        this.setAddInvestigatorInProgress(true);
        try {
            await this.state.investigatorApi.create({investigatorId: investigator.id});
            await this.fetchStudy();
            this.fetchPotentialInvestigators();
        } catch (response) {
            this.notifications.current.show(
                <RequestError error={response} contact={this.state.contactAddress}/>
                , "warning");
        }
        this.setAddInvestigatorInProgress(false)
    }

    async unassignInvestigator(investigator) {
        this.setRemoveInvestigatorInProgress(true);
        try {
            await this.state.investigatorApi.delete(investigator);
            await this.fetchStudy();
            this.fetchPotentialInvestigators();
        } catch (response) {
            this.notifications.current.show(
                <RequestError error={response} contact={this.state.contactAddress}/>
                , "warning")
        }
        this.setRemoveInvestigatorInProgress(false);
    }

    render() {
        const {study} = this.state;
        return !!study ? <>
            <NavigationSidebar study={study} studyId={study.id} studyName={study.name}/>
            {this.renderMainPane()}
            <Notification ref={this.notifications}/>
        </> : null;
    }

    renderMainPane() {
        return <div id={"study-details-main-pane"} className="grid-block vertical">
            {this.renderBreadcrumbs()}
            <div className="grid-block" id={"study-details-content-pane"}>
                <div className="grid-block vertical">
                    {this.renderContentPane()}
                </div>
            </div>
        </div>;
    }

    renderContentPane() {
        if (this.isLoading()) {
            return (<SearchResultsLoading/>);
        }
        if (this.state.inError) {
            return (
                <SearchResultsError message={this.state.errorMessage}>
                    <RequestError error={this.state.error} contact={this.state.contactAddress}/>
                </SearchResultsError>);
        }
        return this.renderStudyDetails();
    }

    isLoading() {
        return this.state.isStudyLoading || this.state.isPotentialInvestigatorsLoading || this.state.isPatientIdentifierSourcesLoading;
    }

    renderBreadcrumbs() {
        return <ul id="page-bread-crumbs" className="bread-crumbs shrink grid-block border-bottom">
            <li><Link to={"/"}>EHR2EDC Studies</Link></li>
            {this.renderStudyNameBreadcrumb()}
            {this.renderChildBreadcrumbs()}
        </ul>;
    }

    renderStudyNameBreadcrumb() {
        return routes.filter(route => route.default)
            .map((route, index) => {
                return (<li key={index} className="breadcrumb-study">
                    <Link to={route.path}>
                        {this.state.study.name}
                    </Link>
                </li>)
            });
    }

    renderChildBreadcrumbs() {
        return routes
            .sort((a, b) => a.navigation.order - b.navigation.order)
            .map((route, index) => {
                return (<BreadcrumbLink key={index}
                                        path={route.path}
                                        default={route.default}
                                        title={route.navigation.title}
                                        breadcrumbParams={this.state.breadcrumbParams}
                />)
            });
    }

    renderStudyDetails() {
        return (
            <Switch>
                {routes
                    .sort((a, b) => a.navigation.order - b.navigation.order)
                    .reverse()
                    .map((route, index) => this.toRoute(index, route))}
                {routes
                    .filter(route => route.default)
                    .map((route, index) => this.toRoute(index, route))[0]
                }
            </Switch>);
    }

    toRoute(index, route) {
        return (<Route
            key={index}
            path={route.path}
            render={route.render(this.state)}/>);
    }

    setAddInvestigatorInProgress(inProgress) {
        this.setState({
            addInvestigatorInProgress: inProgress
        })
    }

    setRemoveInvestigatorInProgress(inProgress) {
        this.setState({
            removeInvestigatorInProgress: inProgress
        })
    }

    updateEdcSubjectReference(edcSubjectReference) {
        this.setState({
            breadcrumbParams: {...this.state.breadcrumbParams, edcSubjectReference: edcSubjectReference}
        })
    }

    updateEventName(event) {
        this.setState({
            breadcrumbParams: {...this.state.breadcrumbParams, eventName: event.name}
        })
    }

    updateStudyName(studyName) {
        this.setState({
            breadcrumbParams: {...this.state.breadcrumbParams, studyName: studyName}
        })
    }
}

export const mapStateToProps = function (state) {
    const {itemProvenance} = state;
    return {
        hideSidebar: Object.keys(itemProvenance).length !== 0
    }
};

export default connect(mapStateToProps)(StudyDetails);