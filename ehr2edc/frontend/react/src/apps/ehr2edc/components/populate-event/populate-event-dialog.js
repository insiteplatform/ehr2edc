import React, {Fragment} from "react";
import Modal, {ModalErrors} from "../../../../common/components/react-modal/modal";
import axios from "axios";
import SubjectObservationSummaryGraph from "./subject-observation-summary-graph";
import moment from 'moment';
import * as PropTypes from "prop-types";
import ExpandableText from "../../../../common/components/expandable-text/expandable-text";
import styled from "styled-components";
import {DateInput} from "@blueprintjs/datetime";

const dialogDimensions = {
    contentWidth: 950,
    contentHeight: 730,
    emptyContentWidth: 700,
    emptyContentHeight: 392,
    errorSegmentHeight: 70,
    errorItemHeight: 30
};

const ReferenceDateGuidance = styled.div.attrs(() => ({
    "data-cy": "population-guidance"
}))`
line-height: 1.5;
position: relative;
margin: 1rem 0 0 0 !important;
padding: 0.15rem 0.5rem 0.1rem;
font-size: 0.875rem;
border-left: 2px solid #CCCCCC;
background-color: #F7F7F7;
color: #535353;    
`;

export default class PopulateEventDialog extends React.Component {

    constructor(data) {
        super(data);
        this.state = {
            populateEventActionText: data.hasOwnProperty("populateEventActionText") ? data.populateEventActionText : "Populate Event",
            referenceDate: moment(),
            errors: [],
            error: undefined
        };

        this.handleChangeFetchDate = this.handleChangeFetchDate.bind(this);
        this.handleFetchStart = this.handleFetchStart.bind(this);
        this.clearDialog = this.clearDialog.bind(this);
    }

    render() {
        return <Fragment>
            <Modal title={this.state.populateEventActionText}
                   width={this.calculateModalWidth() + "px"} height={(this.calculateModalHeight()) + "px"}
                   open={this.props.open} inProgress={this.state.inProgress}
                   onOkButtonClick={this.handleFetchStart} onClose={this.clearDialog}>
                {this.renderInfoText()}
                <table className="margin-bottom-none">
                    <tbody>
                    <tr>
                        <th>Reference date:</th>
                        <td data-cy={"date-input-container"}>{this.renderReferenceDateField()}</td>
                    </tr>
                    <tr>
                        <th>Subject:</th>
                        <td data-cy={"subject-id-container"}>{this.props.selectedSubject}</td>
                    </tr>
                    <tr>
                        <th>Event:</th>
                        <td data-cy={"event-id-container"}>{this.props.selectedEvent}</td>
                    </tr>
                    </tbody>
                </table>
                {this.renderErrors()}
            </Modal>
        </Fragment>;
    }

    renderInfoText() {
        if (this.hasNoObservationSummaryItems()) {
            return <ReferenceDateGuidance>
                Please select a reference date for populating the event on the graph. If this event is a scheduled
                visit, the selected date should match or closely follow the date at which the visit was performed.
                The system will attempt to extract the defined data points for the selected event from the clinical
                observations available for the selected reference date. This action can be repeated for the same
                event with a different reference date, e.g. when no suitable data could be found for the selected
                date.
            </ReferenceDateGuidance>
        }
        return <div className="alert-box small info margin-small padding" data-cy="population-guidance">
            <ExpandableText>
                <p>This graph shows the number of available clinical observations for the current subject over time.
                    Please select a reference date for populating the event on the graph. If this event is a scheduled
                    visit, the selected date should match or closely follow the date at which the visit was performed.
                    The system will attempt to extract the defined data points for the selected event from the clinical
                    observations available for the selected reference date. This action can be repeated for the same
                    event with a different reference date, e.g. when no suitable data could be found for the selected
                    date.
                </p>
            </ExpandableText>
        </div>;
    }

    renderReferenceDateField() {
        if (this.hasNoObservationSummaryItems()) {
            return <Fragment>
                <DateInput
                    canClearSelection={false}
                    onChange={this.handleChangeFetchDate}
                    formatDate={date => moment(date).format("LL")}
                    parseDate={(str) => new Date(str)}
                    value={this.state.referenceDate.toDate()}
                />
            </Fragment>;
        } else {
            return <SubjectObservationSummaryGraph
                items={this.props.subjectObservationSummary}
                dateSelected={this.state.referenceDate}
                onDateSelected={this.handleChangeFetchDate}/>;
        }
    }

    calculateModalWidth() {
        return this.hasNoObservationSummaryItems() ? dialogDimensions.emptyContentWidth : dialogDimensions.contentWidth;
    }

    calculateModalHeight() {
        const contentHeight = this.hasNoObservationSummaryItems() ? dialogDimensions.emptyContentHeight : dialogDimensions.contentHeight;
        let errorHeight = 0;
        if (!this.isValid()) {
            errorHeight = dialogDimensions.errorSegmentHeight + this.state.errors.length * dialogDimensions.errorItemHeight;
        }
        return contentHeight + errorHeight;
    }

    hasNoObservationSummaryItems() {
        return !this.props.hasOwnProperty("subjectObservationSummary") || this.props.subjectObservationSummary.length === 0;
    }

    handleChangeFetchDate(date) {
        this.setState({referenceDate: moment(date)});
    }

    referenceDateInError() {
        const referenceDateField = "referenceDate";
        return !this.isValid() &&
            (this.state.errors.some(error => error === "Reference date is required") ||
                requestErrorHasField(this.state.error, referenceDateField));
    }

    async handleFetchStart() {
        if (this.getValidationErrors().length === 0) {
            this.setFetchInProgress(true);
            await this.postFetchRequest();
            this.setFetchInProgress(false);
        } else {
            this.validate();
        }
    }

    async postFetchRequest() {
        try {
            const response = await axios.post(this.buildUrl(), {
                referenceDate: this.toISODateString(this.state.referenceDate),
                eventDefinitionId: this.props.selectedEvent
            });
            this.handleSuccessfulRequest(response);
        } catch (response) {
            this.setState({
                error: response
            })
        }
    }

    handleSuccessfulRequest(success) {
        this.clearDialog();
        this.props.onFetchCompleted(success.data.populatedDataPoints);
    }

    buildUrl() {
        const {studyId, selectedSubject} = this.props;
        return `/ehr2edc/studies/${studyId}/subjects/${selectedSubject}/events`;
    }

    toISODateString(date) {
        const dateString = date.toISOString();
        return dateString.substring(0, dateString.indexOf('T'));
    }

    isValid() {
        return (!this.state.errors || this.state.errors.length === 0)
            && !this.state.error;
    }

    validate() {
        const errors = this.getValidationErrors();
        this.setState({errors: errors})
    }

    getValidationErrors() {
        const errors = [];
        if (!this.props.studyId || this.props.studyId === "") {
            errors.push("Study identifier is required");
        }
        if (!this.props.selectedEvent || this.props.selectedEvent === "") {
            errors.push("Event identifier is required");
        }
        if (!this.props.selectedSubject || this.props.selectedSubject === "") {
            errors.push("Subject identifier is required");
        }
        if (this.isReferenceDateInvalid()) {
            errors.push("Reference date is required");
        }
        return errors;
    }

    isReferenceDateInvalid() {
        const {referenceDate} = this.state;
        return !(referenceDate.isValid()) || (!this.hasNoObservationSummaryItems() && !this.observationSummaryContainsReferenceDate());
    }

    observationSummaryContainsReferenceDate() {
        const {subjectObservationSummary} = this.props;
        const {referenceDate} = this.state;
        const observationDates = subjectObservationSummary.map(item => moment.utc(item.date).format("LLL"));
        return observationDates.indexOf(referenceDate.format("LLL")) !== -1;
    }

    clearDialog() {
        this.setState({
            referenceDate: moment(Date.now()),
            errors: [],
            error: undefined
        });
        if (this.props.hasOwnProperty("onClear")) {
            this.props.onClear();
        }
    }

    renderErrors() {
        if (this.isValid()) {
            return <Fragment/>
        }
        return <ModalErrors validationErrors={this.state.errors}
                            requestError={this.state.error}
                            contact={this.props.contactAddress}/>
    }

    setFetchInProgress(inProgress) {
        this.setState({
            inProgress: inProgress
        })
    }
}

PopulateEventDialog.propTypes = {
    onClear: PropTypes.func,
    onFetchCompleted: PropTypes.func,
    subjectObservationSummary: PropTypes.arrayOf(PropTypes.object)
};