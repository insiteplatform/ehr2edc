import React from "react"
import StudyPopulatedEventOverview from "./study-populated-event-overview";
import * as PropTypes from "prop-types";
import {getEvent, getEventPopulationHistory, getSubject, submitReviewedForms} from "../../../../api/api-ext";
import {
    SearchResultsError,
    SearchResultsLoading
} from "../../../../common/components/search-result/search-result.helper";

export default class StudyPopulatedEvent extends React.Component {

    constructor(props) {
        super(props);
        this.keyCount = 0;
        this.state = {
            populatedEventHistory: null,
            forms: undefined,
            inError: false,
            selectedFormItems: [],
            historyItemLoading: true,
            historyItemInError: false,
            historyVisible: props.historyVisible
        };
        this.deselectAllFormItems = this.deselectAllFormItems.bind(this);
        this.selectItem = this.selectItem.bind(this);
        this.deselectItem = this.deselectItem.bind(this);
        this.selectGroup = this.selectGroup.bind(this);
        this.deselectGroup = this.deselectGroup.bind(this);
        this.selectForm = this.selectForm.bind(this);
        this.deselectForm = this.deselectForm.bind(this);
        this.sendReviewedForms = this.sendReviewedForms.bind(this);
        this.showHistory = this.showHistory.bind(this);
        this.hideHistory = this.hideHistory.bind(this);
        this.selectPopulatedEvent = this.selectPopulatedEvent.bind(this);
    }

    componentDidMount() {
        this.fetchEdcSubjectReference();
        this.fetchPopulatedEventHistory();
    }

    async fetchPopulatedEventHistory() {
        try {
            const {data} = await getEventPopulationHistory(this.props.studyId, this.props.subjectId, this.props.eventDefinitionId);
            const eventId = data.historyItems[0].eventId;
            this.setState({
                populatedEventHistory: data.historyItems,
                eventId: eventId,
                historyItemLoading: true,
                historyItemInError: false
            });
            this.fetchEvent()
        } catch (error) {
            this.setErrorState();
        }

    }

    async fetchEvent(eventId = this.state.eventId) {
        try {
            const response = await getEvent(this.props.studyId, this.props.subjectId, eventId);
            this.setState({
                forms: this.addKeysToForms(response.data.event.forms),
                lastSubmissionTime: response.data.event.lastSubmissionTime,
                historyItemLoading: false,
                historyItemInError: false
            });
            this.notifyEventChanged(response.data.event)
        } catch (error) {
            this.setState({
                forms: undefined,
                inError: true,
                historyItemLoading: false,
                historyItemInError: true
            })
        }
    }

    setErrorState() {
        this.setState({
            forms: undefined,
            inError: true
        })
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
        const {inError, populatedEventHistory} = this.state;
        if (inError) {
            return <SearchResultsError message="Cannot retrieve populated event data"/>
        }
        if (populatedEventHistory === null) {
            return SearchResultsLoading();
        }
        return this.renderOverview(populatedEventHistory);
    }

    renderOverview(populatedEventHistory) {
        const {forms, selectedFormItems, lastSubmissionTime, submitError, inProgress, eventId, historyItemLoading, historyItemInError, historyVisible} = this.state;
        const {canSendToEDC, studyId, subjectId, eventDefinitionId, contact} = this.props;
        return <StudyPopulatedEventOverview forms={forms}
                                            selectedItems={selectedFormItems}
                                            lastSubmissionTime={lastSubmissionTime}
                                            inProgress={inProgress}
                                            canSendToEDC={canSendToEDC}
                                            contact={contact}
                                            error={submitError}
                                            actions={this.formActions()}
                                            onReviewedFormsSend={this.sendReviewedForms}
                                            reviewedFormsUrl={`../${eventDefinitionId}/${eventId}/reviewed`}
                                            studyId={studyId}
                                            subjectId={subjectId}
                                            eventId={eventId}
                                            historyItems={populatedEventHistory}
                                            historyVisible={historyVisible}
                                            historyItemLoading={historyItemLoading}
                                            historyItemInError={historyItemInError}
                                            selectedEventId={eventId}
                                            onSelectedHistoryItemChanged={this.selectPopulatedEvent}
                                            onShowHistory={this.showHistory}
                                            onHideHistory={this.hideHistory}
        />
    }

    formActions() {
        return {
            onItemSelected: this.selectItem,
            onItemDeselected: this.deselectItem,
            onGroupSelected: this.selectGroup,
            onGroupDeselected: this.deselectGroup,
            onFormSelected: this.selectForm,
            onFormDeselected: this.deselectForm
        };
    }

    selectPopulatedEvent(eventId) {
        this.deselectAllFormItems();
        this.setState({
            eventId: eventId,
            historyItemLoading: true,
            historyItemInError: false
        });
        this.fetchEvent(eventId);
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

    deselectAllFormItems() {
        this.setState({
            selectedFormItems: []
        })
    }

    selectItem(itemKey) {
        let {selectedFormItems} = this.state;
        const item = this.findItem(itemKey);
        if (!!item.exportable) {
            this.setState({
                selectedFormItems: [...selectedFormItems, itemKey]
            });
        }
    }

    deselectItem(itemKey) {
        const {selectedFormItems} = this.state;
        this.setState({
            selectedFormItems: selectedFormItems.filter(id => id !== itemKey)
        });
    }

    findItem(itemKey) {
        const allItems = this.state.forms.flatMap(form => form.itemGroups).flatMap(group => group.items);
        return allItems.filter(item => item.itemKey === itemKey)[0];
    }

    selectGroup(groupKey) {
        const {selectedFormItems} = this.state;
        const group = this.findItemGroup(groupKey);
        const itemKeys = group.items.filter(item => !!item.exportable).map(item => item.itemKey);
        this.setState({
            selectedFormItems: [...selectedFormItems, ...itemKeys]
        });
    }

    deselectGroup(groupKey) {
        const {selectedFormItems} = this.state;
        const group = this.findItemGroup(groupKey);
        const itemKeys = group.items.map(item => item.itemKey);
        this.setState({
            selectedFormItems: selectedFormItems.filter(id => itemKeys.indexOf(id) === -1)
        });
    }

    findItemGroup(groupKey) {
        const allGroups = this.state.forms.flatMap(form => form.itemGroups);
        return allGroups.filter(group => group.groupKey === groupKey)[0];
    }

    selectForm(formKey) {
        const {selectedFormItems} = this.state;
        const form = this.findForm(formKey);
        const itemKeys = form.itemGroups.flatMap(group => group.items).filter(item => !!item.exportable).map(item => item.itemKey);
        this.setState({
            selectedFormItems: [...selectedFormItems, ...itemKeys]
        })
    }

    deselectForm(formKey) {
        const {selectedFormItems} = this.state;
        const form = this.findForm(formKey);
        const itemKeys = form.itemGroups.flatMap(group => group.items).map(item => item.itemKey);
        this.setState({
            selectedFormItems: selectedFormItems.filter(id => itemKeys.indexOf(id) === -1)
        })
    }

    findForm(formKey) {
        return this.state.forms.filter(form => form.formKey === formKey)[0];
    }

    async sendReviewedForms() {
        const {studyId, subjectId, history, eventDefinitionId} = this.props;
        const {eventId} = this.state;
        this.setFormSubmissionInProgress(true);
        try {
            await submitReviewedForms(studyId, subjectId, this.createReviewedEventRequestBody());
            history.push(`../${eventDefinitionId}/${eventId}/reviewed`)
        } catch (error) {
            this.setState({
                submitError: error
            });
        }
        this.setFormSubmissionInProgress(false);
    }

    getReviewedForms() {
        const {forms, selectedFormItems} = this.state;
        return this.mapFormOnItemSelection(forms, selectedFormItems);
    }

    mapFormOnItemSelection(forms, selectedFormItems) {
        return forms.map(form => {
            return {
                id: form.id,
                itemGroups: this.mapGroupsOnItemSelection(form.itemGroups, selectedFormItems)
            }
        }).filter(form => form.itemGroups.length !== 0);
    }

    mapGroupsOnItemSelection(groups, selectedFormItems) {
        return groups.map(group => {
            const filteredGroupItems = this.mapItemsOnSelection(group.items, selectedFormItems);
            return {
                id: group.id,
                items: filteredGroupItems
            }
        }).filter(group => group.items.length !== 0);
    }

    mapItemsOnSelection(items, selectedFormItems) {
        return items.filter(item => selectedFormItems.indexOf(item.itemKey) !== -1).map(item => {
            return {id: item.id}
        });
    }

    setFormSubmissionInProgress(inProgress) {
        this.setState({
            inProgress: inProgress
        })
    }

    fetchEdcSubjectReference() {
        const {studyId, subjectId} = this.props;
        getSubject(studyId, subjectId).then((response) =>
                this.props.onEdcSubjectReferenceChanged(response.data.edcSubjectReference),
            (error) => this.handleError(error))

    }

    handleError(error) {
        this.setState({
            error: error
        });
    }

    notifyEventChanged(event) {
        this.props.onEventChanged({name: event.name});
    }

    createReviewedEventRequestBody() {
        const {eventId} = this.state;
        return {
            "populatedEventId": eventId,
            "reviewedForms": this.getReviewedForms()
        };
    }
}

StudyPopulatedEvent.propTypes = {
    subjectId: PropTypes.string,
    studyId: PropTypes.string.isRequired,
    onEdcSubjectReferenceChanged: PropTypes.func,
    onEventChanged: PropTypes.func,
    eventDefinitionId: PropTypes.string.isRequired,
    historyVisible: PropTypes.bool
};

StudyPopulatedEvent.defaultProps = {
    historyVisible: false
};