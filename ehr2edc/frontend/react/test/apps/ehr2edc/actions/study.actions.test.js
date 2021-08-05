import configureMockStore from 'redux-mock-store'
import promise from 'redux-promise-middleware'
import {
    FETCH_STUDIES_FULFILLED,
    FETCH_STUDIES_PENDING,
    FETCH_STUDIES_REJECTED,
    fetchStudies
} from "../../../../src/apps/ehr2edc/actions/study.actions";
import * as StudyAPI from "../../../../src/api/study-api";

const middlewares = [promise];
const mockStore = configureMockStore(middlewares);

describe("Study actions", () => {
    let store;

    beforeEach(() => {
        store = mockStore({});
    });

    it("fetchStudies creates a pending and fulfilled action when the REST-call succeeds", async () => {
        // Given: The REST-call will succeed
        const response = [{study: "a-study "}];
        const restCall = jest.spyOn(StudyAPI, "getAll");
        restCall.mockReturnValueOnce(() => Promise.resolve(response));

        // When: Dispatching a 'fetchStudies'-actions
        const dispatched = store.dispatch(fetchStudies());
        await dispatched;

        // Then: A PENDING and FULFILLED action were dispatched
        const expectedActions = [
            {type: FETCH_STUDIES_PENDING},
            {
                type: FETCH_STUDIES_FULFILLED,
                payload: response
            }
        ];
        expect(store.getActions()).toEqual(expectedActions);
        expect(restCall).toHaveBeenCalled();
    });

    it("fetchStudies creates a pending and rejected action when the REST-call fails", async () => {
        // Given: The REST-call will fail
        const restCall = jest.spyOn(StudyAPI, "getAll");
        const rejection = "rejected for test";
        restCall.mockReturnValueOnce(() => Promise.reject(rejection));

        //When: dispatching a 'fetchStudies'-action
        const dispatched = store.dispatch(fetchStudies());

        // Then: a PENDING and REJECTED action were dispatched
        try {
            await dispatched;
        } catch (e) {
            const expectedActions = [
                {
                    type: FETCH_STUDIES_PENDING
                }, {
                    type: FETCH_STUDIES_REJECTED,
                    payload: rejection,
                    error: true,
                }
            ];
            expect(store.getActions()).toEqual(expectedActions);
            expect(restCall).toHaveBeenCalled();
        }
    });
})
;
