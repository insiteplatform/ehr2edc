import React, {Component, Fragment} from "react"
import * as PropTypes from "prop-types";
import ExpandableText from "../../../../common/components/expandable-text/expandable-text";
import {Link} from "react-router-dom";
import {ReviewedEventDetails} from "./reviewed-event-details";
import {
    SearchResultsError,
    SearchResultsLoading
} from "../../../../common/components/search-result/search-result.helper";
import ProvenanceSidebar from "../study-populated-event-pane/provenance/provenance-sidebar";
import SelectableListing from "../../../../common/components/selectable-listing/selectable-listing";
import moment from "moment";

const GeneralInfo = () => {
    return <div className="grid-content padding-none shrink">
        <div className="alert-box small info margin-rl padding">
            <ExpandableText>
                <p>This page shows the data points that were reviewed on the given date for submission to the
                    sponsor's Electronic Data Capture (EDC) system for the given subject and event with the selected
                    reference date. At any point you can request the forms for this event to be repopulated and/or
                    reviewed and resubmitted. This page does not reflect the whole of all reviewed and submitted
                    data points, only those that have been approved for submission at the given date and time.</p>
            </ExpandableText>
        </div>
    </div>
};

const TitleBlock = ({historyVisible, onHideHistory, onShowHistory, resendFormsUrl}) => {
    return <div className="grid-block shrink">
        <div className="grid-block">
            <h3 className="subheader padding-top margin-medium-rl">Submitted Event</h3>
        </div>
        <div className="grid-block shrink padding">
            <HistoryButton historyVisible={historyVisible} onHideHistory={onHideHistory}
                           onShowHistory={onShowHistory}/>
            <Link className="button send-forms" to={resendFormsUrl}>Resend to EDC</Link>
        </div>
    </div>;
};

const HistoryButton = ({historyVisible, onHideHistory, onShowHistory}) => {
    if (historyVisible) {
        return <a className="button secondary hide-history" onClick={() => onHideHistory()}>
            <i className="fa fa-history"/>Hide history</a>;
    } else {
        return <a className="button secondary show-history" onClick={() => onShowHistory()}>
            <i className="fa fa-history"/>Show history</a>;
    }
};

const FormDetailsBlock = ({selectedReviewedEventId, subjectId, historyVisible, reviewer, reviewTime, populationTime, referenceDate, populator, reviewedForms, detailsLoading}) => {
    const hasReviewedForms = reviewedForms.length !== 0;
    return <Fragment>
        {detailsLoading && <SearchResultsLoading/>}
        {!detailsLoading && !hasReviewedForms && <SearchResultsError message="Cannot retrieve reviewed form data"/>}
        {!detailsLoading && hasReviewedForms && <ReviewedEventDetails reviewedEventId={selectedReviewedEventId}
                                                                      subjectId={subjectId}
                                                                      visible={historyVisible} reviewer={reviewer}
                                                                      reviewTime={reviewTime}
                                                                      populationTime={populationTime}
                                                                      referenceDate={referenceDate}
                                                                      populator={populator}
                                                                      reviewedForms={reviewedForms}/>}
    </Fragment>
};

export default class ReviewedEventOverview extends Component {
    render() {
        const {
            historyVisible, onHideHistory, onShowHistory, resendFormsUrl, historyItems, onHistoryItemSelected, selectedReviewedEventId,
            studyId, subjectId, reviewer, reviewTime, populationTime, referenceDate, populator, reviewedForms, detailsLoading
        } = this.props;
        return <Fragment>
            <TitleBlock historyVisible={historyVisible} onHideHistory={onHideHistory} onShowHistory={onShowHistory}
                        resendFormsUrl={resendFormsUrl}/>
            <GeneralInfo/>
            <div className="grid-block margin-top border-top">
                <SelectableListing items={this.mapHistoryItems(historyItems)}
                                   renderItemLabel={this.renderHistoryItemLabel}
                                   selectedId={selectedReviewedEventId} visible={historyVisible}
                                   onItemSelected={onHistoryItemSelected}>
                    <FormDetailsBlock selectedReviewedEventId={selectedReviewedEventId} subjectId={subjectId}
                                      historyVisible={historyVisible} reviewer={reviewer} reviewTime={reviewTime}
                                      populationTime={populationTime} referenceDate={referenceDate}
                                      populator={populator} reviewedForms={reviewedForms}
                                      detailsLoading={detailsLoading}/>
                </SelectableListing>
            </div>
            {reviewedForms.length !== 0 && <ProvenanceSidebar studyId={studyId}/>}
        </Fragment>
    }

    mapHistoryItems(historyItems) {
        return historyItems.map(item => this.mapHistoryItem(item));
    }

    mapHistoryItem(item) {
        return {...item, id: item.reviewedEventId}
    }

    renderHistoryItemLabel({reviewDateTime, reviewer}) {
        const date = moment(reviewDateTime);
        return `Submitted on ${date.format("ll [at] HH:mm")} by ${reviewer}`;
    }
}

ReviewedEventOverview.defaultProps = {
    historyItems: []
};

ReviewedEventOverview.propTypes = {
    reviewTime: PropTypes.string,
    reviewer: PropTypes.string,
    reviewedForms: PropTypes.arrayOf(PropTypes.object),
    populationTime: PropTypes.string,
    referenceDate: PropTypes.string,
    populator: PropTypes.string,
    resendFormsUrl: PropTypes.string,
    historyItems: PropTypes.arrayOf(PropTypes.object),
    detailsLoading: PropTypes.bool,
    selectedReviewedEventId: PropTypes.string,
    historyVisible: PropTypes.bool,
    studyId: PropTypes.string.isRequired,
    subjectId: PropTypes.string,
    onHistoryItemSelected: PropTypes.func,
    onShowHistory: PropTypes.func,
    onHideHistory: PropTypes.func
};