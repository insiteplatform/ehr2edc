import React, {Fragment} from "react"
import {
    SearchResultsError,
    SearchResultsLoading
} from "../../../../common/components/search-result/search-result.helper";
import ReactTable from "react-table";
import ReactTablePager from "../../../../common/components/react-table/react-table-pager";
import {Link} from "react-router-dom";
import moment from 'moment';
import * as PropTypes from "prop-types";
import RequestError from "../../../../common/components/error/backend-request-error";

export default class StudyEventsOverview extends React.Component {
    constructor(props) {
        super(props);
        this.selectEvent = this.selectEvent.bind(this);
    }

    render() {
        const {events, isLoading, error} = this.props;
        if (isLoading) {
            return <SearchResultsLoading/>;
        }
        if (error) {
            return this.renderError();
        }
        if (events && events.length === 0) {
            return this.renderEmpty();
        }
        return this.renderEventTable();
    }

    renderError() {
        const {emptyMessage, error, contactAddress} = this.props;
        return <SearchResultsError message={emptyMessage}>
            <RequestError error={error} contact={contactAddress}/>
        </SearchResultsError>;
    }

    renderEmpty() {
        return <SearchResultsError message={this.props.emptyMessage}/>;
    }

    renderEventTable() {
        const columns = [
            {
                Header: "Event",
                sortable: false,
                filterable: false,
                show: true,
                Cell: colData => this.renderEventNameColumn(colData.original)
            }, {

                Header: "No. of forms",
                accessor: "formDefinitions",
                sortable: false,
                filterable: false,
                headerClassName: "small-2",
                className: "small-2",
                Cell: (colData) => {
                    return Array.isArray(colData.value) ? colData.value.length : 0
                }
            }, {
                Header: "No. of data points",
                accessor: "queryCount",
                sortable: false,
                filterable: false,
                headerClassName: "small-2",
                className: "small-2"
            }, {
                Header: "Last Populated",
                sortable: false,
                filterable: false,
                show: true,
                headerClassName: "small-2",
                className: "small-2",
                Cell: (colData) => {
                    return this.renderPopulationDateColumn(colData.original);
                }
            }, {
                Header: "Actions",
                sortable: false,
                filterable: false,
                show: true,
                headerClassName: "small-3",
                className: "small-3 button-cell",
                Cell: (colData) => {
                    return this.renderActionButtons(colData.original);
                }
            }
        ];
        return <ReactTable data={this.props.events} columns={columns} resizable={false} minRows={0}
                           defaultPageSize={100} pageSizeOptions={[50, 100, 150, 200]}
                           PaginationComponent={ReactTablePager}/>;
    }

    renderEventNameColumn(event) {
        if (this.eventWasFetched(event)) {
            return <Link to={`events/${event.eventDefinitionId}`}>
                {event.name}&nbsp;{this.getReferenceDateText(event)}
            </Link>
        } else {
            return <span title={this.props.eventNotFetchedMessage}>{event.name}</span>;
        }
    }

    eventWasFetched(event) {
        return this.hasEventId(event);
    }

    renderPopulationDateColumn({lastPopulationTime}) {
        if (this.hasPopulationTime(lastPopulationTime)) {
            return this.formatTime(lastPopulationTime);
        }
        return null
    }

    hasPopulationTime(lastPopulationTime) {
        return lastPopulationTime !== undefined && lastPopulationTime !== null;
    }

    getReferenceDateText({lastReferenceDate}) {
        if (this.hasReferenceDate(lastReferenceDate)) {
            return <small>({this.formatDate(lastReferenceDate)})</small>;
        }
        return <Fragment/>
    }

    hasReferenceDate(lastReferenceDate) {
        return lastReferenceDate !== undefined && lastReferenceDate !== null;
    }

    formatDate(dateString) {
        return moment(dateString).format('LL')
    }

    formatTime(dateString) {
        return moment(dateString).format('ll [at] HH:mm')
    }

    renderActionButtons(event) {
        return <Fragment>
            {this.renderPopulateButton(event)}
            {this.renderHistoryButton(event)}
        </Fragment>;
    }

    renderPopulateButton(event) {
        if (this.isPopulateButtonEnabled(event)) {
            return (<a className={"button tiny fetch-event fetch-event-" + event.eventDefinitionId}
                       data-cy={"populate-event-btn"}
                       onClick={() => this.selectEvent(event.eventDefinitionId)}>
                <i className="fa fa-plus"/>{this.props.populateEventActionText}
            </a>)
        } else {
            return (<div title={this.generateFetchButtonTooltip(event)}>
                <a className={"button tiny disabled fetch-event fetch-event-" + event.eventDefinitionId}>
                    <i className="fa fa-plus"/>{this.props.populateEventActionText}
                </a>
            </div>)
        }
    }

    renderHistoryButton(event) {
        if (!event.historyAvailable) {
            return null;
        }
        return <Link className="button tiny secondary"
                     to={{
                         pathname: `events/${event.eventDefinitionId}/${event.eventId}/reviewed`,
                         state: {
                             historyVisible: true
                         }
                     }}
                     onClick={() => this.props.onEventHistoryClicked(event)}>
            <i className="fa fa-history"/>Submission History
        </Link>;

    }

    isPopulateButtonEnabled(event) {
        return this.eventHasForms(event) && this.eventHasQueries(event) && this.isReadyForPopulation();
    }

    generateFetchButtonTooltip(event) {
        if (!this.eventHasForms(event)) {
            return "This event has no associated forms";
        }
        if (!this.eventHasQueries(event)) {
            return "This event has no associated data points to be extracted"
        }
    }

    eventHasForms(event) {
        return event.formCount !== 0;
    }

    eventHasQueries(event) {
        return event.queryCount !== 0;
    }

    selectEvent(eventName) {
        this.props.onEventSelected(eventName);
    }

    hasEventId(event) {
        return event.eventId !== undefined && event.eventId !== null;
    }

    isReadyForPopulation() {
        return this.props.isReadyForPopulation !== undefined && this.props.isReadyForPopulation ;
    }
}

StudyEventsOverview.propTypes = {
    events: PropTypes.arrayOf(PropTypes.object),
    subjectId: PropTypes.string,
    isLoading: PropTypes.bool,
    error: PropTypes.object,
    emptyMessage: PropTypes.string,
    populateEventActionText: PropTypes.string,
    eventNotFetchedMessage: PropTypes.string,
    contactAddress: PropTypes.string,
    onEventSelected: PropTypes.func,
    onEventHistoryClicked: PropTypes.func
};
StudyEventsOverview.defaultProps = {
    emptyMessage: "No events present for the study",
    populateEventActionText: "Populate Event",
    eventNotFetchedMessage: "No data points have been fetched for the event yet"
};