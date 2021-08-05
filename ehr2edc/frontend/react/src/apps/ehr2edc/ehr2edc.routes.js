import React from "react";
import {Redirect} from "react-router-dom";

import StudyInvestigatorsPane from "./components/study-investigators-pane/study-investigators-pane";
import StudySubjectsPane from "./components/study-subjects-pane/study-subjects-pane";
import StudyOverviewPane from "./components/study-overview-pane/study-overview-pane";
import StudyEvents from "./components/study-events-pane/study-events";
import StudyPopulatedEvent from "./components/study-populated-event-pane/study-populated-event";
import StudyReviewedEvent from "./components/study-reviewed-event-pane/study-reviewed-event";

/*
path: Router-path attribute
render: Router-render function
navigation: Sidebar navigation
    order: order in sidebar
    title: title of navigation link
 */
export const ehr2edcRoutes = [
    {
        path: "/:studyId/",
        isTab: true,
        isActivate: () => {
            return true
        },
        render: (state) => {
            return props => <StudyOverviewPane {...props} study={state.study}/>
        },
        navigation: {
            order: 0,
            title: ":studyName"
        }
    },
    {
        path: "/:studyId/investigators",
        isTab: true,
        isActivate: () => {
            return true
        },
        render: (state) => {
            return props => <StudyInvestigatorsPane {...props}
                                                    investigators={state.study.investigators}
                                                    potentialInvestigators={state.potentialInvestigators}
                                                    onInvestigatorAdd={state.onInvestigatorAdd}
                                                    onInvestigatorUnassign={state.onInvestigatorUnassign}
                                                    addInProgress={state.addInvestigatorInProgress}
                                                    removeInProgress={state.removeInvestigatorInProgress}
            />
        },
        navigation: {
            order: 1,
            title: "Investigators"
        }
    },
    {
        path: "/:studyId/subjects",
        isTab: true,
        isActivate: (props) => {
            return props.study.permissions && props.study.permissions.canSubjectsBeViewed;
        },
        render: (state) => {
            return props => <StudySubjectsPane {...props}
                                               studyId={state.study.id}
                                               subjects={state.study.subjects}
                                               canSubjectsBeViewed={state.study.permissions.canSubjectsBeViewed}
                                               patientRegistrationApi={state.patientRegistrationApi}
                                               contactAddress={state.contactAddress}
                                               canSubjectsBeAdded={state.study.permissions.canSubjectsBeAdded}
                                               onRefresh={state.onRefresh}
                                               patientIdentifierSources={state.patientIdentifierSources}/>
        },
        navigation: {
            order: 2,
            title: "Participants"
        }
    },
    {
        path: "/:studyId/subjects/:subjectId",
        render: () => {
            return props => {
                return <Redirect to={`/${props.match.params.studyId}/subjects/${props.match.params.subjectId}/events`}/>
            }
        },
        navigation: {
            order: 2.1,
            title: ":edcSubjectReference"
        }
    },
    {
        path: "/:studyId/subjects/:subjectId/events",
        render: (state) => {
            return props => {
                return <StudyEvents {...props}
                                    studyId={state.study.id}
                                    subjectId={props.match.params.subjectId}
                                    eventApi={state.eventApi}
                                    subjectMigrationApi={state.subjectMigrationApi}
                                    subjectObservationSummaryApi={state.subjectObservationSummaryApi}
                                    studyPageUrl={state.studyPageUrl}
                                    populateEventActionText={state.populateEventActionText}
                                    onEdcSubjectReferenceChanged={state.onEdcSubjectReferenceChanged}
                                    onEventHistoryClicked={state.onEventChanged}
                                    contactAddress={state.contactAddress}
                />
            }
        },
        navigation: {
            order: 2.2,
            title: "Events"
        }
    },
    {
        path: "/:studyId/subjects/:subjectId/events/:eventDefinitionId",
        render: () => {
            return props => {
                return <Redirect
                    to={`/${props.match.params.studyId}/subjects/${props.match.params.subjectId}/events/${props.match.params.eventDefinitionId}/forms`}/>
            }
        },
        navigation: {
            order: 2.3,
            title: ":eventName"
        }
    },
    {
        path: "/:studyId/subjects/:subjectId/events/:eventDefinitionId/forms",
        render: (state) => {
            return props => {
                return <StudyPopulatedEvent canSendToEDC={state.study.permissions.canSendToEDC}
                                            studyId={state.study.id}
                                            eventDefinitionId={props.match.params.eventDefinitionId}
                                            eventId={props.match.params.eventId}
                                            subjectId={props.match.params.subjectId}
                                            history={props.history}
                                            contact={state.contactAddress}
                                            onEdcSubjectReferenceChanged={state.onEdcSubjectReferenceChanged}
                                            onEventChanged={state.onEventChanged}
                />
            }
        },
        navigation: {
            order: 2.4,
            title: "Populated Event"
        }
    },
    {
        path: "/:studyId/subjects/:subjectId/events/:eventDefinitionId/:eventId/reviewed",
        render: (state) => {
            return props => {
                const historyVisible = !!props.location.state ? props.location.state.historyVisible : false;
                return <StudyReviewedEvent studyId={state.study.id}
                                           subjectId={props.match.params.subjectId}
                                           eventDefinitionId={props.match.params.eventDefinitionId}
                                           eventId={props.match.params.eventId}
                                           onEdcSubjectReferenceChanged={state.onEdcSubjectReferenceChanged}
                                           historyVisible={!!historyVisible}
                />
            }
        },
        navigation: {
            order: 2.5,
            title: "Submitted Event"
        }
    }
];