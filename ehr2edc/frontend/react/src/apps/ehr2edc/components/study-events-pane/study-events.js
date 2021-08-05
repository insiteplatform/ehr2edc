import React from "react"
import PopulateEventDialog from "../populate-event/populate-event-dialog"
import * as PropTypes from "prop-types";
import StudyEventsOverview from "./study-events-overview";
import ExpandableText from "../../../../common/components/expandable-text/expandable-text";
import {getEventPopulationReadiness, getSubject} from "../../../../api/api-ext";
import Notification from "../../../../common/components/notification";

const Title = () => <h3 className="subheader padding-tb margin-medium-rl">Events</h3>;

const GuidanceText = () => <div className="margin-rl padding-bottom">
    <div className="alert-box small info margin-small-rl padding">
        <ExpandableText>
            <p>This list contains all the events (scheduled visits and unscheduled events) for which data points
                can be automatically extracted. The number of data points that can be used to populate is
                displayed for
                each listed event. When populating the event it is possible to select a date in the past to
                allow
                retrospective data extraction. If the event is a scheduled visit, the selected date is typically
                equal to or immediately following the date at which the visit was performed. The date used for
                the
                last population for a given event is displayed next to the event name. Select the event name
                in order to review the data points that were used to populate.</p>
        </ExpandableText>
    </div>
</div>;

const MigrationInProgress = ({isReadyForPopulation, isSubjectMigrationRunning, error, events}) => {
    const migrationInProgress = !isReadyForPopulation && !!isSubjectMigrationRunning && !error && events.length > 0;
    return migrationInProgress &&
        <div className="margin-rl padding-bottom">
            <div className="alert-box alert warning margin-small-rl">
                <div className="events-error-container">
                    <p className="margin-bottom-none">
                        <i className="fa fa-circle-o-notch fa-spin margin-small-rl"/>
                        <span>The events are currently not ready for data point fetching.</span>
                    </p>
                </div>
            </div>
        </div>;
};

const Events = ({
                    error, events, areEventsLoading, isSubjectObservationSummaryLoading, isReadyForPopulation,
                    contactAddress, populateEventActionText, subjectId, emptyMessage, onEventHistoryClicked, onEventSelected
                }) => {
    const loading = areEventsLoading || isSubjectObservationSummaryLoading;
    return <StudyEventsOverview events={events} subjectId={subjectId}
                                error={error} contactAddress={contactAddress}
                                isLoading={loading}
                                isReadyForPopulation={isReadyForPopulation}
                                emptyMessage={emptyMessage} populateEventActionText={populateEventActionText}
                                onEventSelected={onEventSelected}
                                onEventHistoryClicked={onEventHistoryClicked}/>
};

const PopulationDialog = ({studyId, subjectId, populateEventActionText, selectedEvent, subjectObservationSummary, onClear, onFetchCompleted}) => {
    return selectedEvent !== "" &&
        <PopulateEventDialog studyId={studyId} selectedSubject={subjectId}
                             selectedEvent={selectedEvent}
                             subjectObservationSummary={subjectObservationSummary}
                             populateEventActionText={populateEventActionText}
                             open={true}
                             onClear={onClear}
                             onFetchCompleted={onFetchCompleted}/>;
};

export default class StudyEvents extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            areEventsLoading: true,
            isSubjectObservationSummaryLoading: true,
            inError: false,
            selectedEvent: "",
            events: [],
            subjectObservationSummary: [],
            isReadyForPopulation: undefined,
            populationNotReadyReason: undefined,
            isSubjectMigrationRunning: undefined
        };
        this.notifications = React.createRef();
        this.selectEvent = this.selectEvent.bind(this);
        this.unselectEvent = this.unselectEvent.bind(this);
        this.handleFetchCompleted = this.handleFetchCompleted.bind(this);
        this.pollPopulationReadinessUntilSubjectMigrationIsNotRunning = this.pollPopulationReadinessUntilSubjectMigrationIsNotRunning.bind(this);
        this.timeout = undefined;
    }

    componentDidMount() {
        this.pollPopulationReadinessUntilSubjectMigrationIsNotRunning();
        this.fetchEdcSubjectReference();
        this.fetchSubjectObservationSummary();
        this.fetchEvents();
    }

    componentWillUnmount() {
        if (this.timeout) {
            clearTimeout(this.timeout)
        }
    }

    render() {
        const {
            isReadyForPopulation, isSubjectMigrationRunning, error, events, selectedEvent,
            areEventsLoading, isSubjectObservationSummaryLoading, subjectObservationSummary
        } = this.state;
        const {studyId, contactAddress, populateEventActionText, subjectId, emptyMessage, onEventHistoryClicked} = this.props;
        return <>
            <Title/>
            <GuidanceText/>
            <MigrationInProgress isReadyForPopulation={isReadyForPopulation} error={error}
                                 isSubjectMigrationRunning={isSubjectMigrationRunning} events={events}/>
            <Events error={error} events={events} areEventsLoading={areEventsLoading}
                    isSubjectObservationSummaryLoading={isSubjectObservationSummaryLoading}
                    isReadyForPopulation={isReadyForPopulation} contactAddress={contactAddress}
                    populateEventActionText={populateEventActionText} subjectId={subjectId}
                    emptyMessage={emptyMessage} onEventHistoryClicked={onEventHistoryClicked}
                    onEventSelected={this.selectEvent}/>
            <PopulationDialog studyId={studyId} subjectId={subjectId}
                              selectedEvent={selectedEvent}
                              subjectObservationSummary={subjectObservationSummary}
                              populateEventActionText={populateEventActionText}
                              onClear={this.unselectEvent}
                              onFetchCompleted={this.handleFetchCompleted}/>
            <Notification ref={this.notifications}/>
        </>;
    }

    async fetchPopulationReadiness() {
        const {studyId, subjectId} = this.props;
        try {
            const response = await this.eventPopulationReadinessProxy(studyId, subjectId);
            this.processPopulationReadiness(!!response && response.data)
        } catch (response) {
            this.setState({
                inError: true,
                error: response,
                isReadyForPopulation: undefined,
                populationNotReadyReason: undefined,
                isSubjectMigrationRunning: undefined
            });
        }
    }

    async eventPopulationReadinessProxy(studyId, subjectId) {
        return await getEventPopulationReadiness(studyId, subjectId);
    }

    processPopulationReadiness({ready, notReadyReason, subjectMigrationInProgress}) {
        this.setState({
            isReadyForPopulation: ready,
            populationNotReadyReason: notReadyReason,
            isSubjectMigrationRunning: subjectMigrationInProgress
        });
    }

    fetchEvents() {
        const {subjectId, eventApi} = this.props;
        eventApi.getOne(subjectId)
            .then(
                (response) => {
                    this.setState({
                        areEventsLoading: false,
                        events: !!response ? response.data.eventDefinitionsInStudy : undefined
                    });
                },
                (response) => {
                    this.setState({
                        areEventsLoading: false,
                        inError: true,
                        error: response,
                        events: []
                    });
                }
            )
    }

    fetchSubjectObservationSummary() {
        const {subjectId, subjectObservationSummaryApi} = this.props;
        subjectObservationSummaryApi.getOne(subjectId)
            .then(
                (response) => {
                    this.setState({
                        isSubjectObservationSummaryLoading: false,
                        subjectObservationSummary: !!response ? response.data.summaryItems : undefined
                    });
                },
                (response) => {
                    this.setState({
                        isSubjectObservationSummaryLoading: false,
                        inError: true,
                        error: response,
                        subjectObservationSummary: []
                    });
                }
            )
    }

    selectEvent(eventName) {
        this.setState({selectedEvent: eventName});
    }

    unselectEvent() {
        this.setState({selectedEvent: ""});
    }

    handleFetchCompleted(populatedDataPoints) {
        this.showFetchCompletedNotification(populatedDataPoints);
        this.fetchEvents();
    }

    showFetchCompletedNotification(populatedDataPoints) {
        if (populatedDataPoints > 0) {
            this.notifications.current.show(`Successfully populated ` + populatedDataPoints + ` data points.`);
        } else {
            this.notifications.current.show(`Insufficient data for this subject relative to the selected reference date to populate forms.`, "warning");
        }
    }

    fetchEdcSubjectReference() {
        const {studyId, subjectId, onEdcSubjectReferenceChanged} = this.props;
        getSubject(studyId, subjectId).then(
            (response) => onEdcSubjectReferenceChanged(response.data.edcSubjectReference),
            this.handleError)

    }

    handleError(error) {
        this.setState({
            error: error
        });
    }

    async pollPopulationReadinessUntilSubjectMigrationIsNotRunning() {
        await this.fetchPopulationReadiness();
        const {isReadyForPopulation, isSubjectMigrationRunning} = this.state;
        if (!isReadyForPopulation && !!isSubjectMigrationRunning) {
            this.timeout = setTimeout(this.pollPopulationReadinessUntilSubjectMigrationIsNotRunning, 3000);
        } else {
            this.fetchSubjectObservationSummary();
        }
    }
}

StudyEvents.propTypes = {
    studyId: PropTypes.string,
    subjectId: PropTypes.string,
    eventApi: PropTypes.object,
    subjectObservationSummaryApi: PropTypes.object,
    emptyMessage: PropTypes.string,
    populateEventActionText: PropTypes.string,
    onEdcSubjectReferenceChanged: PropTypes.func,
    onEventHistoryClicked: PropTypes.func
};