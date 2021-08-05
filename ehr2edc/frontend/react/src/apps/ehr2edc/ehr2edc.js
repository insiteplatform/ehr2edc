import React from "react"
import axios from "axios";
import * as PropTypes from "prop-types";
import {BrowserRouter, Route, Switch} from "react-router-dom";
import AllStudies from "./components/all-studies/all-studies";
import StudyDetails from "./components/study-detail/study-details";

export default class EHR2EDC extends React.Component {

    constructor(props) {
        super(props);

        axios.defaults.baseURL = props.baseUrl ? props.baseUrl : "/";
    }

    render() {
        return <BrowserRouter basename={this.basename()}>
            <Switch>
                <Route path="/:studyId"
                       render={routeProps => <StudyDetails {...routeProps.match.params} {...this.props} />} />
                <Route path="/"
                       render={() => <AllStudies {...this.props} />}/>
            </Switch>
        </BrowserRouter>;
    }

    basename() {
        return `${this.props.studyPageUrl}${this.props.studyPageUrl.endsWith("/") ? "" : "/"}`
    }
}

EHR2EDC.defaultProps = {
    studyPageUrl: "/"
};

EHR2EDC.propTypes = {
    studyPageUrl: PropTypes.string
};