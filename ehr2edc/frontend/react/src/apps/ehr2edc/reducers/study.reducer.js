import {
    FETCH_STUDIES_PENDING,
    FETCH_STUDIES_FULFILLED,
    FETCH_STUDIES_REJECTED,
} from "../actions/study.actions";

const initialState = {};

export default function study(state = initialState, action) {
    switch (action.type) {
        case FETCH_STUDIES_PENDING:
            return {
                ...state,
                all: {
                    error: undefined,
                    data: undefined,
                },
            };
        case FETCH_STUDIES_FULFILLED:
            return {
                ...state,
                all: {
                    error: undefined,
                    data: action.payload.data,
                },
            };
        case FETCH_STUDIES_REJECTED:
            return {
                ...state,
                all: {
                    error: action.payload,
                    data: undefined,
                },
            };
        default:
            return state;
    }
}
