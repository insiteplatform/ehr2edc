import {ActionType} from 'redux-promise-middleware';

import {getItemProvenance, getSubmittedItemProvenance} from "../../../api/api-ext";

export const FETCH_ITEM_PROVENANCE = 'FETCH_ITEM_PROVENANCE';
export const FETCH_ITEM_PROVENANCE_PENDING = `${FETCH_ITEM_PROVENANCE}_${ActionType.Pending}`;
export const FETCH_ITEM_PROVENANCE_FULFILLED = `${FETCH_ITEM_PROVENANCE}_${ActionType.Fulfilled}`;
export const FETCH_ITEM_PROVENANCE_REJECTED = `${FETCH_ITEM_PROVENANCE}_${ActionType.Rejected}`;
export const FETCH_SUBMITTED_ITEM_PROVENANCE = 'FETCH_SUBMITTED_ITEM_PROVENANCE';
export const FETCH_SUBMITTED_ITEM_PROVENANCE_PENDING = `${FETCH_SUBMITTED_ITEM_PROVENANCE}_${ActionType.Pending}`;
export const FETCH_SUBMITTED_ITEM_PROVENANCE_FULFILLED = `${FETCH_SUBMITTED_ITEM_PROVENANCE}_${ActionType.Fulfilled}`;
export const FETCH_SUBMITTED_ITEM_PROVENANCE_REJECTED = `${FETCH_SUBMITTED_ITEM_PROVENANCE}_${ActionType.Rejected}`;
export const OPEN_ITEM_DETAILS = 'OPEN_ITEM_DETAILS';
export const CLOSE_ITEM_DETAILS = 'CLOSE_ITEM_DETAILS';

export function fetchItemProvenance(payload = {}) {
    return {
        type: FETCH_ITEM_PROVENANCE,
        payload: getItemProvenance(payload.studyId, payload.subjectId, payload.eventId, payload.itemId)
    }
}

export function fetchSubmittedItemProvenance(payload = {}) {
    return {
        type: FETCH_SUBMITTED_ITEM_PROVENANCE,
        payload: getSubmittedItemProvenance(payload.studyId, payload.subjectId, payload.eventId, payload.itemId)
    }
}

export function openItemDetails(payload = {}) {
    return {
        type: OPEN_ITEM_DETAILS,
        payload
    }
}

export function closeItemDetails() {
    return {
        type: CLOSE_ITEM_DETAILS
    }
}