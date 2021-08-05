import itemProvenance from "../../../../src/apps/ehr2edc/reducers/item-provenance.reducer";
import {
    CLOSE_ITEM_DETAILS,
    FETCH_ITEM_PROVENANCE_FULFILLED,
    FETCH_ITEM_PROVENANCE_PENDING,
    FETCH_ITEM_PROVENANCE_REJECTED,
    FETCH_SUBMITTED_ITEM_PROVENANCE_FULFILLED,
    FETCH_SUBMITTED_ITEM_PROVENANCE_PENDING,
    FETCH_SUBMITTED_ITEM_PROVENANCE_REJECTED,
    OPEN_ITEM_DETAILS
} from "../../../../src/apps/ehr2edc/actions/item-provenance.actions";

describe("Item provenance reducer", () => {
    const initialState = {
        prop: "value"
    };

    it("Should fail if no action is provided", () => {
        expect(() => itemProvenance(initialState, undefined)).toThrow(TypeError);
    });

    it("Should not apply unknown actions", () => {
        expect(itemProvenance(initialState, {})).toEqual(initialState);
        expect(itemProvenance(initialState, {type: "UNKNOWN"})).toEqual(initialState);
    });

    it("The FETCH_ITEM_PROVENANCE_PENDING action resets the provenance state", () => {
        const action = {
            type: FETCH_ITEM_PROVENANCE_PENDING
        };
        const reduced = itemProvenance(initialState, action);

        const expectedStateChange = {provenance: {error: undefined, data: undefined}};
        const expectedState = Object.assign({}, initialState, expectedStateChange);
        expect(reduced).toEqual(expectedState);
    });

    it("The FETCH_REVIEWED_ITEM_PROVENANCE_PENDING action resets the provenance state", () => {
        const action = {
            type: FETCH_SUBMITTED_ITEM_PROVENANCE_PENDING
        };
        const reduced = itemProvenance(initialState, action);

        const expectedStateChange = {provenance: {error: undefined, data: undefined}};
        const expectedState = Object.assign({}, initialState, expectedStateChange);
        expect(reduced).toEqual(expectedState);
    });

    it("The FETCH_ITEM_PROVENANCE_FULFILLED action sets the provenance data and clears the error", () => {
        const action = {
            type: FETCH_ITEM_PROVENANCE_FULFILLED,
            payload: {
                data: "details"
            }
        };
        const reduced = itemProvenance(initialState, action);

        const expectedStateChange = {provenance: {error: undefined, data: "details"}};
        const expectedState = Object.assign({}, initialState, expectedStateChange);
        expect(reduced).toEqual(expectedState);
    });

    it("The FETCH_REVIEWED_ITEM_PROVENANCE_FULFILLED action sets the provenance data and clears the error", () => {
        const action = {
            type: FETCH_SUBMITTED_ITEM_PROVENANCE_FULFILLED,
            payload: {
                data: "details"
            }
        };
        const reduced = itemProvenance(initialState, action);

        const expectedStateChange = {provenance: {error: undefined, data: "details"}};
        const expectedState = Object.assign({}, initialState, expectedStateChange);
        expect(reduced).toEqual(expectedState);
    });

    it("The FETCH_ITEM_PROVENANCE_REJECTED action sets the  provenance error and clears the data", () => {
        const action = {
            type: FETCH_ITEM_PROVENANCE_REJECTED,
            payload: {
                statusText: "error"
            }
        };
        const reduced = itemProvenance(initialState, action);

        const expectedStateChange = {provenance: {error: action.payload, data: undefined}};
        const expectedState = Object.assign({}, initialState, expectedStateChange);
        expect(reduced).toEqual(expectedState);
    });

    it("The FETCH_REVIEWED_ITEM_PROVENANCE_REJECTED action sets the  provenance error and clears the data", () => {
        const action = {
            type: FETCH_SUBMITTED_ITEM_PROVENANCE_REJECTED,
            payload: {
                statusText: "error"
            }
        };
        const reduced = itemProvenance(initialState, action);

        const expectedStateChange = {provenance: {error: action.payload, data: undefined}};
        const expectedState = Object.assign({}, initialState, expectedStateChange);
        expect(reduced).toEqual(expectedState);
    });

    it("The OPEN_ITEM_DETAILS action sets the item details", () => {
        const action = {
            type: OPEN_ITEM_DETAILS,
            payload: {
                subjectId: "subject-1",
                eventId: "event-1",
                itemId: "item-1",
                inReview: true,
                name: "name-1",
                value: "value-1"
            }
        };
        const reduced = itemProvenance(initialState, action);

        const expectedStateChange = {
            subjectId: "subject-1",
            eventId: "event-1",
            itemId: "item-1",
            inReview: true,
            name: "name-1",
            value: "value-1",
            provenance: undefined
        };
        const expectedState = Object.assign({}, initialState, expectedStateChange);
        expect(reduced).toEqual(expectedState);
    });

    it("The CLOSE_ITEM_DETAILS action resets the item details", () => {
        const action = {
            type: CLOSE_ITEM_DETAILS,
        };
        const reduced = itemProvenance(initialState, action);

        expect(reduced).toEqual({});
    });
});