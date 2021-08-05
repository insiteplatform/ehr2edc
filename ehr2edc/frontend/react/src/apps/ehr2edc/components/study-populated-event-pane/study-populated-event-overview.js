import React, {Component, Fragment} from "react"
import * as PropTypes from "prop-types";
import moment from "moment";
import 'core-js/fn/array/flat-map';
import ExpandableText from "../../../../common/components/expandable-text/expandable-text";
import {Link} from "react-router-dom";
import {PopulatedEventHistoryItemDetails} from "./populated-event-history-item-details";
import {
    SearchResultsError,
    SearchResultsLoading
} from "../../../../common/components/search-result/search-result.helper";
import SelectableListing from "../../../../common/components/selectable-listing/selectable-listing";

const Title = () => (
    <div className="grid-block">
        <h3 className="subheader padding-tb margin-medium-rl">Populated event overview</h3>
    </div>
);

const SubmitButtonLoading = () => (
    <div className="grid-block shrink padding-tr" title="Event submission in progress">
        <a className="button send-forms in-progress">
            Send to EDC
            <div className={"bar"}/>
        </a>
    </div>
);

const SubmitButtonDisabled = ({canSendToEDC}) => {
    const title = canSendToEDC ? "No form items have been selected yet" : "No writable EDC configured";
    return <div className="grid-block shrink padding-tr" title={title}>
        <a className="button send-forms disabled">Send to EDC</a>
    </div>;
};

const SubmitButtonEnabled = ({onSubmit}) => (
    <div className="grid-block shrink padding-tr">
        <a className="button send-forms" onClick={() => onSubmit()}>Send to EDC</a>
    </div>
);

const SubmitButton = ({inProgress, canSendToEDC, selectedItems, onSubmit}) => {
    if (inProgress) {
        return <SubmitButtonLoading/>;
    } else if (!canSendToEDC || selectedItems.length === 0) {
        return <SubmitButtonDisabled canSendToEDC={canSendToEDC}/>
    } else {
        return <SubmitButtonEnabled onSubmit={onSubmit}/>
    }
};

const HideHistoryButton = ({onHideHistory}) => (
    <a className="button secondary margin-tr margin-left-none hide-history"
       onClick={() => onHideHistory()}>
        <i className="fa fa-history"/>Hide population history
    </a>
);

const ShowHistoryButton = ({onShowHistory}) => (
    <a className="button secondary margin-tr margin-left-none show-history"
       onClick={() => onShowHistory()}>
        <i className="fa fa-history"/>Show population history
    </a>
);

const HistoryButton = ({historyVisible, onHideHistory, onShowHistory}) => {
    return historyVisible ? <HideHistoryButton onHideHistory={onHideHistory}/> :
        <ShowHistoryButton onShowHistory={onShowHistory}/>
};

const HeadingButtons = ({forms, inProgress, canSendToEDC, selectedItems, onSubmit, historyVisible, onHideHistory, onShowHistory}) => (
    <Fragment>
        {forms.length !== 0 &&
        <SubmitButton inProgress={inProgress} canSendToEDC={canSendToEDC} selectedItems={selectedItems}
                      onSubmit={onSubmit}/>}
        <HistoryButton historyVisible={historyVisible} onHideHistory={onHideHistory} onShowHistory={onShowHistory}/>
    </Fragment>
);

export default class StudyPopulatedEventOverview extends Component {
    constructor(props) {
        super(props);
        this.state = {};
        this.handleSendButtonClicked = this.handleSendButtonClicked.bind(this);
    }

    componentDidUpdate(prevProps) {
        const {error} = this.props;
        if (error !== prevProps.error) {
            this.transform(error);
        }
    }

    render() {
        return <Fragment>
            {this.renderHeading()}
            {this.renderInformation()}
            <div className="grid-block margin-top border-top">
                {this.renderPopulatedEventHistoryListing()}
            </div>
        </Fragment>
    }

    renderPopulatedEventHistoryListing() {
        const {historyItems, historyVisible, selectedEventId, onSelectedHistoryItemChanged} = this.props;
        const items = historyItems.map(item => this.mapHistoryItem(item));
        return <SelectableListing items={items}
                                  renderItemLabel={this.renderHistoryItem}
                                  onItemSelected={onSelectedHistoryItemChanged}
                                  visible={historyVisible}
                                  selectedId={selectedEventId}>
            {this.renderPopulatedEventHistoryDetailsPanel()}
        </SelectableListing>;
    }

    mapHistoryItem(item) {
        return {...item, id: item.eventId}
    }

    renderHistoryItem(historyItem) {
        const populationDate = moment(historyItem.populationTime);
        const referenceDate = moment(historyItem.referenceDate);
        const populator = historyItem.populator === null?  "":` by ${historyItem.populator}`;

        return `Populated on ${populationDate.format("ll [at] HH:mm")}${populator}, reference date ${referenceDate.format("ll")}`;
    }

    renderPopulatedEventHistoryDetailsPanel() {
        const {historyItemInError, historyItemLoading} = this.props;
        if (historyItemInError) {
            return <SearchResultsError message="Could not retrieve the details for the populated event"/>
        }
        if (historyItemLoading) {
            return <SearchResultsLoading/>
        }
        return this.renderHistoryItemDetails();
    }

    renderHistoryItemDetails() {
        const {transformedError} = this.state;
        const {contact, forms, selectedItems, error, studyId, subjectId, eventId, actions} = this.props;
        return <PopulatedEventHistoryItemDetails
            selectedEvent={this.resolveSelectedEvent()}
            transformedError={transformedError}
            contact={contact}
            forms={forms}
            selectedItems={selectedItems}
            error={error}
            studyId={studyId}
            subjectId={subjectId}
            eventId={eventId}
            actions={actions}/>;
    }

    resolveSelectedEvent() {
        const {historyItems, selectedEventId} = this.props;
        return historyItems.filter(event => event.eventId === selectedEventId)[0];
    }

    renderSubmitInformation() {
        const {lastSubmissionTime, reviewedFormsUrl} = this.props;
        if (lastSubmissionTime === null) {
            return <Fragment/>
        }
        return <div className="margin-top">
            <div className="alert-box info margin-rl padding">
                <p className="margin-bottom-none"><i className="fa fa-info-circle"/> This event was submitted
                    on {moment(lastSubmissionTime).format("LL")}</p>
                <Link className="button link small padding-none" style={{height: "auto"}} to={reviewedFormsUrl}>
                    Show details
                </Link>
            </div>
        </div>;
    }

    renderInformation() {
        return <div className="study-form-info-container">
            {this.renderInfoMessage()}
            {this.renderSubmitInformation()}
        </div>;
    }

    renderHideLink() {
        return <div className="study-form-info-links">

        </div>
    }

    renderInfoMessage() {
        return <div className="alert-box small info margin-rl padding">
            <ExpandableText>
                <p>This page shows all the data points that were populated for the given subject and event with the
                    selected reference date. You can fetch data points for a given subject and event multiple times
                    in which case the data points displayed here will be updated accordingly. From this view, you
                    can select all extracted data points that are to be submitted to the Electronic Data Capture
                    (EDC) system linked to this study (if any).</p>
            </ExpandableText>
        </div>;
    }

    renderHeading() {
        const {forms, inProgress, canSendToEDC, selectedItems, historyVisible, onHideHistory, onShowHistory} = this.props;
        return <div className="grid-block shrink">
            <Title/>
            <HeadingButtons forms={forms} inProgress={inProgress} canSendToEDC={canSendToEDC}
                            selectedItems={selectedItems} onSubmit={this.handleSendButtonClicked}
                            historyVisible={historyVisible} onHideHistory={onHideHistory}
                            onShowHistory={onShowHistory}/>
        </div>;
    }

    handleSendButtonClicked() {
        this.props.onReviewedFormsSend();
    }

    transform(error) {
        const transformedIssues = this.issuesFrom(error)
            .map(this.wrapIssueMessageInAnchor());
        this.setState({
            transformedError: {
                response: {
                    data: {
                        issues: transformedIssues
                    }
                }
            }
        })
    }

    issuesFrom({response}) {
        const {data} = response;
        if (!!data && !!data.issues) {
            return data.issues;
        }
        return [];
    }

    wrapIssueMessageInAnchor() {
        return issue => {
            return {
                reference: issue.reference,
                message: issue.field ? (<a onClick={() => this.scrollTo(issue.field)}>
                    {issue.message}
                </a>) : issue.message
            };
        };
    }

    scrollTo(id) {
        this.expandParentForm(id);
        const target = document.getElementById(id),
            container = document.getElementById("study-form-overview-overview");

        const offsetParent = target.offsetParent;
        if (!!offsetParent) {
            const scroll = target.offsetTop - (offsetParent.clientHeight - container.clientHeight);
            this.animateScrollTo(container, scroll)
        }
    }

    expandParentForm(id) {
        document.getElementById(id)
            .closest(".form")
            .querySelector(".collapsed")
            .checked = false
    }

    animateScrollTo(element, to, duration = 250) {
        let start = element.scrollTop,
            change = to - start,
            currentTime = 0,
            increment = 20;

        let animateScroll = function () {
            currentTime += increment;
            element.scrollTop = Math.easeInOutQuad(currentTime, start, change, duration);
            if (currentTime < duration) {
                setTimeout(animateScroll, increment);
            }
        };
        animateScroll();
    }
}

Math.easeInOutQuad = function (t, b, c, d) {
    t /= d / 2;
    if (t < 1) return c / 2 * t * t + b;
    t--;
    return -c / 2 * (t * (t - 2) - 1) + b;
};

StudyPopulatedEventOverview.defaultProps = {
    forms: [],
    selectedItems: [],
    lastSubmissionTime: null,
    onShowHistory: () => {
    },
    onHideHistory: () => {
    },
    historyItems: []
};
StudyPopulatedEventOverview.propTypes = {
    forms: PropTypes.arrayOf(PropTypes.object),
    selectedItems: PropTypes.arrayOf(PropTypes.string),
    lastSubmissionTime: PropTypes.string,
    historyItems: PropTypes.arrayOf(PropTypes.object),
    historyVisible: PropTypes.bool.isRequired,
    historyItemLoading: PropTypes.bool.isRequired,
    historyItemInError: PropTypes.bool.isRequired,
    error: PropTypes.object,
    reviewedFormsUrl: PropTypes.string,
    onItemSelected: PropTypes.func,
    onItemDeselected: PropTypes.func,
    onGroupSelected: PropTypes.func,
    onGroupDeselected: PropTypes.func,
    onFormSelected: PropTypes.func,
    onFormDeselected: PropTypes.func,
    onReviewedFormsSend: PropTypes.func,
    studyId: PropTypes.string.isRequired,
    subjectId: PropTypes.string,
    eventId: PropTypes.string,
    onShowHistory: PropTypes.func.isRequired,
    onHideHistory: PropTypes.func.isRequired,
    onSelectedHistoryItemChanged: PropTypes.func.isRequired,
    selectedEventId: PropTypes.string
};