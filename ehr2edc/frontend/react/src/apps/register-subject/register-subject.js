import React, {Fragment} from "react";
import ListStudies from "./list-studies";
import Notification from "../../common/components/notification";
import RequestError from "../../common/components/error/backend-request-error";
import axios from "axios";

export default class RegisterSubject extends React.Component {

    constructor(props) {
        super(props);

        axios.defaults.baseURL = props.baseUrl ? props.baseUrl : "/";

        this.state = RegisterSubject.getInitialState(props);
        this.notifications = React.createRef();

        this.registerPatientForStudy = this.registerPatientForStudy.bind(this)
    }

    static getDerivedStateFromProps(nextProps, prevState) {
        if (nextProps.patientId !== prevState.patientId) {
            return RegisterSubject.getInitialState(nextProps);
        }
        return null
    }

    componentDidMount() {
        this.fetchStudies();
    }

    componentDidUpdate() {
        if (this.state.isLoading) {
            this.fetchStudies();
        }
    }

    render() {
        const {registeredStudies,
            isLoading,
            availableStudies,
            inProgress} = this.state;
        return (
            <Fragment>
                <div className="grid-block vertical shrink margin-medium-top padding-none">
                    <div className="grid-block shrink border-bottom">
                        <h6 className="padding-rl padding-small-bottom margin-small-tb">
                            Registered EHR2EDC studies
                        </h6>
                    </div>
                    <div className="grid-block vertical shrink margin-rl">
                        <ListStudies
                            id="registeredStudies"
                            studies={registeredStudies}
                            isLoading={isLoading}
                            noDataMessage={"There are no registered studies for the patient"}
                        >
                        </ListStudies>
                    </div>
                </div>
                <div className="grid-block vertical shrink margin-medium-top padding-none">
                    <div className="grid-block shrink border-bottom">
                        <h6 className="padding-rl padding-small-bottom margin-small-tb">
                            Assign patient to EHR2EDC study <i className="fa fa-info-circle"
                                                               title="Click the study that you want to assign the selected patient to"/>
                        </h6>
                    </div>
                    <div className="grid-block vertical margin-bottom padding-rl">
                        <ListStudies
                            id="availableStudies"
                            studies={availableStudies}
                            isLoading={isLoading}
                            noDataMessage={"There are no available studies for the patient"}
                            editable={true}
                            inProgress={inProgress}
                            onSubjectRegistration={this.registerPatientForStudy}
                        >
                        </ListStudies>
                    </div>
                </div>
                <Notification ref={this.notifications}/>
            </Fragment>
        );
    }

    initState() {
        this.setState(RegisterSubject.getInitialState(this.props));
    }

    static getInitialState(props) {
        const state = {
            patientId: props.patientId,
            isLoading: true,
            inError: false,
            availableStudies: [],
            registeredStudies: [],
        };
        return state;
    }

    fetchStudies() {
        this.props.patientStudyApi.getAll()
            .then(
                (response) => {
                    this.setState({
                        isLoading: false,
                        inError: false,
                        availableStudies: response.data.availableStudies,
                        registeredStudies: response.data.registeredStudies,
                    });
                },
                () => {
                    this.setState({
                        isLoading: false,
                        inError: true,
                        availableStudies: [],
                        registeredStudies: [],
                    });
                }
            )
    }

    registerPatientForStudy(studyId, edcSubjectReference, dateOfConsent) {
        this.setRegistrationInProgress(true);
        this.props.patientRegistrationApi.put(
            studyId,
            this.buildPayload(studyId, edcSubjectReference, dateOfConsent)
        ).then(
            () => {
                this.registrationSucceeded(edcSubjectReference, studyId);
            },
            (response) => {
                this.registrationFailed(edcSubjectReference, studyId, response);
            }
        ).finally(() => {
            this.setRegistrationInProgress(false)
        })
    }

    setRegistrationInProgress(inProgress) {
        this.setState({
            inProgress: inProgress
        })
    }

    buildPayload(studyId, edcSubjectReference, dateOfConsent) {
        return {
            patientId: {
                source: this.props.patientSource,
                id: this.props.patientId
            },
            studyId: studyId,
            edcSubjectReference: edcSubjectReference,
            dateOfConsent: dateOfConsent
        };
    }

    registrationSucceeded(edcSubjectReference, studyId) {
        this.notifications.current.show(`Successfully added Patient (${this.state.patientId}) as Subject (${edcSubjectReference}) to the Study (${studyId})`);
        this.initState();
        this.onClickClose();
    }

    registrationFailed(edcSubjectReference, studyId, response) {
        this.notifications.current.show(
            (<Fragment>
                <p>Problem adding Patient ({this.state.patientId}) as Subject ({edcSubjectReference}) to the
                    Study ({studyId})</p>
                <RequestError error={response} contact={this.props.contactAddress}/>
            </Fragment>), "warning");
        this.setState({
            inError: true,
        });
    }

    onClickClose() {
        if (this.props && this.props.onClose) {
            this.props.onClose.call();
        }
    }
}