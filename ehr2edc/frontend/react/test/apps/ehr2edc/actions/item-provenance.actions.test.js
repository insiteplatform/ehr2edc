import {
    CLOSE_ITEM_DETAILS,
    closeItemDetails,
    FETCH_ITEM_PROVENANCE_FULFILLED,
    FETCH_ITEM_PROVENANCE_PENDING,
    FETCH_ITEM_PROVENANCE_REJECTED,
    FETCH_SUBMITTED_ITEM_PROVENANCE_FULFILLED,
    FETCH_SUBMITTED_ITEM_PROVENANCE_PENDING,
    FETCH_SUBMITTED_ITEM_PROVENANCE_REJECTED,
    fetchItemProvenance,
    fetchSubmittedItemProvenance,
    OPEN_ITEM_DETAILS,
    openItemDetails
} from "../../../../src/apps/ehr2edc/actions/item-provenance.actions";

import configureMockStore from 'redux-mock-store'
import promise from 'redux-promise-middleware'
import mockAxios from 'jest-mock-axios';

const middlewares = [promise];
const mockStore = configureMockStore(middlewares);

describe("ItemProvenance actions", () => {
    afterEach(() => {
        mockAxios.reset();
    });

    it("fetchItemProvenance creates a pending and fulfilled action when the async call succeeds", () => {
        const webResponse = {data: "data"};
        mockAxios.get.mockImplementation(() => Promise.resolve(webResponse));
        const store = mockStore({});

        const dispatch = store.dispatch(fetchItemProvenance({
            studyId: "study-1",
            subjectId: "subject-1",
            eventId: "event-1",
            itemId: "item-1"
        }));

        const expectedActions = [
            {type: FETCH_ITEM_PROVENANCE_PENDING},
            {
                type: FETCH_ITEM_PROVENANCE_FULFILLED,
                payload: webResponse
            }
        ];
        return dispatch.then(() => {
            expect(store.getActions()).toEqual(expectedActions);
            expect(mockAxios.get).toHaveBeenCalledWith('/ehr2edc/studies/study-1/subjects/subject-1/events/populated/event-1/items/item-1/provenance');
        })
    });

    it("fetchItemProvenance creates a pending and error action when the async call fails", () => {
        const webFailure = "Web Failure";
        mockAxios.get.mockImplementation(() => Promise.reject(webFailure));
        const store = mockStore({});

        const dispatch = store.dispatch(fetchItemProvenance({
            studyId: "study-1",
            subjectId: "subject-1",
            eventId: "event-1",
            itemId: "item-1"
        })).catch(error => {
        });

        const expectedActions = [
            {type: FETCH_ITEM_PROVENANCE_PENDING},
            {
                type: FETCH_ITEM_PROVENANCE_REJECTED,
                payload: webFailure,
                error: true
            }
        ];
        return dispatch.then(() => {
            expect(store.getActions()).toEqual(expectedActions);
            expect(mockAxios.get).toHaveBeenCalledWith('/ehr2edc/studies/study-1/subjects/subject-1/events/populated/event-1/items/item-1/provenance');
        })
    });

    it("fetchSubmittedItemProvenance creates a pending and fulfilled action when the async call succeeds", () => {
        const webResponse = {data: "data"};
        mockAxios.get.mockImplementation(() => Promise.resolve(webResponse));
        const store = mockStore({});

        const dispatch = store.dispatch(fetchSubmittedItemProvenance({
            studyId: "study-1",
            subjectId: "subject-1",
            eventId: "event-1",
            itemId: "item-1"
        }));

        const expectedActions = [
            {type: FETCH_SUBMITTED_ITEM_PROVENANCE_PENDING},
            {
                type: FETCH_SUBMITTED_ITEM_PROVENANCE_FULFILLED,
                payload: webResponse
            }
        ];
        return dispatch.then(() => {
            expect(store.getActions()).toEqual(expectedActions);
            expect(mockAxios.get).toHaveBeenCalledWith('/ehr2edc/studies/study-1/subjects/subject-1/events/submitted/event-1/items/item-1/provenance');
        })
    });

    it("fetchSubmittedItemProvenance creates a pending and error action when the async call fails", () => {
        const webFailure = "Web Failure";
        mockAxios.get.mockImplementation(() => Promise.reject(webFailure));
        const store = mockStore({});

        const dispatch = store.dispatch(fetchSubmittedItemProvenance({
            studyId: "study-1",
            subjectId: "subject-1",
            eventId: "event-1",
            itemId: "item-1"
        })).catch(error => {
        });

        const expectedActions = [
            {type: FETCH_SUBMITTED_ITEM_PROVENANCE_PENDING},
            {
                type: FETCH_SUBMITTED_ITEM_PROVENANCE_REJECTED,
                payload: webFailure,
                error: true
            }
        ];
        return dispatch.then(() => {
            expect(store.getActions()).toEqual(expectedActions);
            expect(mockAxios.get).toHaveBeenCalledWith('/ehr2edc/studies/study-1/subjects/subject-1/events/submitted/event-1/items/item-1/provenance');
        })
    });
    
    it("openItemDetails creates an action to open a selectedItem's details", () => {
        const payload = {
            subjectId: "subject-1",
            eventId: "event-1",
            itemId: "item-1",
            name: "name-1",
            value: "value-1"
        };
        const expectedAction = {
            type: OPEN_ITEM_DETAILS, payload: payload
        };

        expect(openItemDetails(payload)).toEqual(expectedAction)
    });

    it("closeItemDetails creates an action to close a selectedItem's details", () => {
        const expectedAction = {type: CLOSE_ITEM_DETAILS};

        expect(closeItemDetails()).toEqual(expectedAction)
    });
});