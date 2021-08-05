import React from "react"
import moment from "moment";
import {requestErrorHasField} from "../../../../common/components/error/backend-request-error";
import {listAvailablePatientFor} from "../../../../api/api-ext";
import AddSubjectPane from "./add-subject-pane";

export default class AddSubject extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            open: false,
            firstName: "",
            lastName: "",
            dateOfBirth: new Date(Date.now()),
            dateOfConsent: new Date(Date.now()),
            subjectIdentifier: "",
            patientIdentifier: "",
            patientIdentifierSource: props.patientIdentifierSources[0],
            errorMessages: [],
            error: props.fetchAvailableSubjectsError
        };

        this.openSubjectDialog = this.openSubjectDialog.bind(this);
        this.closeSubjectDialog = this.closeSubjectDialog.bind(this);
        this.updateDateOfConsent = this.updateDateOfConsent.bind(this);
        this.submitSubjectDialog = this.submitSubjectDialog.bind(this);
        this.updateFirstName = this.updateFirstName.bind(this);
        this.updateLastName = this.updateLastName.bind(this);
        this.updateDateOfBirth = this.updateDateOfBirth.bind(this);
        this.updateSubjectIdentifier = this.updateSubjectIdentifier.bind(this);
        this.updatePatientIdentifier = this.updatePatientIdentifier.bind(this);
        this.updatePatientIdentifierSource = this.updatePatientIdentifierSource.bind(this);
    }

    componentDidMount() {
        if (!!this.props.studyId) {
            this.getAvailablePatientIds();
        }
    }

    componentDidUpdate(prevProps, prevState, snapshot) {
        this.updateAvailablePatientIds(prevState);
    }

    updateAvailablePatientIds(prevState) {
        if (!!this.state.patientIdentifier && this.state.patientIdentifier !== prevState.patientIdentifier) {
            this.getAvailablePatientIds();
        } else if (!!this.state.patientIdentifierSource && this.state.patientIdentifierSource !== prevState.patientIdentifierSource) {
            this.getAvailablePatientIds();
        } else if (!this.state.patientIdentifier && !!prevState.patientIdentifier && !!this.state.availablePatients) {
            this.setAvailablePatientIds(undefined);
        }
    }

    render() {
        return <AddSubjectPane
            {...this.firstNameProps()}
            {...this.lastNameProps()}
            {...this.dateOfBirthProps()}
            {...this.subjectIdentifierProps()}
            {...this.patientIdentifierProps()}
            {...this.dateOfConsentProps()}
            {...this.errorProps()}
            {...this.actions()}
            {...this.modalState()}
        />;
    }

    firstNameProps() {
        const {firstName} = this.state;
        return {
            firstName
        }
    }

    lastNameProps() {
        const {lastName} = this.state;
        return {
            lastName
        }
    }

    dateOfBirthProps() {
        const {dateOfBirth} = this.state;
        return {
            dateOfBirth
        }
    }

    subjectIdentifierProps() {
        const {subjectIdentifier} = this.state;
        const {availableSubjects} = this.props;
        return {
            subjectIdentifier, availableSubjects,
            subjectIdentifierValid: !this.getSubjectIdentifierInError()
        }
    }

    patientIdentifierProps() {
        const {patientIdentifierSource, patientIdentifier, availablePatients} = this.state;
        const {patientIdentifierSources} = this.props;
        return {
            patientIdentifierSource, patientIdentifierSources, patientIdentifier, availablePatients,
            patientIdentifierValid: !this.getPatientIdentifierInError()
        }
    }

    dateOfConsentProps() {
        const {dateOfConsent} = this.state;
        return {
            dateOfConsent,
            dateOfConsentValid: !this.getDateOfConsentInError()
        }
    }

    errorProps() {
        const {error, errorMessages} = this.state;
        const {contactAddress} = this.props;
        return {error, errorMessages, contactAddress}
    }

    actions() {
        return {
            onOpen: this.openSubjectDialog,
            onClose: this.closeSubjectDialog,
            onFirstNameChanged: this.updateFirstName,
            onLastNameChanged: this.updateLastName,
            onDateOfBirthChanged: this.updateDateOfBirth,
            onSubjectAdded: this.submitSubjectDialog,
            onSubjectIdentifierChanged: this.updateSubjectIdentifier,
            onPatientIdentifierSourceChanged: this.updatePatientIdentifierSource,
            onPatientIdentifierChanged: this.updatePatientIdentifier,
            onDateOfConsentChanged: this.updateDateOfConsent
        }
    }

    modalState() {
        const {open, inProgress} = this.state;
        const {enabled, fetchAvailableSubjectsLoading} = this.props;
        const disabled = !enabled || fetchAvailableSubjectsLoading || this.isEmptyEDCResponse();
        const disabledMessage = this.disabledMessage();
        return {open, inProgress, disabled, disabledMessage};
    }

    disabledMessage() {
        const {enabled} = this.props;
        const {fetchAvailableSubjectsLoading} = this.props;
        let disabledMessage = undefined;
        if (!enabled) {
            disabledMessage = "You must be an assigned investigator in order to add participants to this study.";
        } else if (fetchAvailableSubjectsLoading) {
            disabledMessage = "Retrieving available identifiers from the linked EDC..."
        } else if (this.isEmptyEDCResponse()) {
            disabledMessage = "The linked EDC did not return any available identifiers."
        }
        return disabledMessage;
    }

    getSubjectIdentifierInError() {
        const subjectIdentifierField = "subjectId.id";
        return this.hasErrors() &&
            (this.state.subjectIdentifier === "" ||
                requestErrorHasField(this.state.error, subjectIdentifierField));
    }

    getPatientIdentifierInError() {
        const patientIdentifierField = "patientId.id";
        return this.hasErrors() &&
            (this.state.patientIdentifier === "" ||
                requestErrorHasField(this.state.error, patientIdentifierField));
    }

    getDateOfConsentInError() {
        const dateOfConsentField = "dateOfConsent";
        return this.hasErrors() &&
            (!this.state.dateOfConsent || !(this.state.dateOfConsent instanceof Date) ||
                requestErrorHasField(this.state.error, dateOfConsentField));
    }

    hasErrors() {
        return this.state.errorMessages.length !== 0 || !!this.state.error;
    }

    openSubjectDialog() {
        this.setState({
            open: true
        });
    }

    closeSubjectDialog() {
        this.setState({
            open: false,
            dateOfConsent: new Date(Date.now()),
            subjectIdentifier: "",
            patientIdentifier: "",
            errorMessages: [],
            error: undefined
        });
    }

    updateFirstName(firstName) {
        this.setState({
            firstName: firstName
        });
    }

    updateLastName(lastName) {
        this.setState({
            lastName: lastName
        });
    }

    updateDateOfBirth(dateOfBirth) {
        this.setState({
            dateOfBirth: dateOfBirth
        });
    }

    updateSubjectIdentifier(subjectIdentifier) {
        this.setState({
            subjectIdentifier: subjectIdentifier
        });
    }

    updatePatientIdentifierSource(patientIdentifierSource) {
        this.setState({
            patientIdentifierSource: patientIdentifierSource
        });
    }

    updatePatientIdentifier(patientIdentifier) {
        this.setState({
            patientIdentifier: patientIdentifier
        });
    }

    updateDateOfConsent(date) {
        this.setState({
            dateOfConsent: date
        });
    }

    submitSubjectDialog() {
        this.clearErrors();
        let errorList = this.getErrorList();
        this.setState({
            errorMessages: errorList
        });
        if (errorList.length === 0) {
            this.saveSubject();
        }
    }

    clearErrors() {
        this.setState({
            errorMessages: [],
            error: undefined
        })
    }

    async saveSubject() {
        this.subjectRegistrationInProgress(true);
        try {
            await this.props.patientRegistrationApi.put(this.createPatientRegistrationRequest());
            this.handlePatientRegistrationSuccess();
        } catch (error) {
            this.handleError(error)
        }
        this.subjectRegistrationInProgress(false);
    }

    createPatientRegistrationRequest() {
        return {
            patientId: {
                source: this.state.patientIdentifierSource,
                id: this.state.patientIdentifier
            },
            studyId: this.props.studyId,
            edcSubjectReference: this.state.subjectIdentifier,
            dateOfConsent: moment(this.state.dateOfConsent).format("YYYY-MM-DD"),
            firstName: this.state.firstName,
            lastName: this.state.lastName,
            birthDate: moment(this.state.dateOfBirth).format("YYYY-MM-DD"),
        };
    }

    handlePatientRegistrationSuccess() {
        this.setState({
            open: false,
            dateOfConsent: new Date(Date.now()),
            subjectIdentifier: "",
            patientIdentifier: "",
            errorMessages: [],
            error: undefined
        });
        this.props.onPatientRegistered();
        this.getAvailablePatientIds();
    }

    handleError(error) {
        this.setState({
            error: error
        });
    }

    getErrorList() {
        let errorList = [];
        if (this.state.patientIdentifier === "") {
            errorList.push("Patient identifier is required");
        }
        if (this.state.subjectIdentifier === "") {
            errorList.push("Subject identifier is required");
        }
        if (!this.state.dateOfConsent || !(this.state.dateOfConsent instanceof Date)) {
            errorList.push("Date of consent is required");
        }
        return errorList;
    }

    getAvailablePatientIds() {
        const {patientIdentifierSource, patientIdentifier} = this.state;
        const {studyId} = this.props;
        if (typeof studyId === "string" &&
            typeof patientIdentifierSource === "string" && patientIdentifierSource.length > 0) {
            this.callAvailablePatientsAPI(studyId, patientIdentifierSource, patientIdentifier);
        }
    }

    callAvailablePatientsAPI(studyId, patientIdentifierSource, patientIdentifier) {
        listAvailablePatientFor(studyId, patientIdentifierSource, patientIdentifier)
            .then((response) => this.setAvailablePatientIds(response.data),
                (error) => this.handleError(error))
    }

    setAvailablePatientIds(availablePatients) {
        this.setState({availablePatients: availablePatients})
    }

    isEmptyEDCResponse() {
        const availableSubjects = this.props.availableSubjects;
        return !!availableSubjects
            && availableSubjects.fromEDC
            && this.isEmptyArrayOrUndefined(availableSubjects.subjectIds)
    }

    isEmptyArrayOrUndefined(arrayOrUndefined) {
        return !arrayOrUndefined || this.isEmptyArray(arrayOrUndefined);
    }

    isEmptyArray(array) {
        return Array.isArray(array) && array.length === 0;
    }

    subjectRegistrationInProgress(inProgress) {
        this.setState({
            inProgress: inProgress
        })
    }
}

AddSubject.defaultProps = {
    enabled: false,
    patientIdentifierSources: [""],
    onPatientRegistered: () => {
    }
};