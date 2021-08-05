import React from 'react';
import renderer from 'react-test-renderer';
import AddInvestigator from "../../../../../src/apps/ehr2edc/components/study-investigators-pane/add-investigator";

'use strict';
const fs = require('fs');
const path = require("path");

const webserviceResponseFolder = "../../../../../../../infrastructure/web/src/test/resources/samples/";
const potentialInvestigatorResponse = readData("userscontroller/unassigned-response");

function readData(filePath) {
    const fileContents = fs.readFileSync(path.resolve(__dirname, webserviceResponseFolder + filePath + ".json"), 'utf-8');
    return JSON.parse(fileContents);
}

describe("Component gets rendered well", () => {
    test("The component is closed by default", () => {
        const app = renderer.create(<AddInvestigator
            potentialInvestigators={potentialInvestigatorResponse.potentialInvestigators}/>);
        const tree = app.toJSON();
        expect(tree).toMatchSnapshot();
    });
    test("Closed state gets rendered well", () => {
        const app = renderer.create(<AddInvestigator
            potentialInvestigators={potentialInvestigatorResponse.potentialInvestigators}
            isOpen={false}/>);
        const tree = app.toJSON();
        expect(tree).toMatchSnapshot();
    });
    test("Open state gets rendered well", () => {
        const app = renderer.create(<AddInvestigator
            potentialInvestigators={potentialInvestigatorResponse.potentialInvestigators}
            isOpen={true}/>);
        const tree = app.toJSON();
        expect(tree).toMatchSnapshot();
    });
    test("The empty state is rendered when no investigators are passed", () => {
        const app = renderer.create(<AddInvestigator/>);
        const tree = app.toJSON();
        expect(tree).toMatchSnapshot();
    });
    test("The empty state is rendered when an empty list is passed", () => {
        const app = renderer.create(<AddInvestigator potentialInvestigators={[]}/>);
        const tree = app.toJSON();
        expect(tree).toMatchSnapshot();
    });
});
describe("Switching between the open and closed state manually", () => {
    test("Clicking the add button in the closed state switches to the open state", async () => {
        jest.useFakeTimers();
        const app = mount(<AddInvestigator potentialInvestigators={potentialInvestigatorResponse.potentialInvestigators}
                                           isOpen={false}/>);
        await Promise.resolve();
        app.find('.open-add-investigator').simulate('click', {});
        app.update();
        expect(app).toMatchSnapshot();
    });
    test("Clicking the cancel button in the open state switches to the closed state", async () => {
        jest.useFakeTimers();
        const app = mount(<AddInvestigator potentialInvestigators={potentialInvestigatorResponse.potentialInvestigators}
                                           isOpen={true}/>);
        await Promise.resolve();
        app.find('.close-add-investigator').simulate('click', {});
        app.update();
        expect(app).toMatchSnapshot();
    });
});
describe("Interactions with the form", () => {
    let onInvestigatorAdd = jest.fn();

    beforeEach(() => {
        onInvestigatorAdd.mockReset();
    });

    async function getAddInvestigatorPanelWithData() {
        jest.useFakeTimers();
        return mount(<AddInvestigator potentialInvestigators={potentialInvestigatorResponse.potentialInvestigators}
                                      onInvestigatorAdd={onInvestigatorAdd}
                                      isOpen={true}/>);
    }

    function focusAndOpenAutocomplete(app) {
        app.find('.autocomplete-input').simulate('focus', {});
        document.dispatchEvent(new KeyboardEvent('keydown', {keyCode: 40}));
        app.update();
    }

    function findAutocompleteItem(app, itemKey) {
        return app.findWhere(item => item.key() === itemKey);
    }

    test("The autocomplete component doesn't show anything when no interactions have happened", async () => {
        const app = await getAddInvestigatorPanelWithData();
        app.update();
        expect(app).toMatchSnapshot();
    });
    test("Giving the autocomplete box focus opens the full list of investigators that can be added", async () => {
        const app = await getAddInvestigatorPanelWithData();
        app.find('.autocomplete-input').simulate('focus', {});
        await Promise.resolve();
        app.update();
        expect(app).toMatchSnapshot();
    });
    test("Taking away the autocomplete box's focus hides the list of investigators that can be added", async () => {
        const app = await getAddInvestigatorPanelWithData();
        app.find('.autocomplete-input').simulate('focus', {});
        app.find('.autocomplete-input').simulate('blur', {});
        await Promise.resolve();
        app.update();
        expect(app).toMatchSnapshot();
    });
    test("Typing in the autocomplete box filters the list of investigators that can be added", async () => {
        const app = await getAddInvestigatorPanelWithData();
        app.find('.autocomplete-input').simulate('change', {target: {value: "1"}});
        app.find('.autocomplete-input').simulate('focus', {});
        await Promise.resolve();
        app.update();
        expect(app).toMatchSnapshot();
    });
    test("Hovering over an item in the autocomplete menu adds an highlight to it", async () => {
        const app = await getAddInvestigatorPanelWithData();
        focusAndOpenAutocomplete(app);
        findAutocompleteItem(app, 'User id. 7').simulate('mouseenter', {});
        await Promise.resolve();
        app.update();
        expect(app.findWhere(item => item.key() === 'User id. 7').hasClass("highlighted")).toBe(true)
    });
    test("Clicking an item in the autocomplete menu fills it into the autocomplete element", async () => {
        const app = await getAddInvestigatorPanelWithData();
        focusAndOpenAutocomplete(app);
        findAutocompleteItem(app, 'User id. 3').simulate('click', {});
        await Promise.resolve();
        app.update();
        expect(app.find('.autocomplete-input').getDOMNode().value).toEqual("User nr. 3");
    });
    test("Submitting the form with valid input", async () => {
        // Given: A selected potential investigator
        const spyOnSubmit = jest.spyOn(AddInvestigator.prototype, 'handleSubmit');
        const app = await getAddInvestigatorPanelWithData();
        focusAndOpenAutocomplete(app);
        findAutocompleteItem(app, 'User id. 3').simulate('click', {});

        // When: Submitting the form
        app.find('form').simulate('submit');

        // Then: The handler was called
        expect(spyOnSubmit).toHaveBeenCalled();
        // And: The callback was triggered
        expect(onInvestigatorAdd).toHaveBeenCalledWith({
            id: 'User id. 3',
            name: 'User nr. 3'
        })
    });
    test("Submitting the form with invalid input", async () => {
        // Given: A selected potential investigator
        const spyOnSubmit = jest.spyOn(AddInvestigator.prototype, 'handleSubmit');
        const app = await getAddInvestigatorPanelWithData();
        focusAndOpenAutocomplete(app);

        // When: Submitting the form
        app.find('form').simulate('submit');

        // Then: The handler was called
        expect(spyOnSubmit).toHaveBeenCalled();
        // And: The callback was triggered
        expect(onInvestigatorAdd).not.toHaveBeenCalled()
    });
    test("Closing the form with the 'Esc'-key", async () => {
        // Given: A form with opened autocomplete
        const app = await getAddInvestigatorPanelWithData();
        focusAndOpenAutocomplete(app);

        // When: Pressing Esc
        document.dispatchEvent(new KeyboardEvent('keydown', {keyCode: 27}));
        app.update();

        // Then: The form was closed
        expect(app.find("form").exists()).toBe(false);
    });
});
describe("Clicking the add button", () => {
    function selectInvestigator(app) {
        app.find('.autocomplete-input').simulate('focus', {});
        document.dispatchEvent(new KeyboardEvent('keydown', {keyCode: 40}));
        app.update();
        app.findWhere(item => item.key() === 'User id. 3').simulate('click', {});
    }

    test("Clicking the add button without selecting an investigator does nothing", async () => {
        jest.useFakeTimers();
        let onInvestigatorAdd = jest.fn();
        const app = mount(<AddInvestigator potentialInvestigators={potentialInvestigatorResponse.potentialInvestigators}
                                           isOpen={true}
                                           onInvestigatorAdd={onInvestigatorAdd}/>);

        app.find(".add-investigator").simulate('click', {});

        expect(onInvestigatorAdd.mock.calls.length).toEqual(0);
    });
    test("Clicking the add button after selecting an investigator fires an event", async () => {
        jest.useFakeTimers();
        let onInvestigatorAdd = jest.fn();
        const app = mount(<AddInvestigator potentialInvestigators={potentialInvestigatorResponse.potentialInvestigators}
                                           isOpen={true}
                                           onInvestigatorAdd={onInvestigatorAdd}/>);

        selectInvestigator(app);
        app.find(".add-investigator").simulate('click', {});

        expect(onInvestigatorAdd.mock.calls.length).toEqual(1);
    });
    test("Clicking the add button after selecting an investigator passes the correct investigator", async () => {
        jest.useFakeTimers();
        let onInvestigatorAdd = jest.fn();
        const app = mount(<AddInvestigator potentialInvestigators={potentialInvestigatorResponse.potentialInvestigators}
                                           isOpen={true}
                                           onInvestigatorAdd={onInvestigatorAdd}/>);

        selectInvestigator(app);
        app.find(".add-investigator").simulate('click', {});

        expect(onInvestigatorAdd).toHaveBeenCalledWith({"id": "User id. 3", "name": "User nr. 3"});
    });
});