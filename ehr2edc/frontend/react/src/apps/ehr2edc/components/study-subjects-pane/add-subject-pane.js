import React from "react"
import * as PropTypes from "prop-types"
import Modal, {ModalErrors} from "../../../../common/components/react-modal/modal";
import SubjectIdField from "../../../../common/components/forms/subject-id/subject-id";
import DatePicker from "react-datepicker";
import {Portal} from "react-overlays";
import PatientIdField from "../../../../common/components/forms/patient-id/patient-id";
import pick from "lodash.pick";

const CalendarContainer = ({children}) => {
    const el = document.getElementById('calendar-portal');
    return (
        <Portal container={el}>
            {children}
        </Portal>
    )
};

const FirstNameField = ({focus, onChange}) => {
    return <input type="text"
                  name="firstName"
                  placeholder="First name"
                  className="margin-none first-name-field"
                  onChange={e => onChange(e.target.value)}
                  onBlur={e => onChange(e.target.value.trim())}
                  autoFocus={focus} />;
};

const FirstNameHeader = ({firstNameInfoText}) =>
    <th>First name <i className="fa fa-info-circle" title={firstNameInfoText}/></th>;

const FirstNameCell = ({firstName, onFirstNameChanged}) => {
    return <td>
        <FirstNameField focus={false} firstName={firstName} onChange={onFirstNameChanged}/>
    </td>;
};

const FirstNameRow = ({firstNameInfoText, firstName, onFirstNameChanged}) => {
    return <tr>
        <FirstNameHeader {...{firstNameInfoText}}/>
        <FirstNameCell {...{firstName, onFirstNameChanged}}/>
    </tr>;
};

const LastNameField = ({focus, onChange}) => {
    return <input type="text"
                  name="lastName"
                  placeholder="Last name"
                  className="margin-none last-name-field"
                  onChange={e => onChange(e.target.value)}
                  onBlur={e => onChange(e.target.value.trim())}
                  autoFocus={focus} />;
};

const LastNameHeader = ({lastNameInfoText}) =>
    <th>Last name <i className="fa fa-info-circle" title={lastNameInfoText}/></th>;

const LastNameCell = ({lastName, onLastNameChanged}) => {
    return <td>
        <LastNameField focus={false} lastName={lastName} onChange={onLastNameChanged}/>
    </td>;
};

const LastNameRow = ({lastNameInfoText, lastName, onLastNameChanged}) => {
    return <tr>
        <LastNameHeader {...{lastNameInfoText}}/>
        <LastNameCell {...{lastName, onLastNameChanged}}/>
    </tr>;
};

const DateOfBirthHeader = ({dateOfBirthInfoText}) =>
    <th>Date of birth <i className="fa fa-info-circle" title={dateOfBirthInfoText}/></th>;

const DateOfBirthCell = ({dateOfBirth, dateOfBirthValid, onDateOfBirthChanged}) => {
    const dateOfBirthClass = `birth-date-field${dateOfBirthValid ? '' : ' form-field-error'}`;
    return <td>
        <div className="calendar-icon">
            <DatePicker popperContainer={CalendarContainer}
                        dateFormat="d LLL yyyy"
                        maxDate={new Date(Date.now())}
                        selected={dateOfBirth}
                        onChange={onDateOfBirthChanged}
                        className={dateOfBirthClass}/>
        </div>
    </td>;
};

const DateOfBirthRow = ({dateOfBirthInfoText, dateOfBirth, dateOfBirthValid, onDateOfBirthChanged}) => {
    return <tr>
        <DateOfBirthHeader {...{dateOfBirthInfoText}}/>
        <DateOfBirthCell {...{dateOfBirth, dateOfBirthValid, onDateOfBirthChanged}}/>
    </tr>;
};

const EdcIdentifierHeader = ({subjectIdentifierInfoText}) =>
    <th>EDC identifier <i className="fa fa-info-circle" title={subjectIdentifierInfoText}/></th>;

const EdcIdentifierCell = ({subjectIdentifier, subjectIdentifierValid, availableSubjects,
                               onSubjectIdentifierChanged}) => {
    return <td>
        <SubjectIdField value={subjectIdentifier}
                        valid={subjectIdentifierValid}
                        menuStyle={{maxHeight: "16em"}}
                        availableSubjects={availableSubjects}
                        onChange={onSubjectIdentifierChanged}
                        openOnFocus={true} />
    </td>;
};

const EdcIdentifierRow = ({subjectIdentifier, subjectIdentifierValid, subjectIdentifierInfoText,
                                  availableSubjects, onSubjectIdentifierChanged}) => {
    return <tr>
        <EdcIdentifierHeader {...{subjectIdentifierInfoText}}/>
        <EdcIdentifierCell {...{subjectIdentifier, subjectIdentifierValid, availableSubjects,
            onSubjectIdentifierChanged}}/>
    </tr>;
};

const EhrIdentifierSourceHeader = ({patientIdentifierSourceInfoText}) =>
    <th>EHR system <i className="fa fa-info-circle" title={patientIdentifierSourceInfoText}/></th>;

const EhrIdentifierSourceCell = ({patientIdentifierSource, patientIdentifierSources,
                                         onPatientIdentifierSourceChanged}) => {
    return <td>
        <select value={patientIdentifierSource} className="patient-identifier-source-field"
                onChange={e => onPatientIdentifierSourceChanged(e.target.value)}>
            {patientIdentifierSources.map((source, index) => <option key={index}
                                                                     value={source}>{source}</option>)}
        </select>
    </td>;
};

const EhrIdentifierSourceRow = ({patientIdentifierSource, patientIdentifierSources,
                                        patientIdentifierSourceInfoText, onPatientIdentifierSourceChanged}) => {
    return patientIdentifierSources.length > 1 && <tr>
        <EhrIdentifierSourceHeader {...{patientIdentifierSourceInfoText}}/>
        <EhrIdentifierSourceCell {...{patientIdentifierSource, patientIdentifierSources,
            onPatientIdentifierSourceChanged}}/>
    </tr>;
};

const EhrIdentifierHeader = ({patientIdentifierInfoText}) =>
    <th>EHR identifier <i className="fa fa-info-circle" title={patientIdentifierInfoText}/></th>;

const EhrIdentifierCell = ({patientIdentifier, onPatientIdentifierChanged, patientIdentifierValid,
                               availablePatients}) => {
    return <td>
        <PatientIdField value={patientIdentifier}
                        valid={patientIdentifierValid}
                        availablePatients={availablePatients}
                        menuStyle={{maxHeight: "8em"}}
                        onChange={onPatientIdentifierChanged}/>
    </td>
};

const EhrIdentifierRow = ({patientIdentifierInfoText, patientIdentifier, onPatientIdentifierChanged,
                                  patientIdentifierValid, availablePatients}) => {
    return <tr>
        <EhrIdentifierHeader {...{patientIdentifierInfoText}}/>
        <EhrIdentifierCell {...{patientIdentifier, onPatientIdentifierChanged, patientIdentifierValid,
            availablePatients}}/>
    </tr>;
};

const DateOfConsentHeader = ({dateOfConsentInfoText}) =>
    <th>Date of consent <i className="fa fa-info-circle" title={dateOfConsentInfoText}/></th>;

const DateOfConsentCell = ({dateOfConsent, dateOfConsentValid, onDateOfConsentChanged}) => {
    const dateOfConsentClass = `consent-date-field${dateOfConsentValid ? '' : ' form-field-error'}`;
    return <td>
        <div className="calendar-icon">
            <DatePicker popperContainer={CalendarContainer}
                        dateFormat="d LLL yyyy"
                        maxDate={new Date(Date.now())}
                        selected={dateOfConsent}
                        onChange={onDateOfConsentChanged}
                        className={dateOfConsentClass}/>
        </div>
    </td>;
};

const DateOfConsentRow = ({dateOfConsentInfoText, dateOfConsent, dateOfConsentValid, onDateOfConsentChanged}) => {
    return <tr>
        <DateOfConsentHeader {...{dateOfConsentInfoText}}/>
        <DateOfConsentCell {...{dateOfConsent, dateOfConsentValid, onDateOfConsentChanged}}/>
    </tr>;
};

const Errors = ({error, errorMessages, contactAddress}) => {
    if (errorMessages.length === 0 && !error) {
        return null;
    }
    return <ModalErrors validationErrors={errorMessages} requestError={error} contact={contactAddress}/>;
};

const Form = ({onSubjectAdded, ...props}) => {
    const firstNameProps = pick(props, 'firstName', 'firstNameInfoText', 'onFirstNameChanged');
    const lastNameProps = pick(props, 'lastName', 'lastNameInfoText', 'onLastNameChanged');
    const dateOfBirthProps = pick(props, 'dateOfBirth', 'dateOfBirthInfoText', 'onDateOfBirthChanged');
    const edcIdentifierProps = pick(props, 'subjectIdentifier', 'subjectIdentifierValid', 'subjectIdentifierInfoText',
        'availableSubjects', 'onSubjectIdentifierChanged');
    const edcIdentifierSourceProps = pick(props, 'patientIdentifierSource', 'patientIdentifierSources',
        'patientIdentifierSourceInfoText', 'onPatientIdentifierSourceChanged');
    const ehrIdentifierProps = pick(props, 'patientIdentifierInfoText', 'patientIdentifier',
        'onPatientIdentifierChanged', 'patientIdentifierValid', 'availablePatients');
    const dateOfConsentProps = pick(props, 'dateOfConsentInfoText', 'dateOfConsent', 'dateOfConsentValid',
        'onDateOfConsentChanged');
    const onSubmit = (event) => {
        event.preventDefault();
        onSubjectAdded();
    };
    return <form onSubmit={onSubmit}>
        <table>
            <tbody>
                <FirstNameRow {...firstNameProps} />
                <LastNameRow {...lastNameProps} />
                <DateOfBirthRow {...dateOfBirthProps} />
                <EdcIdentifierRow {...edcIdentifierProps} />
                <EhrIdentifierSourceRow {...edcIdentifierSourceProps} />
                <EhrIdentifierRow {...ehrIdentifierProps} />
                <DateOfConsentRow {...dateOfConsentProps} />
            </tbody>
        </table>
        <button type="submit" style={{ display: 'none' }}/>
    </form>;
};

const DisabledState = ({disabledMessage}) =>
    <div title={disabledMessage}>
        <a className="button open-add-subject-form margin-none disabled" data-cy={"register-participant-disabled-btn"}>
            <i className="fa fa-plus"/>Add Participant</a>
    </div>;

const ClosedState = ({onOpen}) =>
    <a className="button open-add-subject-form margin-none" onClick={onOpen} data-cy={"register-participant-closed-btn"}>
        <i className="fa fa-plus"/>Add Participant</a>;

const OpenState = ({inProgress, onClose, onSubjectAdded, error, errorMessages, contactAddress, ...formProps}) =>
    <Modal title="Register participant"
           open={true} inProgress={inProgress}
           onClose={onClose} onOkButtonClick={onSubjectAdded}>
        <Form {...formProps}/>
        <Errors {...{error, errorMessages, contactAddress}}/>
    </Modal>;

export default class AddSubjectPane extends React.Component {
    render() {
        const {disabled, open, disabledMessage, onOpen, ...rest} = this.props;
        if (disabled) {
            return <DisabledState {...{disabledMessage}}/>;
        }
        if (!open) {
            return <ClosedState {...{onOpen}}/>;
        }
        return <OpenState {...rest}/>
    }
};

AddSubjectPane.propTypes = {
    disabled: PropTypes.bool,
    open: PropTypes.bool,

    subjectIdentifier: PropTypes.string,
    availableSubjects: PropTypes.object,
    availablePatientIds: PropTypes.object,
    subjectIdentifierValid: PropTypes.bool,
    patientIdentifierSource: PropTypes.string,
    patientIdentifierSources: PropTypes.arrayOf(PropTypes.string),
    patientIdentifier: PropTypes.string,
    patientIdentifierValid: PropTypes.bool,
    dateOfConsent: PropTypes.instanceOf(Date),
    dateOfConsentValid: PropTypes.bool,

    errorMessages: PropTypes.arrayOf(PropTypes.string),
    error: PropTypes.object,
    contactAddress: PropTypes.string,

    disabledMessage: PropTypes.string,
    subjectIdentifierInfoText: PropTypes.string,
    patientIdentifierSourceInfoText: PropTypes.string,
    patientIdentifierInfoText: PropTypes.string,
    dateOfConsentInfoText: PropTypes.string,

    onOpen: PropTypes.func,
    onClose: PropTypes.func,
    onFirstNameChanged: PropTypes.func,
    onSubjectAdded: PropTypes.func,
    onSubjectIdentifierChanged: PropTypes.func,
    onPatientIdentifierSourceChanged: PropTypes.func,
    onPatientIdentifierChanged: PropTypes.func,
    onDateOfConsentChanged: PropTypes.func
};

AddSubjectPane.defaultProps = {
    patientIdentifierSources: [],
    errorMessages: [],

    firstNameInfoText: "The participant's first name",
    lastNameInfoText: "The participant's last name",
    dateOfBirthInfoText: "The participant's date of birth",
    subjectIdentifierInfoText: "Identifier used to reference the participant in this study (as issued by the IVRS or a similar system)",
    patientIdentifierInfoText: "Identifier used to reference the participant within the healthcare source systems. The patient id is only visible to investigators and is not exported to the linked sponsor's EDC system",
    patientIdentifierSourceInfoText: "Domain in which the patient identifier was issued by the patient identification management system (MPI)",
    dateOfConsentInfoText: "Date at which the patient has given informed consent to enter the study",
};