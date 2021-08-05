import {StaticRouter} from "react-router-dom";
import React from 'react';
import renderer from 'react-test-renderer';
import moment from 'moment-timezone';
import StudyPopulatedEventOverview
    from "../../../../../src/apps/ehr2edc/components/study-populated-event-pane/study-populated-event-overview";
import 'core-js/fn/array/flat-map';
import * as Sinon from "sinon";

require('moment-timezone');

const fs = require('fs');
const path = require("path");
const webserviceResponseFolder = "../../../../../../../infrastructure/web/src/test/resources/samples/";
let keyCount = 0;

beforeEach(() => {
    jest.mock("react-redux");
});

describe("The overview gets rendered well", () => {
    beforeEach(() => {
        moment.tz.setDefault("utc");
        keyCount = 0;
    });
    test("The normal state with forms gets rendered well", () => {
        const forms = getFormsFromJson("getEvent-forms-present");
        const app = renderer.create(<StudyPopulatedEventOverview studyId={"study-1"}
                                                                 canSendToEDC={true}
                                                                 forms={addKeysToForms(forms)}
                                                                 historyVisible={false}
                                                                 onSelectedHistoryItemChanged={() => {}}
                                                                 historyItemLoading={false}
                                                                 historyItemInError={false}/>);
        expect(app.toJSON()).toMatchSnapshot();
    });
    test("The normal state with history shown gets rendered well", () => {
        const forms = getFormsFromJson("getEvent-forms-present");
        const app = renderer.create(<StudyPopulatedEventOverview studyId={"study-1"}
                                                                 canSendToEDC={true}
                                                                 forms={addKeysToForms(forms)}
                                                                 historyVisible={true}
                                                                 selectedEventId={"event-3"}
                                                                 historyItems={readEventPopulationHistoryData("multiple-history-items-response")}
                                                                 onSelectedHistoryItemChanged={() => {}}
                                                                 historyItemLoading={false}
                                                                 historyItemInError={false}/>);
        expect(app.toJSON()).toMatchSnapshot();
    });
    test("The unwritable state with forms gets rendered well", () => {
        const forms = getFormsFromJson("getEvent-forms-present");
        const app = renderer.create(<StudyPopulatedEventOverview studyId={"study-1"}
                                                                 canSendToEDC={false}
                                                                 forms={addKeysToForms(forms)}
                                                                 historyVisible={false}
                                                                 onSelectedHistoryItemChanged={() => {}}
                                                                 historyItemLoading={false}
                                                                 historyItemInError={false}/>);
        expect(app.toJSON()).toMatchSnapshot();
    });
    test("The normal state with forms and a submission time gets rendered well", () => {
        const forms = getFormsFromJson("getEvent-forms-present");
        const app = renderer.create(<StaticRouter>
            <StudyPopulatedEventOverview studyId={"study-1"}
                                         canSendToEDC={true}
                                         forms={addKeysToForms(forms)}
                                         lastSubmissionTime="2019-07-15T00:00:00.000Z"
                                         reviewedFormsUrl="/review"
                                         historyVisible={false}
                                         onSelectedHistoryItemChanged={() => {}}
                                         historyItemLoading={false}
                                         historyItemInError={false}/>
        </StaticRouter>);
        expect(app.toJSON()).toMatchSnapshot();
    });
    test("The normal state with an error", () => {
        const error = {
            response: {
                data: {
                    issues: [{reference: "guid", field: "formItems", message: "Duplicate entry"}]
                }
            }
        };
        const app = mount(<StudyPopulatedEventOverview studyId={"study-1"}
                                                       contact="support@custodix.com"
                                                       canSendToEDC={true}
                                                       forms={[]}
                                                       historyVisible={false}
                                                       onSelectedHistoryItemChanged={() => {}}
                                                       historyItemLoading={false}
                                                       historyItemInError={false}/>);

        app.setProps({error: error});
        app.update();

        expect(app).toMatchSnapshot();
    });
    test("The blank state gets rendered well", () => {
        const forms = getFormsFromJson("getEvent-forms-empty");
        const app = renderer.create(<StudyPopulatedEventOverview studyId={"study-1"}
                                                                 canSendToEDC={true}
                                                                 forms={forms}
                                                                 historyVisible={false}
                                                                 onSelectedHistoryItemChanged={() => {}}
                                                                 historyItemLoading={false}
                                                                 historyItemInError={false}/>);
        expect(app.toJSON()).toMatchSnapshot();
    });
    test("The component renders the blank state by default", () => {
        const app = renderer.create(<StudyPopulatedEventOverview studyId={"study-1"}
                                                                 historyVisible={false}
                                                                 onSelectedHistoryItemChanged={() => {}}
                                                                 historyItemLoading={false}
                                                                 historyItemInError={false}/>);
        expect(app.toJSON()).toMatchSnapshot();
    });
    test("The component renders errors in a Form correctly", () => {
        const forms = getFormsFromJson("getEvent-forms-present");
        const message = "Duplicate entry";
        const error = {
            response: {
                data: {
                    issues: [{reference: "guid-in-logs", field: "form-1", message}]
                }
            }
        };
        const app = mount(<StudyPopulatedEventOverview studyId={"study-1"}
                                                       canSendToEDC={false}
                                                       forms={addKeysToForms(forms)}
                                                       historyVisible={false}
                                                       onSelectedHistoryItemChanged={() => {}}
                                                       historyItemLoading={false}
                                                       historyItemInError={false}/>);

        app.setProps({error: error});
        app.update();

        expect(app.find("Tooltip").find({message})).toMatchSnapshot()
    });
    test("The component renders errors in a ItemGroup correctly", () => {
        const forms = getFormsFromJson("getEvent-forms-present");
        const message = "Duplicate entry";
        const error = {
            response: {
                data: {
                    issues: [{reference: "guid-in-logs", field: "group-2", message}]
                }
            }
        };
        const app = mount(<StudyPopulatedEventOverview studyId={"study-1"}
                                                       canSendToEDC={false}
                                                       forms={addKeysToForms(forms)}
                                                       historyVisible={false}
                                                       onSelectedHistoryItemChanged={() => {}}
                                                       historyItemLoading={false}
                                                       historyItemInError={false}/>);

        app.setProps({error: error});
        app.update();

        expect(app.find("Tooltip").find({message})).toMatchSnapshot()
    });
    test("The component renders errors in a Item correctly", () => {
        const forms = getFormsFromJson("getEvent-forms-present");
        const message = "Duplicate entry";
        const error = {
            response: {
                data: {
                    issues: [{reference: "guid-in-logs", field: "item-3", message}]
                }
            }
        };
        const app = mount(<StudyPopulatedEventOverview studyId={"study-1"}
                                                       canSendToEDC={false}
                                                       forms={addKeysToForms(forms)}
                                                       historyVisible={false}
                                                       onSelectedHistoryItemChanged={() => {}}
                                                       historyItemLoading={false}
                                                       historyItemInError={false}/>);

        app.setProps({error: error});
        app.update();

        expect(app.find("Tooltip").find({message})).toMatchSnapshot()
    });
    test("The state where the history is loaded, but the forms are still loading renders as expected", () => {
        const app = renderer.create(<StudyPopulatedEventOverview studyId={"study-1"}
                                                                 canSendToEDC={true}
                                                                 forms={[]}
                                                                 historyVisible={false}
                                                                 onSelectedHistoryItemChanged={() => {}}
                                                                 historyItemLoading={true}
                                                                 historyItemInError={false}/>);
        expect(app.toJSON()).toMatchSnapshot();
    });
    test("The state where the history is loaded, but the forms are in error renders as expected", () => {
        const app = renderer.create(<StudyPopulatedEventOverview studyId={"study-1"}
                                                                 canSendToEDC={true}
                                                                 forms={[]}
                                                                 historyVisible={false}
                                                                 onSelectedHistoryItemChanged={() => {}}
                                                                 historyItemLoading={false}
                                                                 historyItemInError={true}/>);
        expect(app.toJSON()).toMatchSnapshot();
    });
});

describe("The events get fired correctly", () => {
    const forms = getFormsFromJson("getEvent-forms-present");
    beforeEach(() => {
        moment.tz.setDefault("utc");
        keyCount = 0;
    });

    test("Changing an unchecked form item group will trigger the onGroupSelected event with the selected group id", () => {
        const onGroupSelected = jest.fn();
        const app = mount(<StudyPopulatedEventOverview studyId={"study-1"}
                                                       canSendToEDC={true}
                                                       forms={addKeysToForms(forms)}
                                                       actions={{onGroupSelected}}
                                                       selectedItems={[]}
                                                       historyVisible={false}
                                                       onSelectedHistoryItemChanged={() => {}}
                                                       historyItemLoading={false}
                                                       historyItemInError={false}/>);
        app.find("#select-item-group-group-8").simulate("change", {target: {value: ""}});

        expect(onGroupSelected).toHaveBeenCalledWith("group-8");
        expect(onGroupSelected).toHaveBeenCalledTimes(1);
    });
    test("Changing an checked form item group will trigger the onGroupDeselected event with the selected group id", () => {
        const onGroupDeselected = jest.fn();
        const app = mount(<StudyPopulatedEventOverview studyId={"study-1"}
                                                       canSendToEDC={true}
                                                       forms={addKeysToForms(forms)}
                                                       actions={{onGroupDeselected}}
                                                       selectedItems={["item-9", "item-10", "item-11", "item-12", "item-13", "item-14", "item-15"]}
                                                       historyVisible={false}
                                                       onSelectedHistoryItemChanged={() => {}}
                                                       historyItemLoading={false}
                                                       historyItemInError={false}/>);
        app.find("#select-item-group-group-8").simulate("change", {target: {value: ""}});

        expect(onGroupDeselected).toHaveBeenCalledWith("group-8");
        expect(onGroupDeselected).toHaveBeenCalledTimes(1);
    });
    test("Changing an unchecked form will trigger the onFormSelected event with the selected from id", () => {
        const onFormSelected = jest.fn();
        const app = mount(<StudyPopulatedEventOverview studyId={"study-1"}
                                                       canSendToEDC={true}
                                                       forms={addKeysToForms(forms)}
                                                       actions={{onFormSelected}}
                                                       selectedItems={[]}
                                                       historyVisible={false}
                                                       onSelectedHistoryItemChanged={() => {}}
                                                       historyItemLoading={false}
                                                       historyItemInError={false}/>);
        app.find("#select-form-form-1").simulate("change", {target: {value: ""}});

        expect(onFormSelected).toHaveBeenCalledWith("form-1");
        expect(onFormSelected).toHaveBeenCalledTimes(1);
    });
    test("Changing an checked form will trigger the onFormDeselected event with the selected from id", () => {
        const onFormDeselected = jest.fn();
        const app = mount(<StudyPopulatedEventOverview studyId={"study-1"}
                                                       canSendToEDC={true}
                                                       forms={addKeysToForms(forms)}
                                                       actions={{onFormDeselected}}
                                                       selectedItems={["item-3", "item-4", "item-5", "item-6"]}
                                                       historyVisible={false}
                                                       onSelectedHistoryItemChanged={() => {}}
                                                       historyItemLoading={false}
                                                       historyItemInError={false}/>);
        app.find("#select-form-form-1").simulate("change", {target: {value: ""}});

        expect(onFormDeselected).toHaveBeenCalledWith("form-1");
        expect(onFormDeselected).toHaveBeenCalledTimes(1);
    });
    test("Clicking the submit button will trigger the onReviewedFormsSend event", () => {
        const onReviewSend = jest.fn();
        const app = mount(<StudyPopulatedEventOverview studyId={"study-1"}
                                                       canSendToEDC={true}
                                                       forms={addKeysToForms(forms)}
                                                       onReviewedFormsSend={onReviewSend}
                                                       selectedItems={["item-3", "item-4", "item-5", "item-6"]}
                                                       historyVisible={false}
                                                       onSelectedHistoryItemChanged={() => {}}
                                                       historyItemLoading={false}
                                                       historyItemInError={false}/>);
        app.find(".send-forms").simulate("click", {target: {value: ""}});

        expect(onReviewSend).toHaveBeenCalledWith();
        expect(onReviewSend).toHaveBeenCalledTimes(1);
    });
    test("Clicking the submit button won't trigger the onReviewedFormsSend event when no EDC is available", () => {
        const onReviewSend = jest.fn();
        const app = mount(<StudyPopulatedEventOverview studyId={"study-1"}
                                                       canSendToEDC={false}
                                                       forms={addKeysToForms(forms)}
                                                       onReviewedFormsSend={onReviewSend}
                                                       selectedItems={["item-3", "item-4", "item-5", "item-6"]}
                                                       historyVisible={false}
                                                       onSelectedHistoryItemChanged={() => {}}
                                                       historyItemLoading={false}
                                                       historyItemInError={false}/>);
        app.find(".send-forms").simulate("click", {target: {value: ""}});

        expect(onReviewSend).not.toHaveBeenCalledWith();
    });
});

describe("Component interaction", () => {
    const forms = getFormsFromJson("getEvent-forms-present");
    const error = {
        response: {
            data: {
                issues: [{reference: "guid-in-logs", field: "item-3", message: "Duplicate entry"}]
            }
        }
    };
    const scrollSpy = Sinon.stub(StudyPopulatedEventOverview.prototype, "scrollTo");
    const app = mount(<StudyPopulatedEventOverview studyId={"study-1"}
                                                   canSendToEDC={true}
                                                   forms={addKeysToForms(forms)}
                                                   historyVisible={false}
                                                   onSelectedHistoryItemChanged={() => {}}
                                                   historyItemLoading={false}
                                                   historyItemInError={false}/>);

    test("Collapsing a form sets the hidden checkbox to true", () => {
        const collapsibleForm = CollapsibleForm(app, "form-1");

        // Given: an expanded form
        expect(collapsibleForm.isToggled()).toBe(false);
        // When: clicking the toggle
        collapsibleForm.toggle();
        // Then: the form is collapsed
        expect(collapsibleForm.isToggled()).toBe(true);
    });

    test("Clicking an error message expands the corresponding form", () => {
        // Given: An error response
        app.setProps({error: error});
        app.update();
        // And: A collapsed form
        const collapsibleForm = CollapsibleForm(app, "form-1");
        collapsibleForm.toggle();

        // When: clicking the error
        const errorAnchor = app.find("RequestError ul a:first-child");
        expect(errorAnchor.text()).toEqual("Duplicate entry");
        errorAnchor.simulate('click');
        app.update();

        // Then: The form is expanded to allow scrolling to the violating item
        expect(collapsibleForm.isToggled()).toBe(false);
    });

    test("Clicking an error message triggers the 'scrollTo' function", () => {
        app.setProps({error: error});
        app.update();

        const errorAnchor = app.find("RequestError ul a:first-child");
        expect(errorAnchor.text()).toEqual("Duplicate entry");
        errorAnchor.simulate('click');

        expect(scrollSpy.called).toBe(true);
        expect(scrollSpy.getCall(0).args[0]).toBe("item-3");
    });
});

describe("Populated event history events", () => {
    test("Clicking a history item triggers the onSelectedHistoryItemChanged event", () => {
        const onSelectedHistoryItemChanged = jest.fn();
        const app = mount(<StudyPopulatedEventOverview studyId={"study-1"}
                                                       canSendToEDC={false}
                                                       forms={[]}
                                                       onReviewedFormsSend={jest.fn()}
                                                       selectedItems={[]}
                                                       historyItems={readEventPopulationHistoryData("multiple-history-items-response")}
                                                       historyVisible={true}
                                                       onSelectedHistoryItemChanged={onSelectedHistoryItemChanged}
                                                       historyItemLoading={false}
                                                       historyItemInError={false}/>);

        app.find("SelectableListing li").first().simulate("click", {target: {value: ""}});

        expect(onSelectedHistoryItemChanged).toHaveBeenCalledWith("event-1");
    });
});

function readEventPopulationHistoryData(file) {
    return readFile("populatedeventhistorycontroller/" + file).historyItems;
}

function getFormsFromJson(file) {
    return readFile("eventcontroller/" + file).event.forms;
}

function readFile(file) {
    const fileContents = fs.readFileSync(path.resolve(__dirname, webserviceResponseFolder + file + ".json"), 'utf-8');
    return JSON.parse(fileContents);
}

function addKeysToForms(forms) {
    return forms.map(form => {
        const key = generateKey("form-");
        return {
            ...form,
            formKey: key,
            itemGroups: addKeysToItemGroups(form.itemGroups)
        };
    });
}

function addKeysToItemGroups(itemGroups) {
    return itemGroups.map(group => {
        const key = generateKey("group-");
        return {
            ...group,
            groupKey: key,
            items: addKeysToItems(group.items)
        };
    });
}

function addKeysToItems(items) {
    return items.map(item => {
        const key = generateKey("item-");
        return {
            ...item,
            itemKey: key
        };
    });
}

function generateKey(prefix) {
    return prefix + (++keyCount);
}

const CollapsibleForm = function (app, formId) {
    const _getToggleLabel = function () {
        return app.find(`#${formId}.form .collapsible-toggle`);
    };
    const _clickToggle = function () {
        return _getToggleLabel().getDOMNode().dispatchEvent(new Event('click'));
    };
    const _getToggleId = function () {
        return _getToggleLabel().getDOMNode().htmlFor
    };
    const _getToggleValue = function () {
        return app.find(`input.collapsed#${_getToggleId()}`).getDOMNode().checked;
    };

    return {
        isToggled: function () {
            return _getToggleValue();
        },
        toggle: function () {
            _clickToggle();
        }
    }
};