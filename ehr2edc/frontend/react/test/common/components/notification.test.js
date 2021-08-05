import React from 'react';
import Notification from "../../../src/common/components/notification";
import sinon from "sinon";

describe("Rendering the component", () => {

    const spyOnComponentDidMount = sinon.spy(Notification.prototype, 'componentDidMount');
    const spyOnComponentWillUnmount = sinon.spy(Notification.prototype, 'componentWillUnmount');
    const spyOnShow = sinon.spy(Notification.prototype, 'show');
    const spyHide = sinon.spy(Notification.prototype, 'scheduleHide');
    const spyOnHideNotification = sinon.spy(Notification.prototype, 'hideNotification');

    beforeEach(() => {
        spyOnComponentDidMount.resetHistory();
        spyOnComponentWillUnmount.resetHistory();
        spyOnShow.resetHistory();
        spyHide.resetHistory();
        spyOnHideNotification.resetHistory();
    });

    test("The component is mounted and ", () => {
        const notification = mount(<Notification id={"test-parent-id"}/>);
        expect(global.document.querySelector('#test-parent-id')).not.toBeNull();
        expect(global.document.querySelector('#test-parent-id').hasChildNodes()).toBeFalsy();
        notification.unmount();
        expect(global.document.querySelector('#test-parent-id')).toBeNull();

        expect(spyOnComponentDidMount.callCount).toEqual(1);
        expect(spyOnComponentWillUnmount.callCount).toEqual(1);
        expect(spyOnShow.callCount).toEqual(0);
        expect(spyHide.callCount).toEqual(0);
        expect(spyOnHideNotification.callCount).toEqual(0);
    });

    test("The mounted component displays a message", () => {
        const notification = mount(<Notification id={"test-parent-id"}/>);
        const instance = notification.instance().show("Kiekeboe!");
        expect(global.document.querySelector('div#test-parent-id').hasChildNodes()).toBeTruthy();
        notification.unmount();

        expect(spyOnComponentDidMount.callCount).toEqual(1);
        expect(spyOnComponentWillUnmount.callCount).toEqual(1);
        expect(spyOnShow.callCount).toEqual(1);
        expect(spyHide.callCount).toEqual(1);
        expect(spyOnHideNotification.callCount).toEqual(0);
    });

    test("The mounted component displays a success message", () => {
        const notification = mount(<Notification id={"test-parent-id"}/>);
        const instance = notification.instance().show("Kiekeboe!", "success");
        expect(global.document.querySelector('#test-parent-id').hasChildNodes()).toBeTruthy();
        notification.unmount();

        expect(spyOnComponentDidMount.callCount).toEqual(1);
        expect(spyOnComponentWillUnmount.callCount).toEqual(1);
        expect(spyOnShow.callCount).toEqual(1);
        expect(spyHide.callCount).toEqual(1);
        expect(spyOnHideNotification.callCount).toEqual(0);
    });

    test("The mounted component displays a warning message", () => {
        const notification = mount(<Notification id={"test-parent-id"}/>);
        const instance = notification.instance().show("Kiekeboe!", "warning");
        expect(global.document.querySelector('#test-parent-id').hasChildNodes()).toBeTruthy();
        notification.unmount();

        expect(spyOnComponentDidMount.callCount).toEqual(1);
        expect(spyOnComponentWillUnmount.callCount).toEqual(1);
        expect(spyOnShow.callCount).toEqual(1);
        expect(spyHide.callCount).toEqual(1);
        expect(spyOnHideNotification.callCount).toEqual(0);
    });

    test("The mounted component displays a secondary message", () => {
        const notification = mount(<Notification id={"test-parent-id"}/>);
        const instance = notification.instance().show("Kiekeboe!", "secondary");
        expect(global.document.querySelector('#test-parent-id').hasChildNodes()).toBeTruthy();
        notification.unmount();

        expect(spyOnComponentDidMount.callCount).toEqual(1);
        expect(spyOnComponentWillUnmount.callCount).toEqual(1);
        expect(spyOnShow.callCount).toEqual(1);
        expect(spyHide.callCount).toEqual(1);
        expect(spyOnHideNotification.callCount).toEqual(0);
    });

    test("The mounted component displays a message which disappears after timeout", async () => {
        const notification = mount(<Notification id={"test-parent-id"} timeout={1}/>);
        expect(global.document.querySelector('#test-parent-id').hasChildNodes()).toBeFalsy();
        const instance = notification.instance().show("Kiekeboe!");
        expect(global.document.querySelector('#test-parent-id').hasChildNodes()).toBeTruthy();

        await timeout(50);

        notification.unmount();
        expect(global.document.querySelector('#test-parent-id')).toBeNull();

        expect(spyOnComponentDidMount.callCount).toEqual(1);
        expect(spyOnComponentWillUnmount.callCount).toEqual(1);
        expect(spyOnShow.callCount).toEqual(1);
        expect(spyHide.callCount).toEqual(1);
        expect(spyOnHideNotification.callCount).toEqual(1);
    });

});

export const timeout = ms => ({
    then: resolve => {
        setTimeout(resolve, ms)
    }
})