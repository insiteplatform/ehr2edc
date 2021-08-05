import React from 'react';
import Modal, {ModalErrors} from "../../../../src/common/components/react-modal/modal";
import RequestError from "../../../../src/common/components/error/backend-request-error";

const title = "test";
const content = "Test";
const error = {
    response: {
        data: {
            "issues": [{
                "reference": null,
                "field": null,
                "message": "Something happened in the back-end"
            }]
        }
    }
};
const validationErrors = [
    "Validation failed.",
    "Another validation failed"
];

describe("Rendering the component", () => {
    test("Rendering the open state", () => {
        let open = true;
        const app = mount(<Modal open={open} title={title} onClose={() => open = false}><p>{content}</p></Modal>);
        expect(app.prop("open")).toEqual(true);

        expect(app).toMatchSnapshot();
        validateOpenModal(app, title, content);
    });
    test("Rendering the open state with an action in Progress", () => {
        let open = true;
        const app = mount(<Modal open={open} title={title} inProgress={true}
                                 onClose={() => open = false}><p>{content}</p></Modal>);
        expect(app.prop("open")).toEqual(true);

        expect(app).toMatchSnapshot();
        validateOpenModal(app, title, content);
    });
    test("Rendering the closed state", () => {
        const app = mount(<Modal open={false} title={title}><p>{content}</p></Modal>);
        expect(app.prop("open")).toEqual(false);

        expect(app).toMatchSnapshot();
        validateClosedModal(app);
    });
    test("A modal is in the close state by default", () => {
        const app = mount(<Modal title={title}><p>{content}</p></Modal>);
        expect(app.prop("open")).toEqual(undefined);

        expect(app).toMatchSnapshot();
        validateClosedModal(app);
    });
    test("A modal with a fixed width and height", () => {
        const app = mount(<Modal title={title} open={true} width="800px" height="350px"><p>{content}</p></Modal>);
        expect(app.prop("open")).toEqual(true);

        expect(app).toMatchSnapshot();
        validateOpenModal(app, title, content);
    });
    test("Reloading the component", () => {
        const app = mount(<Modal title={title} open={false}><p>{content}</p></Modal>);
        app.setProps({open: true});
        app.update();

        expect(app.prop("open")).toEqual(true);
        validateOpenModal(app, title, content);
    });
    test("Reloading the component without any data", () => {
        const app = mount(<Modal title={title} open={false}><p>{content}</p></Modal>);

        app.setProps({});

        expect(app.prop("open")).toEqual(false);
        validateClosedModal(app);
    });
});

describe("Actions", () => {
    test("Clicking the close icon puts the modal in the closed state", () => {
        let open = true;
        const app = shallow(<Modal open={open} title={title} onClose={() => open = false}><p>{content}</p></Modal>);

        app.find(".modal-close-icon").simulate('click', {});
        app.setProps({open: open});
        app.update();

        expect(app.prop("isOpen")).toEqual(open);
        validateClosedModal(app);
    });
    test("Clicking the cancel button puts the modal in the closed state", () => {
        let open = true;
        const app = shallow(<Modal open={open} title={title} onClose={() => open = false}><p>{content}</p></Modal>);

        app.find(".modal-cancel").simulate('click', {});
        app.setProps({open: open});
        app.update();

        expect(app.prop("isOpen")).toEqual(open);
        validateClosedModal(app);
    });
    test("Clicking the OK-button fires the onOkButtonClick event", () => {
        let onOkButtonClick = jest.fn();
        let app = shallow(<Modal open={true} title={title} onOkButtonClick={onOkButtonClick}><p>{content}</p></Modal>);

        app.find(".modal-ok").simulate('click', {});
        app.update();

        expect(onOkButtonClick.mock.calls.length).toEqual(1);
    });
});

describe("ModalErrors", () => {
    test("Renders validationErrors correctly", () => {
        const modalErrors = shallow(<ModalErrors validationErrors={validationErrors}/>, { disableLifecycleMethods: true });

        expect(modalErrors.find("li")).toHaveLength(2);
        expect(modalErrors.find("RequestError").prop("error")).toBeUndefined();
        expect(modalErrors).toMatchSnapshot();
    });
    test("Renders requestError correctly", () => {
        const modalErrors = shallow(<ModalErrors requestError={error} contact={"cont@ct"}/>, { disableLifecycleMethods: true });

        expect(modalErrors.find("li")).toHaveLength(0);
        expect(modalErrors.exists("RequestError")).toBe(true);
        expect(modalErrors.find("RequestError").prop("error")).toEqual(error);
        expect(modalErrors).toMatchSnapshot();
    });
    test("Renders combination correctly", () => {
        const modalErrors = shallow(<ModalErrors validationErrors={validationErrors}
                                                 requestError={error}
                                                 contact={"cont@ct"}/>, { disableLifecycleMethods: true });

        expect(modalErrors.find("li")).toHaveLength(2);
        expect(modalErrors.exists("RequestError")).toBe(true);
        expect(modalErrors.find("RequestError").prop("error")).toEqual(error);
        expect(modalErrors).toMatchSnapshot();
    })
});

function validateOpenModal(wrapper, title, content) {
    expect(wrapper.find('ModalPortal').children().length).toEqual(1);
    expect(wrapper.find('.modal-title').text()).toEqual(title);
    expect(wrapper.find('.modal-content').text()).toEqual(content);
    expect(wrapper.find('.modal-ok').text()).toEqual("OK");
    expect(wrapper.find('.modal-cancel').text()).toEqual("Cancel");
}

function validateClosedModal(wrapper) {
    expect(wrapper.find('ModalPortal').children().length).toEqual(0);
}