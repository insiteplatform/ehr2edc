import React from "react"
import {
    mapDispatchToProps,
    ProvenanceSidebar
} from "../../../../../../src/apps/ehr2edc/components/study-populated-event-pane/provenance/provenance-sidebar";
import {
    closeItemDetails,
    FETCH_ITEM_PROVENANCE,
    FETCH_SUBMITTED_ITEM_PROVENANCE
} from "../../../../../../src/apps/ehr2edc/actions/item-provenance.actions";

describe("ProvenanceSidebar rendering", () => {
    function resetDocument() {
        document.body.innerHTML = '';
    }

    function createPortalRoot() {
        const portalRoot = global.document.createElement('div');
        portalRoot.setAttribute('id', 'study-details-content-pane');
        const body = global.document.querySelector('body');
        body.appendChild(portalRoot);
    }

    beforeEach(() => {
        resetDocument();
        createPortalRoot();
    });

    test("Should not render with incomplete props", () => {
        // Expect:The component not to be rendered, when I mount the component with invalid props
        expect(mount(<ProvenanceSidebar/>)).toEqual({});
        expect(mount(<ProvenanceSidebar {...validProps} name={""}/>)).toEqual({});
        expect(mount(<ProvenanceSidebar {...validProps} name={undefined}/>)).toEqual({});
        expect(mount(<ProvenanceSidebar {...validProps} value={""}/>)).toEqual({});
        expect(mount(<ProvenanceSidebar {...validProps} value={undefined}/>)).toEqual({});
        expect(mount(<ProvenanceSidebar {...validProps} provenance={"notAnObject"}/>)).toEqual({});
        expect(mount(<ProvenanceSidebar {...validProps} provenance={undefined}/>)).toEqual({});
    });

    test("Should create a portal to #study-details-content-pane", () => {
        // Given: A root to attach the Sidebar to
        const portalRoot = global.document.querySelector('#study-details-content-pane');

        // When: I mount the component
        const comp = mount(<ProvenanceSidebar {...validProps}/>);

        // Then: The sidebar is added to the DOM
        expect(comp.find("Portal").first().prop("container")).toEqual(portalRoot);
    });

    test("Should render a ItemProvenance-component inside a Sidebar", () => {
        // Given: A ProvenanceSidebar
        const app = mount(<ProvenanceSidebar {...validProps}/>);

        // Then: The component should match the snapshot
        expect(app).toMatchSnapshot();
        // And: A Sidebar should be rendered
        const sidebar = app.find("Sidebar");
        expect(sidebar.exists()).toBe(true);
        // And: The Sidebar should contain an ItemProvenance
        expect(sidebar.exists("ItemProvenance")).toBe(true);
    })
});

describe("Actions", () => {
    it("Should dispatch the fetchItemProvenance action when item is in review and item identifiers are set as props", () => {
        // Given: A fetchItemProvenance action
        const fetchItemProvenance = jest.fn();
        const actions = {
            fetchItemProvenance: fetchItemProvenance
        };

        // When: Rendering the component in review with mandatory identifiers
        const component = mount(<ProvenanceSidebar inReview={true}
                                                   actions={actions}/>);
        component.setProps(identifiers);

        // Then: The fetchItemProvenance action is dispatched
        expect(fetchItemProvenance).toHaveBeenCalledWith({...identifiers});
    });
    it("Should dispatch the fetchSubmittedItemProvenance action when item is submitted and item identifiers are set as props", () => {
        // Given: A fetchSubmittedItemProvenance action
        const fetchSubmittedItemProvenance = jest.fn();
        const actions = {
            fetchSubmittedItemProvenance: fetchSubmittedItemProvenance
        };

        // When: Rendering the component reviewed with mandatory identifiers
        const component = mount(<ProvenanceSidebar inReview={false}
                                                   actions={actions}/>);
        component.setProps(identifiers);

        // Then: The fetchSubmittedItemProvenance action is dispatched
        expect(fetchSubmittedItemProvenance).toHaveBeenCalledWith({...identifiers});
    });
    it("Should dispatch the closeItemDetails action when clicking the icon", () => {
        // Given: A close action
        const closeItemDetails = jest.fn();
        const actions = {
            closeItemDetails: closeItemDetails
        };

        // When: Clicking the close button
        const component = mount(<ProvenanceSidebar {...validProps} actions={actions}/>);
        component.find("a.close-icon").simulate("click");

        // Then: The close-action is dispatched
        expect(closeItemDetails).toHaveBeenCalled();
    });
    it("Should call the close handler and dispatch the closeItemDetails action when unmounting the component", () => {
        // Given: A close handler and action
        const onCloseSpy = jest.spyOn(ProvenanceSidebar.prototype, "onClose");
        const closeItemDetails = jest.fn();
        const actions = {
            closeItemDetails: closeItemDetails
        };
        // And: The component is mounted
        const component = mount(<ProvenanceSidebar {...validProps}
                                                   actions={actions}/>);

        // When: The component is unmounted
        component.unmount();

        // Then: The close handler and action is called
        expect(closeItemDetails).toHaveBeenCalled();
        expect(onCloseSpy).toHaveBeenCalled();
    });
});

describe("mapDispatchToProps", () => {
    it("Should map the fetchItemProvenance action", () => {
        const dispatch = jest.fn();

        mapDispatchToProps(dispatch).actions.fetchItemProvenance(identifiers);

        expect(dispatch).toBeCalledWith(expect.objectContaining({
            type: FETCH_ITEM_PROVENANCE
        }));
    });
    it("Should map the fetchSubmittedItemProvenance action", () => {
        const dispatch = jest.fn();

        mapDispatchToProps(dispatch).actions.fetchSubmittedItemProvenance(identifiers);

        expect(dispatch).toBeCalledWith(expect.objectContaining({
            type: FETCH_SUBMITTED_ITEM_PROVENANCE
        }));
    });
    it("Should map the closeItemDetails action", () => {
        const dispatch = jest.fn();

        mapDispatchToProps(dispatch).actions.closeItemDetails();

        expect(dispatch).toBeCalledWith(closeItemDetails());
    });
});

const identifiers = {
    studyId: "studyId",
    eventId: "eventId",
    subjectId: "subjectId",
    itemId: "itemId"
};

const validProps = {
    actions: {
        fetchItemProvenance: jest.fn(),
        fetchSubmittedItemProvenance: jest.fn(),
        openItemDetails: jest.fn(),
        closeItemDetails: jest.fn(),
        toggleNavigationSidebar: jest.fn()
    },
    name: "Lab Test or Examination Short Name",
    value: "VAL",
    provenance: {
        labConcept: {
            concept: {
                code: "INSULIN"
            },
            component: "Component for insulin",
            method: "Radioimmunoassay",
            fastingStatus: "FASTING",
            specimen: "Blood"
        },
        startDate: "2019-07-08T13:35:00",
        endDate: "2019-07-09T13:35:00",
        quantitativeResult: {
            lowerLimit: 75,
            upperLimit: 100,
            value: 85,
            unit: "mg/dL"
        },
        qualitativeResult: {
            parsedInterpretation: 1,
            originalInterpretation: "positive"
        },
        vendor: "Lab"
    }
};