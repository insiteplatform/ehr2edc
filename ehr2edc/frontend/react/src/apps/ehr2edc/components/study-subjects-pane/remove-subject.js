import React from "react";
import Modal, {ModalErrors} from "../../../../common/components/react-modal/modal";
import DatePicker from "react-datepicker";
import {Portal} from "react-overlays";
import axios from "axios";
import {requestErrorHasField} from "../../../../common/components/error/backend-request-error";

const CalendarContainer = ({children}) => {
    const el = document.getElementById('calendar-portal');
    return (
        <Portal container={el}>
            {children}
        </Portal>
    );
};

export default class RemoveSubject extends React.Component {
    constructor(props) {
        super(props);
        this.state = RemoveSubject.getInitialState();

        this.open = this.open.bind(this);
        this.confirm = this.confirm.bind(this);
        this.close = this.close.bind(this);

        this.updateEndDate = this.updateEndDate.bind(this);
        this.updateSelectedReason = this.updateSelectedReason.bind(this);
    }

    initState() {
        this.setState(RemoveSubject.getInitialState());
    }

    static getInitialState() {
        return {
            open: false,
            studyId: null,
            subjectId: null,
            endDate: new Date(Date.now()),
            selectedReason: 'CONSENT_RETRACTED',
            errorMessages: [],
            error: undefined,
        };
    }

    render() {
        const {open, inProgress } = this.state;
        return (
            <Modal title="Deregister participant"
                   width="450px"
                   open={open} inProgress={inProgress}
                   onOkButtonClick={this.confirm} onClose={this.close}>
                {this.renderContent()}
                {this.renderErrors()}
            </Modal>
        );
    }

    renderContent() {
        return (
            <table>
                <tbody>
                <tr>
                    <th>Date of deregistration <i className="fa fa-info-circle"
                                                  title="Date at which the patient will be removed from the study"/>
                    </th>
                    <td>
                        <div className="calendar-icon">
                            <DatePicker popperContainer={CalendarContainer}
                                        selected={this.state.endDate}
                                        dateFormat="d LLL yyyy"
                                        onChange={this.updateEndDate}
                                        className={`consent-date-field${this.endDateInError() ? ' form-field-error' : ''}`}
                                        maxDate={new Date(Date.now())}
                            />
                        </div>
                    </td>
                </tr>
                <tr>
                    <th>Reason for deregistration <i className="fa fa-info-circle"
                                                     title="The reason for removal from the study"/>
                    </th>
                    <td>
                        <select onClick={e => this.updateSelectedReason(e.target.value)}
                                className={this.reasonInError() ? 'form-field-error' : ''}
                        >
                            <option value="CONSENT_RETRACTED">Consent Retracted</option>
                        </select>
                    </td>
                </tr>
                </tbody>
            </table>
        );
    }

    endDateInError() {
        const endDateField = "endDate";
        return !this.isValid() &&
            (this.state.errorMessages.some(error => error === "An end date is required") ||
                requestErrorHasField(this.state.error, endDateField));
    }

    reasonInError() {
        const reasonField = "dataCaptureStopReason";
        return !this.isValid() &&
            (this.state.errorMessages.some(error => error === "A reason for deregistration is required") ||
                requestErrorHasField(this.state.error, reasonField));
    }

    renderErrors() {
        if (this.state.errorMessages.length !== 0 || !!this.state.error) {
            return <ModalErrors validationErrors={this.state.errorMessages}
                                requestError={this.state.error}
                                contact={this.props.contactAddress}/>
        }
        return null;
    }

    isValid() {
        return (!this.state.errorMessages || this.state.errorMessages.length === 0) && !this.state.error;
    }

    open(studyId, subjectId) {
        this.setState({
            open: true,
            studyId: studyId,
            subjectId: subjectId,
        });
    }

    async confirm() {
        this.setInProgress(true);
        let errorList = this.getErrorList();
        this.setState({
            errorMessages: errorList
        });
        if (errorList.length === 0) {
            await this.deregisterPatient();
        }
        this.setInProgress(false);
    }

    close() {
        this.initState();
    }

    updateEndDate(date) {
        this.setState({
            endDate: date
        });
    }

    updateSelectedReason(value) {
        this.setState({
            selectedReason: value
        });
    }

    async deregisterPatient() {
        try {
            await axios.delete(this.deregistrationUrl(), { data: this.deregistrationPayload() });
            this.onSuccess();
        } catch (error) {
            this.onError(error)
        }
    }

    deregistrationUrl() {
        const {studyId, subjectId} = this.state;
        return `/ehr2edc/studies/${studyId}/subjects/${subjectId}`
    }

    deregistrationPayload() {
        return {
            studyId: this.state.studyId,
            subjectId: this.state.subjectId,
            endDate: this.state.endDate,
            dataCaptureStopReason: this.state.selectedReason,
        };
    }

    onSuccess() {
        this.initState();
        if (this.props.onPatientDeregistered) {
            this.props.onPatientDeregistered()
        }
        this.close();
    }

    onError(error) {
        this.setState({
            error: error
        });
    }

    getErrorList() {
        let errorList = [];
        if (!this.state.endDate) {
            errorList.push("An end date is required");
        }
        if (!this.state.selectedReason) {
            errorList.push("A reason for deregistration is required");
        }
        return errorList;
    }

    setInProgress(inProgress) {
        this.setState({
            inProgress: inProgress
        })
    }
}