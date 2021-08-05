import studyReducer from "../../../../src/apps/ehr2edc/reducers/study.reducer";
import {
    FETCH_STUDIES_FULFILLED,
    FETCH_STUDIES_PENDING,
    FETCH_STUDIES_REJECTED
} from "../../../../src/apps/ehr2edc/actions/study.actions";

describe("Study reducer", () => {
    const initialState = {
        prop: "value"
    };

    it("Should fail if no action is provided", () => {
        expect(() => studyReducer(initialState, undefined)).toThrow(TypeError);
    });

    it("Should not apply unknown actions", () => {
        expect(studyReducer(initialState, {})).toEqual(initialState);
        expect(studyReducer(initialState, {type: "UNKNOWN"})).toEqual(initialState);
    });

    it("The FETCH_STUDIES_PENDING action resets the 'study.all' state", () => {
        const action = {
            type: FETCH_STUDIES_PENDING
        };
        const reduced = studyReducer(initialState, action);

        const expectedStateChange = {all: {error: undefined, data: undefined}};
        const expectedState = Object.assign({}, initialState, expectedStateChange);
        expect(reduced).toEqual(expectedState);
    });

    it("The FETCH_STUDIES_FULFILLED action sets the 'study.all.data' and clears the error", () => {
        const action = {
            type: FETCH_STUDIES_FULFILLED,
            payload: {
                data: "study-data"
            }
        };
        const reduced = studyReducer(initialState, action);

        const expectedStateChange = {all: {error: undefined, data: "study-data"}};
        const expectedState = Object.assign({}, initialState, expectedStateChange);
        expect(reduced).toEqual(expectedState);
    });

    it("The FETCH_STUDIES_REJECTED action sets the 'study.all.error' state and clears the data", () => {
        const action = {
            type: FETCH_STUDIES_REJECTED,
            payload: {
                statusText: "error"
            }
        };
        const reduced = studyReducer(initialState, action);

        const expectedStateChange = {all: {error: action.payload, data: undefined}};
        const expectedState = Object.assign({}, initialState, expectedStateChange);
        expect(reduced).toEqual(expectedState);
    });
});
