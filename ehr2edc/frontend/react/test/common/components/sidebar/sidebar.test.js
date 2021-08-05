import React from 'react';
import {Sidebar} from "../../../../src/common/components/sidebar/sidebar";

describe("Sidebar rendering", () => {
    test("Component renders", () => {
        const sidebar = shallow(<Sidebar/>);
        const expectedClasses = ["sidebar", "grid-block", "vertical"];

        const wrapped = sidebar.find("div").first();
        expectedClasses.forEach(cssClass => {
            expect(wrapped.hasClass(cssClass)).toBe(true);
        });
    });

    test("Component renders with additional class", () => {
        const additionalClass = "additionalClass";
        const sidebar = shallow(<Sidebar additionalClasses={[additionalClass]}/>);

        const wrapped = sidebar.find("div").first();
        expect(wrapped.hasClass(additionalClass)).toBe(true);
    });

    test("Component renders with multiple additional classes", () => {
        const additionalClasses = ["additionalClass", "optionalClass"];
        const sidebar = shallow(<Sidebar additionalClasses={additionalClasses}/>);

        const wrapped = sidebar.find("div").first();
        additionalClasses.forEach(cssClass => {
            expect(wrapped.hasClass(cssClass)).toBe(true);
        });
    });

    test("Component can be hidden", () => {
        const sidebar = shallow(<Sidebar hidden={true}/>);

        const wrapped = sidebar.find("div").first();
        expect(wrapped.hasClass("hide")).toBe(true);
    });

    test("Component renders children", () => {
        const sidebar = shallow((<Sidebar>
            <div>Child</div>
        </Sidebar>));

        const child = sidebar.find("div > div");
        expect(child.length).toEqual(1);
        expect(child.text()).toEqual("Child");
    })
});

describe("Collapsible sidebar", () => {
   test("Expanded state", () => {
       // Given: A collapsible sidebar
       const collapsible = shallow(<Sidebar collapsible={true}>
           <h1>Collapsible</h1>
       </Sidebar>);

       // When: The sidebar is expanded
       collapsible.setProps({ collapsed: false});

       // Then: The sidebar's children are rendered
       expect(collapsible.find("h1").exists()).toBe(true);
       // And: A toggle with description is rendered
       const toggle = collapsible.find(".toggle");
       expect(toggle.exists()).toBe(true);
       expect(toggle.find("span").text()).toEqual("Hide");
   });
   test("Collapsed state", () => {
       // Given: A collapsible sidebar
       const collapsible = shallow(<Sidebar collapsible={true}>
           <h1>Collapsible</h1>
       </Sidebar>);

       // When: The sidebar is collapsed
       collapsible.setProps({ collapsed: true});

       // Then: The sidebar's children are NOT rendered
       expect(collapsible.find("h1").exists()).toBe(false);
       // And: A toggle without description is rendered
       const toggle = collapsible.find(".toggle");
       expect(toggle.exists()).toBe(true);
       expect(toggle.find("span").exists()).toBe(false)
   });
   test("Toggle handler", () => {
       // Given: A collapsible sidebar
       const collapsible = shallow(<Sidebar collapsible={true}>
           <h1>Collapsible</h1>
       </Sidebar>);
       // And: A handler for the toggle
       const handler = jest.fn();
       collapsible.setProps({onCollapseToggle: handler});

       // When: Clicking the toggle
       const toggle = collapsible.find(".toggle");
       toggle.simulate("click");

       // Then: The handler is called
       expect(handler).toHaveBeenCalled();
   });
});