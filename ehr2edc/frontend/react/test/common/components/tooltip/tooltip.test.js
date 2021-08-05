import React from 'react';
import ReactDOMServer from 'react-dom/server';
import Tooltip, {ErrorTooltip} from "../../../../src/common/components/tooltip/tooltip";

describe("Rendering the component", () => {
    test("Rendering the component without props or children", () => {
        const tooltip = shallow(<Tooltip/>);

        expect(tooltip).toEqual({});
    });

    test("Rendering the component without children", () => {
        const message = "Tooltip message";
        const tooltip = shallow(<Tooltip additionalClassNames={["additional-tooltip", "tooltip-class"]}
                                         messageClassNames={["additional-message", "message-class"]}
                                         message={message}/>);

        expect((tooltip)).toEqual({});
    });

    test("Rendering the component without props", () => {
        const children = (<div>
            <p>Wrapped element</p>
        </div>);
        const tooltip = shallow(<Tooltip>
            {children}
        </Tooltip>);

        expect((tooltip.html())).toEqual(ReactDOMServer.renderToStaticMarkup(children));
    });

    test("Rendering the component without additional classes", () => {
        const children = (<div>
            <p>Wrapped element</p>
        </div>);
        const message = "Tooltip message";
        const tooltip = shallow(<Tooltip message={message}>
            {children}
        </Tooltip>);

        wrapperContains(tooltip, children);
        hasTooltip(tooltip, message);
    });

    test("Rendering the component with additional classes", () => {
        const children = (<div>
            <p>Wrapped element</p>
        </div>);
        const message = "Tooltip message";
        const tooltip = shallow(<Tooltip additionalClassNames={["additional-tooltip", "tooltip-class"]}
                                         messageClassNames={["additional-message", "message-class"]}
                                         message={message}>
            {children}
        </Tooltip>);

        wrapperContains(tooltip, children);
        hasTooltip(tooltip, message);
        wrapperHasClasses(tooltip, "tooltip", "additional-tooltip", "tooltip-class");
        messageHasClasses(tooltip, "tooltip-text", "additional-message", "message-class");
    });

    function wrapperContains(tooltip, children) {
        const wrapper = wrapperFor(tooltip);
        expect(wrapper).toHaveLength(1);
        expect((wrapper.html())).toContain(ReactDOMServer.renderToStaticMarkup(children));
    }

    function hasTooltip(tooltip, message) {
        const tooltipMessage = messageFor(tooltip);
        expect(tooltipMessage).toHaveLength(1);
        expect(tooltipMessage.text()).toEqual(message);
    }

    function wrapperHasClasses(tooltip, ...classes) {
        hasClasses(wrapperFor(tooltip), ...classes)
    }

    function messageHasClasses(tooltip, ...classes) {
        hasClasses(messageFor(tooltip), ...classes)
    }

    function hasClasses(element, ...classes) {
        classes.forEach(clazz => {
            expect(element.hasClass(clazz)).toBe(true)
        });
    }

    function wrapperFor(tooltip) {
        return tooltip.find(".tooltip");
    }

    function messageFor(tooltip) {
        return tooltip.find(".tooltip > .tooltip-text");
    }
});

describe("ErrorTooltip", () => {
    function messageClassesInWrappedTooltip(error) {
        return error.find("Tooltip").prop("messageClassNames");
    }

    test("Sets default message classes", () => {
        const error = mount(<ErrorTooltip/>);

        expect(messageClassesInWrappedTooltip(error))
            .toEqual(["alert-box", "alert", "warning"])
    });

    test("Merges additional message classes with defaults", () => {
        const error = mount(<ErrorTooltip messageClassNames={["another-error-class"]}/>);

        expect(error.find("Tooltip").prop("messageClassNames"))
            .toEqual(["alert-box", "alert", "warning", "another-error-class"])
    })
});
