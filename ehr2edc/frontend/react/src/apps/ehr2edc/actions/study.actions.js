import {ActionType} from 'redux-promise-middleware';
import {getAll} from "../../../api/study-api";

export const FETCH_STUDIES = 'FETCH_STUDIES';
export const FETCH_STUDIES_PENDING = `${FETCH_STUDIES}_${ActionType.Pending}`;
export const FETCH_STUDIES_FULFILLED = `${FETCH_STUDIES}_${ActionType.Fulfilled}`;
export const FETCH_STUDIES_REJECTED = `${FETCH_STUDIES}_${ActionType.Rejected}`;

export function fetchStudies() {
    return {
        type: FETCH_STUDIES,
        payload: getAll()
    };
}