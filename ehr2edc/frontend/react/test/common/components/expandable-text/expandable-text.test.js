import React from 'react';
import ExpandableText from "../../../../src/common/components/expandable-text/expandable-text";

describe("Rendering the component", () => {
    test("Does not render without children", () => {
        let expandableText = shallow(<ExpandableText/>);

        expect(expandableText).toEqual({});
    });
    test("Renders in closed state without props", () => {
        let expandableText = shallow(<ExpandableText>
            <p>Lorem ipsum dolor sit amet, consectetur adipisicing elit. Ab accusamus alias atque deleniti eius
                eligendi, fugit labore, libero nobis odio odit officia temporibus unde. Blanditiis debitis et labore
                soluta totam?</p>
        </ExpandableText>);

        assertCollapsed(expandableText)
        expect(expandableText).toMatchSnapshot();
    });
    test("Renders in closed state with expanded=false", () => {
        let expandableText = renderWithExpanded(false);

        assertCollapsed(expandableText);
        expect(expandableText).toMatchSnapshot();
    });
    test("Renders in open state with expanded=true", () => {
        let expandableText = renderWithExpanded(true);

        assertExpanded(expandableText);
        expect(expandableText).toMatchSnapshot();
    });
});

describe("Expanding and collapsing the component", () => {
    test("Expands a closed component when the action trigger is clicked", () => {
        //given
        let expandableText = renderWithExpanded(false);
        assertCollapsed(expandableText);
        // when
        clickActionTrigger(expandableText);
        //then
        assertExpanded(expandableText);
    });
    test("Expands a closed component when the action trigger is clicked", () => {
        // given
        let expandableText = renderWithExpanded(true);
        assertExpanded(expandableText);
        //when
        clickActionTrigger(expandableText);
        //then
        assertCollapsed(expandableText);
    });
});

function renderWithExpanded(expanded) {
    return shallow(<ExpandableText expanded={expanded}>
        <p>Lorem ipsum dolor sit amet, consectetur adipisicing elit. Ab accusamus alias atque deleniti eius
            eligendi, fugit labore, libero nobis odio odit officia temporibus unde. Blanditiis debitis et labore
            soluta totam?</p>
    </ExpandableText>);
}

function assertExpanded(expandableText) {
    expect(expandableText.state("expanded")).toBe(true);
    expect(expandableText.first().hasClass("expanded")).toBe(true);
    expect(expandableText.find("[data-enzyme='action']").text()).toBe("Show less");
}

function assertCollapsed(expandableText) {
    expect(expandableText.state("expanded")).toBe(false);
    expect(expandableText.first().hasClass("expanded")).toBe(false);
    expect(expandableText.find("[data-enzyme='action']").text()).toBe("Show more");
}

function clickActionTrigger(expandableText) {
    expandableText.find("[data-enzyme='action']").simulate('click');
}
