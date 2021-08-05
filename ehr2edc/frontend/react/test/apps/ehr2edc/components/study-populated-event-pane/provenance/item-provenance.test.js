import React from "react"
import {ItemProvenance} from "../../../../../../src/apps/ehr2edc/components/study-populated-event-pane/provenance/item-provenance";
import readDataFromFile from "../../../../../__helper__/sample-data-file";

describe("Item Provenance", () => {
    test("Component does not render without item", () => {
        // Given: A ItemProvenance component
        const comp = mount(<ItemProvenance/>);

        // When: No item is present
        comp.setProps({item: undefined});
        comp.update();

        // Then: No component is rendered
        expect(comp).toEqual({});
    });

    test("Component renders loading state", () => {
        // Given: A ItemProvenance component
        const comp = mount(<ItemProvenance/>);

        // When: An item is present
        comp.setProps({
            item: {
                name: "Lab Test or Examination Short Name",
                value: "VAL",
                provenance: undefined
            }
        });

        // Then: The component is rendered
        expect(comp).toMatchSnapshot();
    });

    test("Component renders error state", () => {
        // Given: A ItemProvenance component
        const comp = mount(<ItemProvenance/>);

        // When: An item is present
        comp.setProps({
            item: {
                name: "Lab Test or Examination Short Name",
                value: "VAL",
                provenance: {
                    error: {
                        response: {
                            data: {
                                "issues": [{
                                    "reference": null,
                                    "field": null,
                                    "message": "Something happened in the back-end"
                                }]
                            }
                        }
                    },
                    data: undefined
                }
            }
        });

        // Then: The component is rendered
        expect(comp).toMatchSnapshot();
    });

    test("Component renders success state", () => {
        // Given: A ItemProvenance component
        const comp = mount(<ItemProvenance/>);

        // When: An item is present
        comp.setProps({
            item: {
                name: "Lab Test or Examination Short Name",
                value: "VAL",
                provenance: {
                    error: undefined,
                    data: {
                        "groups": [
                            {
                                "label": "Group 1",
                                "items": [
                                    {
                                        "label": "Item 1",
                                        "value": "Value 1"
                                    },
                                    {
                                        "label": "Item 2",
                                        "value": "Value 2"
                                    },
                                    {
                                        "label": "Item 3",
                                        "value": ""
                                    },
                                    {
                                        "label": "Item 4",
                                        "value": undefined
                                    }
                                ]
                            }
                        ],
                        "items": [
                            {
                                "label": "Item 1",
                                "value": "Value 1"
                            },
                            {
                                "label": "Item 2",
                                "value": "Value 2"
                            },
                            {
                                "label": "Item 3",
                                "value": ""
                            },
                            {
                                "label": "Item 4",
                                "value": undefined
                            }
                        ]
                    }
                }
            }
        });

        // Then: The component is rendered
        expect(comp).toMatchSnapshot();
    })
});

describe("Provenance listing", () => {

    test("Items are rendered", () => {
        // Given: A provenance object containing items
        const provenance = {
            groups: [],
            items: [
                {label: "Item 1", value: "Value 1"},
                {label: "Item 2", value: "Value 2"}
            ]
        };

        // When: Generating the listing
        const listing = ProvenanceListing.for(provenance);

        // Then: All items are rendered correctly
        listing.hasLength(2);
        listing.hasListingFor("Item 1", "Value 1");
        listing.hasListingFor("Item 2", "Value 2");
    });

    test("Items within a group are rendered as sublisting of that group", () => {
        // Given: A provenance object containing another object
        const provenance = {
            groups: [{
                "label": "Group 1",
                "items": [
                    {
                        "label": "Item 1",
                        "value": "Value 1"
                    },
                    {
                        "label": "Item 2",
                        "value": "Value 2"
                    }
                ]
            }],
            items: []
        };

        // When: Generating the listing
        const listing = ProvenanceListing.for(provenance);

        // Then: A group is created
        listing.containsListing("Group 1");
        const objectListing = listing.sublistingFor("Group 1");
        // And: The items in the group are rendered correctly
        objectListing.hasLength(2);
        objectListing.hasListingFor("Item 1", "Value 1");
        objectListing.hasListingFor("Item 2", "Value 2");
    });

    test("Empty provenance gets rendered correctly", () => {
        const provenance = {
            groups: [],
            items: []
        };

        const listing = ProvenanceListing.for(provenance);

        listing.hasLength(0);
    });
});

describe("Application REST API complies with the component", () => {
    test("The item provenance REST API complies", () => {
        // Given: An item provenance REST response
        const response = readDataFromFile("eventcontroller/getItemProvenance.json");

        // When: Generating the listing
        const listing = ProvenanceListing.for(response);

        // Then: The provenance is rendered correctly
        assertListing(listing)
    });

    test("The submitted item provenance REST API complies", () => {
        // Given: A submitted item provenance REST response
        const response = readDataFromFile("reviewedeventcontroller/getSubmittedItemProvenance.json");

        // When: Generating the listing
        const listing = ProvenanceListing.for(response);

        // Then: The provenance is rendered correctly
        assertListing(listing)
    });

    function assertListing(listing) {
        listing.hasLength(7);
        listing.hasListingFor("Item 4", "Value 4");
        listing.hasListingFor("Item 5", "none");
        listing.hasListingFor("Item 6", "none");
        const group = listing.sublistingFor("Group 1");
        group.hasLength(3);
        group.hasListingFor("Item 1", "Value 1");
        group.hasListingFor("Item 2", "none");
        group.hasListingFor("Item 3", "none");
    }
});

const ProvenanceListing = function () {
    const _render = function (provenance) {
        return shallow(ItemProvenance.prototype.provenanceDataListing(provenance));
    };
    const _Assertions = function (listing) {
        const _allListings = function () {
            return listing.find(".row");
        };
        const _findGroup = function (key) {
            return _allListings().filterWhere(row => row.children("h6").exists() && row.children("h6").text() === key)
        };
        const _findListing = function (key) {
            return _allListings().filterWhere(row => row.children("dt").exists() && row.children("dt").text() === key)
        };
        const _listingValueMatches = function (key, value) {
            const listingValue = _findListing(key).children("dd");
            if (value.length === 1) {
                expect(listingValue.text() === value[0]).toBe(true);
            } else {
                expect(
                    value.map(val => listingValue.find("li").map(elem => elem.text()).includes(val)).includes(false)
                ).toBe(false)
            }
        };

        return {
            hasLength: function (length) {
                expect(_allListings()).toHaveLength(length);
            },
            containsListing: function (key) {
                const ungrouped = _findListing(key).exists();
                const group = _findGroup(key).exists();
                expect(ungrouped || group).toBe(true);
            },
            hasListingFor: function (key, ...value) {
                this.containsListing(key);
                _listingValueMatches(key, value);
            },
            sublistingFor: function (key) {
                const sublisting = _findGroup(key).children("dl");
                return _Assertions(sublisting);
            }
        }
    };

    return {
        for: function (provenance) {
            const listing = _render(provenance);
            return _Assertions(listing);
        }
    }
}();