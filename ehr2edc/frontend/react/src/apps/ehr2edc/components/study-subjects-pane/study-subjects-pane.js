import React from "react";
import ReactTable from "react-table";
import ReactTablePager from "../../../../common/components/react-table/react-table-pager";
import {Link} from "react-router-dom";
import AddSubject from "./add-subject";
import RemoveSubject from "./remove-subject";
import ExpandableText from "../../../../common/components/expandable-text/expandable-text";
import {listAvailableSubjectFor} from "../../../../api/api-ext";
import moment from 'moment';
import {ErrorMessage} from "../../../../common/components/error/error-message";
import * as PropTypes from "prop-types";

const subjectColumn = {
    Header: "Participant",
    sortable: false,
    filterable: false,
    show: true,
    className: "small-5",
    headerClassName: "small-5",
    Cell: row =>
        <Link to={`subjects/${row.original.subjectId}/events`}>
            {row.original.edcSubjectReference}
        </Link>
};

const patientIdentifierColumn = {
    Header: "Patient identifier",
    sortable: false,
    filterable: false,
    show: true,
    className: "small-4",
    headerClassName: "small-4",
    Cell: ({original: {patientId}}) =>
        <><span className="label info margin-small-right">{patientId.source}</span>{patientId.id}</>
};

const dateOfConsentColumn = {
    Header: "Date of Consent",
    sortable: false,
    filterable: false,
    show: true,
    className: "small-2",
    headerClassName: "small-2",
    Cell: row => moment(row.original.consentDateTime).format("ll")
};

function createActionsColumn(removeSubjectAction) {
    return {
        Header: "Actions",
        sortable: false,
        filterable: false,
        headerClassName: 'small-1 text-right',
        className: 'small-1 text-right',
        style: {
            "flex": "none",
            "width": "5%"
        },
        Cell: row =>
            <a className="remove-icon"
               onClick={() => removeSubjectAction(row.original.subjectId)}
               title={'Remove subject ' + row.original.edcSubjectReference}>
                <i className="fa fa-minus-square-o"/>
            </a>
    };
}

function StudySubjectsTable({subjects, removeSubjectAction}) {
    const actionsColumn = createActionsColumn(removeSubjectAction);
    const columns = [subjectColumn, patientIdentifierColumn, dateOfConsentColumn, actionsColumn];
    return <ReactTable data={subjects}
                       columns={columns}
                       defaultPageSize={10}
                       pageSizeOptions={[10, 50, 100]}
                       PaginationComponent={ReactTablePager} resizable={false} minRows={0}/>;
}

function StudySubjectsMissing() {
    return (<p className="padding-medium-rl">No subjects present</p>);
}

function StudySubjects({canSubjectsBeViewed, subjects, removeSubjectAction}) {
    const subjectsMissing = canSubjectsBeViewed && (!subjects || subjects.length === 0);
    return <>
        {!canSubjectsBeViewed && <ErrorMessage message="User is not an assigned Investigator"/>}
        {subjectsMissing && <StudySubjectsMissing/>}
        {!subjectsMissing && <StudySubjectsTable subjects={subjects} removeSubjectAction={removeSubjectAction}/>}
    </>;
}

function StudySubjectsPaneText() {
    return (
        <div className="margin-rl padding-bottom">
            <div className="alert-box small info margin-small-rl padding">
                <ExpandableText>
                    <p>Here you can see all subjects registered in this study. If you are an investigator, you
                        can register new subjects to this study by their subject identifier. This can be done
                        either from this page (Add subject) in which case you'll have to specify the subject's
                        patient identifier or this can be done from a cohort listing within the <a
                            href="/app/cohort/studies">cohort analysis module</a> by selecting the patient and
                        then selecting this study from the list of available EHR2EDC studies in the patient
                        registration details for the selected patient. You can also deregister individual
                        subjects from this list
                    </p>
                </ExpandableText>
            </div>
        </div>
    );
}

function StudySubjectsPaneHeader() {
    return <div className="grid-content collapse">
        <h3 className="subheader">Participants</h3>
    </div>;
}

export default class StudySubjectsPane extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            availableSubjects: {},
            fetchAvailableSubjectsLoading: false,
            fetchAvailableSubjectsError: undefined
        };
        this.removeSubjectRef = React.createRef();
        this.refresh = this.refresh.bind(this);
        this.removeSubject = this.removeSubject.bind(this);
    }

    componentDidMount() {
        if (!!this.props.studyId) {
            this.fetchAvailableSubjects();
        }
    }

    componentDidUpdate(prevProps, prevState, snapshot) {
        this.updateAvailableSubjects(prevProps);
    }

    render() {
        const {
            patientRegistrationApi, studyId, contactAddress, canSubjectsBeViewed, canSubjectsBeAdded,
            patientIdentifierSources, subjects
        } = this.props;
        const {availableSubjects} = this.state;
        return (
            <>
                <div className="grid-block padding margin-small-rl shrink">
                    <StudySubjectsPaneHeader/>
                    <AddSubject patientRegistrationApi={patientRegistrationApi}
                                studyId={studyId}
                                contactAddress={contactAddress}
                                onPatientRegistered={this.refresh}
                                enabled={canSubjectsBeAdded}
                                patientIdentifierSources={patientIdentifierSources}
                                availableSubjects={availableSubjects}
                                fetchAvailableSubjectsLoading={this.state.fetchAvailableSubjectsLoading}
                                fetchAvailableSubjectsError={this.state.fetchAvailableSubjectsError}/>
                    <RemoveSubject ref={this.removeSubjectRef}
                                   contactAddress={contactAddress}
                                   onPatientDeregistered={this.refresh}/>
                </div>
                <StudySubjectsPaneText/>
                <StudySubjects canSubjectsBeViewed={canSubjectsBeViewed}
                               subjects={subjects}
                               removeSubjectAction={this.removeSubject}/>
            </>);
    }

    removeSubject(subjectId) {
        this.removeSubjectRef.current.open(this.props.studyId, subjectId);
    }

    refresh() {
        this.fetchAvailableSubjects();
        this.props.onRefresh();
    }

    updateAvailableSubjects({studyId: prevStudyId}) {
        const {studyId} = this.props;
        if (!!studyId && studyId !== prevStudyId) {
            this.fetchAvailableSubjects();
        } else if (!studyId && !!prevStudyId && !!this.state.availableSubjects) {
            this.setAvailableSubjects({});
        }
    }

    async fetchAvailableSubjects() {
        this.setState({fetchAvailableSubjectsLoading: true});
        try {
            const response = await listAvailableSubjectFor(this.props.studyId);
            this.setAvailableSubjects(response);
        } catch (error) {
            this.setState({fetchAvailableSubjectsError: error})
        }
        this.setState({fetchAvailableSubjectsLoading: false});
    }

    setAvailableSubjects({data: availableSubjects}) {
        this.setState({availableSubjects: availableSubjects});
    }
}

StudySubjectsPane.defaultProps = {
    canSubjectsBeViewed: false,
    canSubjectsBeAdded: false,
};

StudySubjectsPane.propTypes = {
    patientRegistrationApi: PropTypes.object,
    studyId: PropTypes.string,
    contactAddress: PropTypes.string,
    canSubjectsBeViewed: PropTypes.bool,
    canSubjectsBeAdded: PropTypes.bool,
    patientIdentifierSources: PropTypes.arrayOf(PropTypes.string),
    subjects: PropTypes.arrayOf(PropTypes.object),
};
