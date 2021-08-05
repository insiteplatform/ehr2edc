import React from 'react';
import {
    Item,
    mapDispatchToProps,
    mapStateToProps
} from "../../../../../src/apps/ehr2edc/components/study-forms/study-form-item"
import {closeItemDetails, openItemDetails} from "../../../../../src/apps/ehr2edc/actions/item-provenance.actions";

const item = {
    itemKey: "item-key-1",
    id: "id-1",
    subjectId: "subject-1",
    eventId: "event-1",
    name: "Item name",
    value: "Item value",
};
const itemNotSubmittedToEdc = Object.assign({}, item, {submittedToEdc: false});
const itemInReview = Object.assign({}, item, {inReview: true});
const itemReviewed = Object.assign({}, item, {inReview: false});

describe("An Item in review gets rendered well", () => {
    test("A regular Item in review gets rendered well", () => {
        const app = shallow(<Item {...itemInReview}/>);

        expect(app).toMatchSnapshot();
    });

    test("A read-only Item in review gets rendered well", () => {
        const app = shallow(<Item {...itemInReview}
                                  readOnly={true}/>);

        expect(app).toMatchSnapshot();
    });

    test("An Item in review with a unit gets rendered well", () => {
        const app = shallow(<Item {...itemInReview}
                                  unit="kg"/>);

        expect(app).toMatchSnapshot();
    });

    test("An Item in review with a labeled value gets rendered well", () => {
        const app = shallow(<Item {...itemInReview}
                                  valueLabel="Item value label"/>);

        expect(app).toMatchSnapshot();
    });

    test("A selected Item in review gets rendered well", () => {
        const app = shallow(<Item {...itemInReview}
                                  selected={true}/>);

        expect(app).toMatchSnapshot();
    });

    test("A Item with open details is rendered as active", () => {
        const app = shallow(<Item {...itemInReview}
                                  detailsOpened={true}/>);

        expect(app.hasClass("is-active")).toBe(true);
    });

    test("An reviewed Item that was not submitted to edc", () => {
        const app = shallow(<Item {...itemNotSubmittedToEdc}
                                  valueLabel="Item value label"/>);

        expect(app).toMatchSnapshot();
    });
});

describe("A reviewed Item gets rendered well", () => {
    test("A regular reviewed Item gets rendered well", () => {
        const app = shallow(<Item {...itemReviewed}/>);

        expect(app).toMatchSnapshot();
    });

    test("A read-only reviewed Item gets rendered well", () => {
        const app = shallow(<Item {...itemReviewed}
                                  readOnly={true}/>);

        expect(app).toMatchSnapshot();
    });

    test("An reviewed Item with a unit gets rendered well", () => {
        const app = shallow(<Item {...itemReviewed}
                                  unit="kg"/>);

        expect(app).toMatchSnapshot();
    });

    test("A reviewed Item with open details is rendered as active", () => {
        const app = shallow(<Item {...itemReviewed}
                                  detailsOpened={true}/>);

        expect(app.hasClass("is-active")).toBe(true);
    });
});

describe("Actions are dispatched correctly", () => {
    test("The openItemDetails action is dispatched when clicking the button", () => {
        const preventDefault = jest.fn();
        const event = {preventDefault: preventDefault};
        const openItemDetails = jest.fn();
        const actions = {openItemDetails: openItemDetails};
        const app = shallow(<Item {...itemInReview}
                                  actions={actions}/>);

        app.find('.open-item-details').simulate('click', event);

        const expectedPayload = {
            "eventId": itemInReview.eventId,
            "itemId": itemInReview.id,
            "subjectId": itemInReview.subjectId,
            "inReview": itemInReview.inReview,
            "name": itemInReview.name,
            "value": itemInReview.value
        };
        expect(preventDefault).toHaveBeenCalled();
        assertAction(openItemDetails, expectedPayload);
    });

    test("The closeItemDetails action is dispatched when clicking the button and the details for this item is already open", () => {
        const preventDefault = jest.fn();
        const event = {preventDefault: preventDefault};
        const closeItemDetails = jest.fn();
        const actions = {closeItemDetails: closeItemDetails};
        const app = shallow(<Item {...itemInReview}
                                  actions={actions}
                                  detailsOpened={true}/>);

        app.find('.open-item-details').simulate('click', event);

        expect(preventDefault).toHaveBeenCalled();
        assertAction(closeItemDetails);
    });

    test("The onItemSelected action is dispatched correctly", () => {
        const action = jest.fn();
        const app = shallow(<Item {...itemInReview}
                                  onItemSelected={action}/>);

        app.find(`#select-form-item-${item.itemKey}`).simulate("change", {target: {checked: true}});

        assertAction(action, item.itemKey);
    });

    test("The onItemDeselected action is dispatched correctly", () => {
        const action = jest.fn();
        const app = shallow(<Item {...itemInReview}
                                  selected={true}
                                  onItemDeselected={action}/>);

        app.find(`#select-form-item-${item.itemKey}`).simulate("change", {target: {checked: false}});

        assertAction(action, item.itemKey);
    });

    function assertAction(action, expectedPayload) {
        expect(action).toHaveBeenCalledTimes(1);
        if (expectedPayload) {
            expect(action).toHaveBeenCalledWith(expectedPayload);
        }
    }
});

describe("mapDispatchToProps", () => {
    test("openItemDetails", () => {
        const dispatch = jest.fn();

        mapDispatchToProps(dispatch).actions.openItemDetails();

        expect(dispatch).toBeCalledWith(openItemDetails());
    });

    test("closeItemDetails", () => {
        const dispatch = jest.fn();

        mapDispatchToProps(dispatch).actions.closeItemDetails();

        expect(dispatch).toBeCalledWith(closeItemDetails());
    });
});

describe("mapStateToProps", () => {
    test("Empty state", () => {
        const ownProps = item;
        const state = {};

        expect(mapStateToProps(state, ownProps)).toEqual({detailsOpened: undefined});
    });
    test("No itemprovenance", () => {
        const ownProps = item;
        const state = {itemProvenance: undefined};

        expect(mapStateToProps(state, ownProps)).toEqual({detailsOpened: undefined});
    });
    test("Empty itemprovenance", () => {
        const ownProps = item;
        const state = {itemProvenance: {}};

        expect(mapStateToProps(state, ownProps)).toEqual({detailsOpened: undefined});
    });
    test("No selectedItem", () => {
        const ownProps = item;
        const state = {
            itemProvenance: {
                itemId: undefined
            }
        };

        expect(mapStateToProps(state, ownProps)).toEqual({detailsOpened: undefined});
    });
    test("Different selectedItem", () => {
        const ownProps = item;
        const state = {
            itemProvenance: {
                itemId: `other-${item.id}`
            }
        };

        expect(mapStateToProps(state, ownProps)).toEqual({detailsOpened: false});
    });
    test("Is selectedItem", () => {
        const ownProps = item;
        const state = {
            itemProvenance: {
                itemId: item.id
            }
        };

        expect(mapStateToProps(state, ownProps)).toEqual({detailsOpened: true});
    });
});