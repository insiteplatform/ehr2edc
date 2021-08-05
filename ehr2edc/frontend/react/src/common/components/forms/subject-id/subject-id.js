import React from "react";
import AutocompleteInput from "../../react-autocomplete/autocomplete-input";

export default class SubjectIdField extends React.Component {

    constructor(props) {
        super(props);
        this.subjectIdChange = this.subjectIdChange.bind(this);
    }

    render() {
        const {disabled} = this.props;
        return this.hasSuggestions() && !disabled ? this.autocomplete() : this.inputfield();
    }

    hasSuggestions() {
        return !!this.props.availableSubjects && this.props.availableSubjects.fromEDC;
    }

    autocomplete() {
        const {availableSubjects, menuStyle, openOnFocus, inputProps} = this.props;
        const extendedInputProps = {...inputProps, className: this.inputfieldClass()};
        return <AutocompleteInput
            placeholder="Select EDC ID"
            options={availableSubjects.subjectIds.map(id => {
                return {
                    value: id,
                    label: id
                };
            })}
            menuStyle={menuStyle}
            openOnFocus={openOnFocus}
            inputProps={extendedInputProps}
            onSelectionChange={selection => this.subjectIdChange(selection.value)}/>;
    }

    inputfield() {
        const {disabled, value, inputProps} = this.props;
        const focus = inputProps.autoFocus;
        return <input type="text"
                      name="subjectIdentifier"
                      required={true}
                      disabled={disabled}
                      placeholder="Subject identifier"
                      value={value}
                      className={this.inputfieldClass()}
                      onChange={e => this.subjectIdChange(e.target.value)}
                      onBlur={e => this.subjectIdChange(e.target.value.trim())}
                      autoFocus={focus}
        />;
    }

    inputfieldClass() {
        return `subject-identifier-field${!this.props.valid ? ' form-field-error' : ''}`
    }

    subjectIdChange(value) {
        this.props.onChange(value);
    }
}

SubjectIdField.defaultProps = {
    valid: true,
    disabled: false,
    value: '',
    availableSubjects: undefined,
    menuStyle: {},
    inputProps: {
        autoFocus: false
    },
    onChange: () => {
    }
};