import React from "react";
import * as PropTypes from "prop-types";
import moment from "moment";
import RequestError from "../../../../common/components/error/backend-request-error";
import Form from "../study-forms/study-form";
import ProvenanceSidebar from "./provenance/provenance-sidebar";

function PopulationMessage({selectedEvent}) {
    const populatedBy = !!selectedEvent && !!selectedEvent.populator ? ` by ${selectedEvent.populator}` : '';
    return !!selectedEvent && <div className="alert-box info margin-trl padding">
        <i className="fa fa-info-circle"/> This event was populated
        on {moment(selectedEvent.populationTime).format("ll [at] HH:mm")}{populatedBy},
        with reference date {moment(selectedEvent.referenceDate).format("ll")}
    </div>;
}

function Error({transformedError, contact}) {
    return !!transformedError && <div className="alert-box alert warning margin-trl">
        <div className="study-form-error-container">
            <p className="margin-bottom-none">Sending the selected data points to the EDC failed. No eCRF updates
                were performed. Please review the error(s) below. Deselect erroneous data points and try again or
                contact support.</p>
            <p className="margin-bottom-none">You can click error messages to view the incorrect data point.</p>
            <RequestError error={transformedError} contact={contact}/>
        </div>
    </div>;
}

function FormsOverview({
                           forms,
                           selectedItems,
                           error,
                           subjectId,
                           eventId,
                           actions
                       }) {
    return Array.isArray(forms) && forms.length > 0
        ? <Forms {...arguments[0]} />
        : <NoForms/>;
}

function NoForms() {
    return <div className="grid-block">
        <div className="blank-state-text">
            <p className="text-center">Insufficient data points to populate for this subject and reference
                date. Consider populating the event with a different reference date or contact support if you think
                this is an error.
            </p>
        </div>
    </div>
}

function Forms({
                   forms,
                   selectedItems,
                   error,
                   subjectId,
                   eventId,
                   actions
               }) {
    return <div id="study-form-overview-overview" className="margin-top study-form-container">
        {forms.map(form => {
            const items = form.itemGroups.flatMap(group => group.items).filter(item => !!item.exportable);
            const formSelected = items.length !== 0 && items.filter(item => selectedItems.indexOf(item.itemKey) === -1).length === 0;
            return <Form key={form.formKey} {...form} inReview={true}
                         selected={formSelected} selectedItems={selectedItems}
                         faultyItems={!!error ? issuesFrom(error) : []}
                         subjectId={subjectId}
                         eventId={eventId}
                         {...actions}/>
        })}
    </div>;
}

function issuesFrom({response}) {
    const {data} = response;
    if (!!data && !!data.issues) {
        return data.issues;
    }
    return [];
}

export function PopulatedEventHistoryItemDetails(props) {
    const {studyId, selectedEvent, transformedError, contact, forms} = props;
    const hasForms = Array.isArray(forms) && forms.length > 0;
    return <div className="grid-block vertical">
        <PopulationMessage selectedEvent={selectedEvent}/>
        <Error transformedError={transformedError} contact={contact}/>
        <FormsOverview {...props} />
        {hasForms && <ProvenanceSidebar studyId={studyId}/>}
    </div>;
}

PopulatedEventHistoryItemDetails.propTypes = {
    selectedEvent: PropTypes.object,
    forms: PropTypes.arrayOf(PropTypes.object).isRequired,
    selectedItems: PropTypes.arrayOf(PropTypes.string),
    error: PropTypes.object,
    studyId: PropTypes.string.isRequired,
    subjectId: PropTypes.string,
    eventId: PropTypes.string,
    transformedError: PropTypes.object,
    contact: PropTypes.string,

    actions: PropTypes.shape({
        onItemSelected: PropTypes.func,
        onItemDeselected: PropTypes.func,
        onGroupSelected: PropTypes.func,
        onGroupDeselected: PropTypes.func,
        onFormSelected: PropTypes.func,
        onFormDeselected: PropTypes.func
    })
};