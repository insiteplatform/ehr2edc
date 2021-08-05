import React from "react";
import AutocompleteInput from "../../react-autocomplete/autocomplete-input";

export default class PatientIdField extends React.Component {

    constructor(props) {
        super(props);
        this.patientIdChange = this.patientIdChange.bind(this);
    }

    render() {
        const {availablePatients, menuStyle} = this.props;
        return <AutocompleteInput
            placeholder="Select EHR ID"
            options={availablePatients.patientIds.map(id => {
                return {
                    value: id,
                    label: id
                };
            })}
            alwaysRender={true}
            menuStyle={menuStyle}
            inputProps={{className: this.inputfieldClass()}}
            onSelectionChange={selection => this.patientIdChange(selection.value)}/>;
    }

    inputfieldClass() {
        return `patient-identifier-field${!this.props.valid ? ' form-field-error' : ''}`
    }

    patientIdChange(value) {
        this.props.onChange(value);
    }
}

PatientIdField.defaultProps = {
    valid: true,
    value: '',
    availablePatients: {patientIds: []},
    onChange: () => {
    }
};