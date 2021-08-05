import {
    CLOSE_ITEM_DETAILS,
    FETCH_ITEM_PROVENANCE_FULFILLED,
    FETCH_ITEM_PROVENANCE_PENDING,
    FETCH_ITEM_PROVENANCE_REJECTED,
    FETCH_SUBMITTED_ITEM_PROVENANCE_FULFILLED,
    FETCH_SUBMITTED_ITEM_PROVENANCE_PENDING,
    FETCH_SUBMITTED_ITEM_PROVENANCE_REJECTED,
    OPEN_ITEM_DETAILS,
} from "../actions/item-provenance.actions";

const initialState = {};

export default function itemProvenance(state = initialState, action) {
    switch (action.type) {
        case FETCH_ITEM_PROVENANCE_PENDING:
        case FETCH_SUBMITTED_ITEM_PROVENANCE_PENDING:
            return {
                ...state,
                provenance: {
                    error: undefined,
                    data: undefined
                }
            };
        case FETCH_ITEM_PROVENANCE_FULFILLED:
        case FETCH_SUBMITTED_ITEM_PROVENANCE_FULFILLED:
            return {
                ...state,
                provenance: {
                    error: undefined,
                    data: action.payload.data
                }
            };
        case FETCH_ITEM_PROVENANCE_REJECTED:
        case FETCH_SUBMITTED_ITEM_PROVENANCE_REJECTED:
            return {
                ...state,
                provenance: {
                    error: action.payload,
                    data: undefined
                }
            };
        case OPEN_ITEM_DETAILS:
            return {
                ...state,
                subjectId: action.payload.subjectId,
                eventId: action.payload.eventId,
                itemId: action.payload.itemId,
                inReview: action.payload.inReview,
                name: action.payload.name,
                value: action.payload.value,
                provenance: undefined
            };
        case CLOSE_ITEM_DETAILS:
            return {};
        default:
            return state
    }
}