import React from "react";
import ReactTable from "react-table";
import Moment from 'moment';
import DatePicker from "react-datepicker";
import {Portal} from "react-overlays";
import SubjectIdField from "../../common/components/forms/subject-id/subject-id";
import {listAvailableSubjectFor} from "../../api/api-ext";
import {ENTER_KEY, ESCAPE_KEY} from "../../common/components/forms/form-constants";

const CalendarContainer = ({children}) => {
    const el = document.getElementById('calendar-portal');
    return <Portal container={el}>{children}</Portal>;
};

export default class ListStudies extends React.Component {

    constructor(props) {
        super(props);
        this.state = ListStudies.getInitialState();

        this.handleChangeSubjectId = this.handleChangeSubjectId.bind(this);
        this.handleChangeDateOfConsent = this.handleChangeDateOfConsent.bind(this);

        this.onRegisterPatientSubmit = this.onRegisterPatientSubmit.bind(this);
        this.handleKeyPress = this.handleKeyPress.bind(this);
    }

    componentDidMount() {
        document.addEventListener('keydown', this.handleKeyPress);
    }

    componentWillUnmount() {
        document.removeEventListener('keydown', this.handleKeyPress);
    }

    handleKeyPress(e) {
        const {expanded} = this.state;
        if (e.keyCode === ESCAPE_KEY && expanded) {
            this.setState({
                selectedStudyIndex: undefined,
                selectedStudy: undefined,
                expanded: false
            })
        }
        if (e.keyCode === ENTER_KEY && expanded) {
            this.onRegisterPatientSubmit(e);
        }
    }

    static getDerivedStateFromProps(nextProps, prevState) {
        if (nextProps.isLoading) {
            return ListStudies.getInitialState();
        }
        return null;
    }

    initState() {
        this.setState(ListStudies.getInitialState());
    }

    static getInitialState() {
        const now = new Date(Date.now());
        const state = {
            selectedStudy: null,
            selectedStudyIndex: null,
            subjectId: "",
            dateOfConsent: new Date(now.getFullYear(), now.getMonth(), now.getDate()),
            expanded: {},
        };
        return state;
    }

    render() {
        switch (true) {
            case this.props.isLoading:
                return this.renderStudiesLoading();
            case !this.props.studies || this.props.studies.length === 0 :
                return this.renderNoData(this.props.noDataMessage);
            case this.props.editable:
                return this.renderAvailableStudiesColumns();
            default:
                return this.renderRegisteredStudiesColumns();
        }
    }

    renderRegisteredStudiesColumns() {
        const columns = [
            {
                Header: 'Name',
                accessor: 'name'
            },
            {
                Header: 'Description',
                accessor: 'description'
            },
            {
                Header: 'Subject ID',
                accessor: 'subject.edcSubjectReference'
            },
            {
                id: "dateOfConsent",
                Header: 'Date Of Consent',
                accessor: item => {
                    if (item && item.subject && item.subject.dateOfConsent) {
                        return Moment(item.subject.dateOfConsent)
                            .local()
                            .format('LL')
                    }
                }
            }
        ];
        return <ReactTable columns={columns}
                           data={this.props.studies}
                           minRows={0}
                           showPagination={false}
                           loading={this.props.isLoading}
                           className="shrink"
        />;
    }

    renderAvailableStudiesColumns() {
        const {studies, isLoading} = this.props;
        const {expanded} = this.state;
        return <ReactTable columns={this.columns()}
                           data={studies}
                           minRows={0}
                           showPagination={false}
                           loading={isLoading}
                           className="shrink"
                           getTrProps={this.getTrProps()}
                           expanded={expanded}
                           SubComponent={(row) => {
                               const positionFromEnd = studies.length - row.index;
                               return this.renderRegisterPatientToStudyForm(positionFromEnd);
                           }}
        />;
    }

    columns() {
        return [
            {
                Header: 'Name',
                accessor: 'name'
            },
            {
                Header: 'Description',
                accessor: 'description',
            }
        ];
    }

    renderStudiesLoading() {
        return (
            <div className="grid-block search-results-loading">
                <span className="search-results-loading-icon padding-large">
                    <i className="fa fa-circle-o-notch fa-spin"></i>
                </span>
            </div>
        );
    }

    renderNoData(message) {
        return (
            <div className="padding">
                {message}
            </div>
        );
    }

    renderRegisterPatientToStudyForm(positionFromEnd) {
        return (
            <div className="grid-block vertical register-patient-form">
                {this.registerFormWarning()}
                <form className="grid-block" onSubmit={this.onRegisterPatientSubmit}>
                    {this.subjectIdField(positionFromEnd, positionFromEnd)}
                    {this.dateOfConsentField()}
                    {this.registerButton()}
                </form>
            </div>
        );
    }

    registerFormWarning() {
        return this.isEmptyEDCResponse()
            ? <div className="alert-box alert warning margin-trl">
                <p>The linked EDC did not return any available subject IDs.</p>
            </div>
            : null;
    }

    subjectIdField(positionFromEnd) {
        const {fetchingSubjectIds, availableSubjects, subjectId} = this.state;
        return <div className="grid-block padding">
            <label htmlFor="inputSubjectId">
                Subject ID <i className="fa fa-info-circle"
                              title="Identifier used to reference to the patient as a subject in this study (as issued by the IVRS or a similar system)"/>
                <div>
                    <SubjectIdField
                        disabled={fetchingSubjectIds || !this.formEnabled()}
                        availableSubjects={availableSubjects}
                        value={subjectId}
                        menuStyle={{maxHeight: `${Math.min(3, positionFromEnd) * 4}em`}}
                        onChange={this.handleChangeSubjectId}
                        openOnFocus={true}/>
                </div>
            </label>
        </div>;
    }

    dateOfConsentField() {
        return <div className="grid-block padding">
            <label htmlFor="inputDateOfConsent">
                Date of Consent <i className="fa fa-info-circle"
                                   title="Date at which the patient has given informed consent to enter the study"/>
                <div className="calendar-icon">
                    <DatePicker
                        disabled={!this.formEnabled()}
                        popperContainer={CalendarContainer}
                        id="inputDateOfConsent"
                        dateFormat="d LLL yyyy"
                        maxDate={new Date(Date.now())}
                        selected={this.state.dateOfConsent}
                        onChange={this.handleChangeDateOfConsent}
                    />
                </div>
            </label>
        </div>;
    }

    registerButton() {
        return <div className="grid-block margin padding">
            <div>
                <a className={this.registerButtonClass()}
                   onClick={this.onRegisterPatientSubmit}>
                    <i className="fa fa-fw fa-plus"/>
                    <div className={"bar"}/>
                </a>
            </div>
        </div>
    }

    formEnabled() {
        return !(this.isEmptyEDCResponse() || this.state.fetchingSubjectIds);
    }

    registerButtonClass() {
        const classes = ["button", "icon"];
        if (!this.canSubmitForm()) {
            classes.push("disabled")
        }
        if (this.props.inProgress) {
            classes.push("in-progress")
        }
        return classes.join(" ");
    }

    canSubmitForm() {
        const {selectedStudy, subjectId, dateOfConsent} = this.state;
        const mandatoryInput = selectedStudy && subjectId && dateOfConsent;
        return mandatoryInput && this.formEnabled();
    }

    getTrProps() {
        return (state, rowInfo) => {
            return {
                onClick: () => {
                    this.selectStudy(rowInfo);
                    this.expandRow(rowInfo);
                },
                className: this.state.selectedStudyIndex === rowInfo.index ? "selected" : ""
            };
        };
    }

    selectStudy(rowInfo) {
        let index = null;
        let clickedStudy = null;
        if (this.state.selectedStudyIndex !== rowInfo.index) {
            index = rowInfo.index;
            clickedStudy = this.props.studies[rowInfo.index];
            this.retrieveAvailableSubjectIds(clickedStudy);
        }
        this.setState({
            selectedStudyIndex: index,
            selectedStudy: clickedStudy,
        });
    }

    expandRow(rowInfo) {
        let singleExpand = [];
        singleExpand[rowInfo.viewIndex] = !this.state.expanded[rowInfo.viewIndex];
        this.setState({
            expanded: singleExpand
        });
    }

    handleChangeSubjectId(subjectId) {
        this.setState({subjectId: subjectId});
    }

    handleChangeDateOfConsent(date) {
        this.setState({dateOfConsent: date});
    }

    onRegisterPatientSubmit(event) {
        event.preventDefault();
        if (this.canSubmitForm()) {
            this.props.onSubjectRegistration(this.state.selectedStudy.studyId, this.state.subjectId, Moment(this.state.dateOfConsent).format("YYYY-MM-DD"))
        }
    }

    retrieveAvailableSubjectIds(study) {
        listAvailableSubjectFor(study.studyId).then(
            (response) => this.setAvailableSubjects(response.data),
            () => {
            });
    }

    setAvailableSubjects(availableSubjects) {
        this.setState({availableSubjects: availableSubjects});
    }

    isEmptyEDCResponse() {
        const availableSubjects = this.state.availableSubjects;
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
}