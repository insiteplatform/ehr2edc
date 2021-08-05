import React from 'react';
import renderer from 'react-test-renderer';
import AutocompleteInput from "../../../../src/common/components/react-autocomplete/autocomplete-input";

describe("Autocomplete rendering", () => {
    test("Autocomplete without options gets rendered well", () => {
        const autocomplete = renderer.create(
            <AutocompleteInput/>
        );
        let tree = autocomplete.toJSON();
        expect(tree).toMatchSnapshot();
    });

    test("Autocomplete without options, but with a placeholder text gets rendered well", () => {
        const autocomplete = renderer.create(
            <AutocompleteInput options={[]} placeholder={"Select an item"}/>
        );
        let tree = autocomplete.toJSON();
        expect(tree).toMatchSnapshot();
    });

    test("Autocomplete passes inputProps to rendered inputfield", () => {
        const autocomplete = renderer.create(
            <AutocompleteInput options={[]} inputProps={{
                className: "override-autocomplete-class"
            }} placeholder={"Select an item"}/>
        );
        let tree = autocomplete.toJSON();
        expect(tree).toMatchSnapshot();
    });

    test("Autocomplete without options, changing input to value not in options", () => {
        let onSelectionChange = jest.fn();

        const autocomplete = mount(
            <AutocompleteInput options={[]} placeholder={"Select an item"} onSelectionChange={onSelectionChange}/>
        );

        const input = autocomplete.find('input');
        input.simulate('change', {target: {value: 'Hello'}});
        expect(onSelectionChange.mock.calls.length).toEqual(1);
        expect(input.get(0).value).toBe(undefined);
        autocomplete.unmount();
    });

    test("Autocomplete with options, changing input to value in options", () => {
        var selected = null;

        const autocomplete = mount(
            <AutocompleteInput options={[{value: "hello", label: "Hello"}]} placeholder={"Select an item"}
                               onSelectionChange={(item) => selected = item}/>
        );

        const input = autocomplete.find('input');
        input.simulate('change', {target: {value: 'he'}});

        input.simulate('focus');

        // simulate keyUp of last key, triggering autocomplete suggestion + selection of the suggestion in the menu
        input.simulate('keyUp', {key: 'r', keyCode: 82, which: 82});

        // Hit enter, updating state.value with the selected Autocomplete suggestion
        input.simulate('keyDown', {
            key: 'Enter', keyCode: 13, which: 13, preventDefault() {
            }
        });

        expect(selected.value).toBe('hello');
        expect(input.instance().value).toBe('Hello');
        autocomplete.unmount();
    });

    test("Autocomplete with options, changing input to complete value in options", () => {
        var selected = null;

        const autocomplete = mount(
            <AutocompleteInput options={[{value: "hello", label: "world"}]} placeholder={"Select an item"}
                               onSelectionChange={(item) => selected = item}/>
        );

        const input = autocomplete.find('input');
        input.simulate('change', {target: {value: 'hello'}});

        expect(selected.value).toBe('hello');
        expect(input.instance().value).toBe('hello');
        autocomplete.unmount();
    });

    test("Autocomplete with options, changing input should trigger the callback function with input value", () => {
        const onSelectionChangeCallback = jest.fn();

        const autocomplete = mount(
            <AutocompleteInput options={[{value: "hello", label: "world"}]} placeholder={"Select an item"}
                               onSelectionChange={onSelectionChangeCallback}/>
        );

        const input = autocomplete.find('input');
        input.simulate('change', {target: {value: 'hello'}});

        expect(onSelectionChangeCallback).toHaveBeenCalledWith({value: "hello", label: "hello"});
        expect(input.instance().value).toBe('hello');
        autocomplete.unmount();
    });

    test("Autocomplete with options, changing input should trigger the callback function with input value event if the input value is not in the options list", () => {
        const onSelectionChangeCallback = jest.fn();

        const autocomplete = mount(
            <AutocompleteInput options={[{value: "hello", label: "world"}]} placeholder={"Select an item"}
                               onSelectionChange={onSelectionChangeCallback}/>
        );

        const input = autocomplete.find('input');
        input.simulate('change', {target: {value: 'notInTheOptionValue'}});

        expect(onSelectionChangeCallback).toHaveBeenCalledWith({value: "notInTheOptionValue", label: "notInTheOptionValue"});
        expect(input.instance().value).toBe('notInTheOptionValue');
        autocomplete.unmount();
    });

    test("Autocomplete with options, changing input to complete value with whitespace in options", () => {
        var selected = null;

        const autocomplete = mount(
            <AutocompleteInput options={[{value: "hello", label: "world"}]} placeholder={"Select an item"}
                               onSelectionChange={(item) => selected = item}/>
        );

        const input = autocomplete.find('input');
        const inputValue = '   hello \t';
        input.simulate('change', {target: {value: inputValue}});

        expect(selected.value).toBe("hello");
        expect(input.instance().value).toBe(inputValue);
        autocomplete.unmount();
    });

    test("Autocomplete without focus is closed", () => {
        let selected = null;
        const autocomplete = mount(
            <AutocompleteInput options={[{value: "hello", label: "world"}]} placeholder={"Select an item"}
                               onSelectionChange={(item) => selected = item}
                               openOnFocus={false}
            />
        );

        expect(autocomplete.find('Autocomplete').state("isOpen")).toBe(false);
    });

    test("Autocomplete with options, that should not automatically open on focus", () => {
        let selected = null;
        const autocomplete = mount(
            <AutocompleteInput options={[{value: "hello", label: "world"}]} placeholder={"Select an item"}
                               onSelectionChange={(item) => selected = item}
                               openOnFocus={false}
            />
        );

        const input = autocomplete.find('input');
        input.simulate('focus');

        expect(autocomplete.find('Autocomplete').state("isOpen")).toBe(true);
        expect(autocomplete.find('Autocomplete').find(".autocomplete-menu").children().length).toBe(0);

        document.dispatchEvent(new KeyboardEvent('keydown', {keyCode: 40}));
        autocomplete.update();

        expect(autocomplete.find('Autocomplete').find(".autocomplete-menu").children().length).toBe(1);
    });

    test("Autocomplete with options, that should open on focus", () => {
        let selected = null;
        const autocomplete = mount(
            <AutocompleteInput options={[{value: "hello", label: "world"}]} placeholder={"Select an item"}
                               onSelectionChange={(item) => selected = item}
                               openOnFocus={true}
            />
        );

        const input = autocomplete.find('input');
        input.simulate('focus');
        autocomplete.update();

        expect(autocomplete.find('Autocomplete').find(".autocomplete-menu").children().length).toBe(1);
    });
});
describe("Value matches user input", () => {
    const item = {
        value: "potential investigator",
        label: "Potential Investigator"
    };
    const autocomplete = new AutocompleteInput({});

    test("exact match", () => {
        expect(autocomplete.matches(item, "Potential Investigator")).toBe(true);
    });
    test("partial match", () => {
        expect(autocomplete.matches(item, "Potential")).toBe(true);
        expect(autocomplete.matches(item, "Investigator")).toBe(true);
        expect(autocomplete.matches(item, "gator")).toBe(true);
    });
    test("case-insensitive match", () => {
        expect(autocomplete.matches(item, "potential investigator")).toBe(true);
        expect(autocomplete.matches(item, "POTENTIAL INVESTIGATOR")).toBe(true);
    });
    test("case-insensitive partial match", () => {
        expect(autocomplete.matches(item, "potential")).toBe(true);
        expect(autocomplete.matches(item, "INVESTIGATOR")).toBe(true);
        expect(autocomplete.matches(item, "GATOR")).toBe(true);
    });
    test("no match", () => {
        expect(autocomplete.matches(item, "subject")).toBe(false);
    });
});