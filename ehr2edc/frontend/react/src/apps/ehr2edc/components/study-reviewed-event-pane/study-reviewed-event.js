import React, {Fragment} from "react"
import * as PropTypes from "prop-types";
import ReviewedEventOverview from "./reviewed-event-overview";
import {getAllSubmittedEvents, getSubject, getSubmittedEvent} from "../../../../api/api-ext";
import {
    SearchResultsError,
    SearchResultsLoading
} from "../../../../common/components/search-result/search-result.helper";

export default class StudyReviewedEvent extends React.Component {
    constructor(props) {
        super(props);
        this.keyCount = 0;
        this.state = StudyReviewedEvent.emptyState();
        this.state.historyVisible = props.historyVisible;
        this.handleHistoryItemSelected = this.handleHistoryItemSelected.bind(this);
        this.showHistory = this.showHistory.bind(this);
        this.hideHistory = this.hideHistory.bind(this);
    }

    static emptyState() {
        return {
            formsLoading: true,
            formHistoryLoading: true,
            formsInError: false,
            subjectInError: false,
            formHistoryInError: false,
            submittedFormHistory: [],
            reviewedForms: [],
            reviewTime: "",
            reviewer: "",
            populationTime: "",
            referenceDate: ""
        };
    }

    componentDidMount() {
        this.clearComponent();
        this.fetchEdcSubjectReference();
        this.fetchAllSubmittedFormHistory();
    }

    clearComponent() {
        this.setState(StudyReviewedEvent.emptyState());
    }

    fetchAllSubmittedFormHistory() {
        const {studyId, subjectId, eventId} = this.props;
        getAllSubmittedEvents(studyId, subjectId, eventId)
            .then(response => this.handleSubmittedHistorySuccess(response),
                () => this.handleSubmittedHistoryError());
    }

    handleSubmittedHistorySuccess({data}) {
        const historyItems = data.historyItems;
        this.setState({
            formHistoryLoading: false,
            submittedFormHistory: historyItems,
            formHistoryInError: false
        });
        if (historyItems.length === 0) {
            this.setState({
                formsLoading: false,
            });
        } else {
            this.fetchLastReviewedForms(historyItems[0].reviewedEventId);
        }
    }

    handleSubmittedHistoryError() {
        this.setState({
            formHistoryLoading: false,
            submittedFormHistory: [],
            formHistoryInError: true,
            formsLoading: false,
        });
    }

    fetchLastReviewedForms(reviewedEventId) {
        const {studyId, subjectId} = this.props;
        this.loadingStateForReviewedEventDetails(reviewedEventId);
        getSubmittedEvent(studyId, subjectId, reviewedEventId)
            .then(response => {
                this.handleLastReviewedFormsCompleted(response);
            }, () => {
                this.handleLastReviewedFormsError();
            });
    }

    loadingStateForReviewedEventDetails(selectedReviewedEventId) {
        this.setState({
            selectedReviewedEventId: selectedReviewedEventId,
            formsLoading: true,
            formsInError: false,
            reviewedForms: [],
            reviewTime: "",
            reviewer: "",
            populationTime: "",
            referenceDate: "",
            populator: ""
        });
    }

    handleLastReviewedFormsCompleted(response) {
        this.setState({
            formsLoading: false,
            reviewedForms: this.addKeysToForms(response.data.reviewedForms),
            reviewTime: response.data.reviewTime,
            reviewer: response.data.reviewer,
            populationTime: response.data.populationTime,
            referenceDate: response.data.referenceDate,
            populator: response.data.populator,
            formsInError: false
        });
    }

    handleLastReviewedFormsError() {
        this.setState({
            formsLoading: false,
            reviewedForms: [],
            reviewTime: "",
            reviewer: "",
            populationTime: "",
            referenceDate: "",
            populator: "",
            formsInError: true
        });
    }

    addKeysToForms(forms) {
        return forms.map(form => {
            const key = this.generateKey("form-");
            return {
                ...form,
                formKey: key,
                itemGroups: this.addKeysToItemGroups(form.itemGroups)
            };
        });
    }

    addKeysToItemGroups(itemGroups) {
        return itemGroups.map(group => {
            const key = this.generateKey("group-");
            return {
                ...group,
                groupKey: key,
                items: this.addKeysToItems(group.items)
            };
        });
    }

    addKeysToItems(items) {
        return items.map(item => {
            const key = this.generateKey("item-");
            return {
                ...item,
                itemKey: key
            };
        });
    }

    generateKey(prefix) {
        return prefix + (++this.keyCount);
    }

    render() {
        const {reviewedForms, reviewTime, reviewer, populationTime, referenceDate, populator, submittedFormHistory, formsLoading, selectedReviewedEventId, historyVisible} = this.state;
        const {studyId, subjectId, eventDefinitionId} = this.props;
        return <Fragment>
            {this.isLoading() && <SearchResultsLoading/>}
            {!this.isLoading() && this.isInError() &&
            <SearchResultsError message="Could not load the most recent reviewed event"/>}
            {!this.isLoading() && !this.isInError() &&
            <ReviewedEventOverview reviewedForms={reviewedForms} reviewTime={reviewTime} reviewer={reviewer}
                                   populationTime={populationTime} referenceDate={referenceDate}
                                   studyId={studyId} subjectId={subjectId} populator={populator}
                                   resendFormsUrl={`/${studyId}/subjects/${subjectId}/events/${eventDefinitionId}`}
                                   historyItems={submittedFormHistory} detailsLoading={formsLoading}
                                   selectedReviewedEventId={selectedReviewedEventId} historyVisible={historyVisible}
                                   onHistoryItemSelected={this.handleHistoryItemSelected}
                                   onShowHistory={this.showHistory} onHideHistory={this.hideHistory}/>}
        </Fragment>;
    }

    isLoading() {
        const {formsLoading, formHistoryLoading} = this.state;
        return formsLoading && formHistoryLoading;
    }

    isInError() {
        const {formsInError, subjectInError, formHistoryInError} = this.state;
        return formsInError || subjectInError || formHistoryInError;
    }

    handleHistoryItemSelected(reviewedEventId) {
        this.fetchLastReviewedForms(reviewedEventId);
    }

    showHistory() {
        this.setState({
            historyVisible: true
        });
    }

    hideHistory() {
        this.setState({
            historyVisible: false
        });
    }

    fetchEdcSubjectReference() {
        const {studyId, subjectId} = this.props;
        getSubject(studyId, subjectId).then(
            (response) => this.props.onEdcSubjectReferenceChanged(response.data.edcSubjectReference),
            () => this.setState({
                subjectInError: true
            }))

    }
}
StudyReviewedEvent.propTypes = {
    studyId: PropTypes.string.isRequired,
    subjectId: PropTypes.string.isRequired,
    eventDefinitionId: PropTypes.string.isRequired,
    eventId: PropTypes.string.isRequired,
    historyVisible: PropTypes.bool
};

StudyReviewedEvent.defaultProps = {
    historyVisible: true
};