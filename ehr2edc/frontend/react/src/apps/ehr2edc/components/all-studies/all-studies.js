import React, {useEffect} from "react";
import * as PropTypes from "prop-types";
import {Sidebar} from "../../../../common/components/sidebar/sidebar";
import {
    SearchResultsError,
    SearchResultsLoading
} from "../../../../common/components/search-result/search-result.helper";
import StudiesOverviewTable from "./studies-overview-table";
import RequestError from "../../../../common/components/error/backend-request-error";
import {connect} from "react-redux";
import {fetchStudies} from "../../actions/study.actions";

const BreadCrumbs = () => <ul id="page-bread-crumbs" className="bread-crumbs shrink grid-block border-bottom">
    <li className="is-active">EHR2EDC Studies</li>
</ul>;

const EmptyStudies = ({emptyMessage}) => {
    return <div className="grid-block" data-cy={"all-studies-empty"}>
        <div className="blank-state-button">
            <p className="text-center">{emptyMessage}</p>
        </div>
    </div>;
};

const StudiesError = ({errorMessage, error, contactAddress}) => {
    return <SearchResultsError message={errorMessage} dataCy={"all-studies-error"}>
        <RequestError error={error} contact={contactAddress}/>
    </SearchResultsError>;
};

const StudiesOverview = ({errorMessage, error, contactAddress, studies, emptyMessage}) => {
    const isInError = !!error;
    const isLoading = !isInError && !studies;
    const hasStudies = !isInError && Array.isArray(studies) && studies.length !== 0;
    const hasNoStudies = !isInError && !isLoading && !hasStudies;
    return <div className="ehr2edc-studies grid-block">
        {isLoading && <SearchResultsLoading/>}
        {isInError && <StudiesError error={error} errorMessage={errorMessage} contactAddress={contactAddress}/>}
        {hasStudies && <StudiesOverviewTable data={studies}/>}
        {hasNoStudies && <EmptyStudies emptyMessage={emptyMessage}/>}
    </div>
};

export const AllStudies = ({errorMessage, error, contactAddress, studies, emptyMessage, actions}) => {
    useEffect(() => {
        if (!!actions) {
            actions.fetchStudies();
        }
    }, [actions]);
    return <>
        <Sidebar additionalClasses={["shrink", "sidebar-nav", "border-right", "padding-none"]}/>
        <div className="grid-block vertical">
            <BreadCrumbs/>
            <StudiesOverview errorMessage={errorMessage} error={error} contactAddress={contactAddress}
                             studies={studies} emptyMessage={emptyMessage}/>
        </div>
    </>;
};

AllStudies.defaultProps = {
    emptyMessage: "No EHR2EDC studies are found.",
    errorMessage: "EHR2EDC Studies failed to load."
};

AllStudies.propTypes = {
    emptyMessage: PropTypes.string.isRequired,
    errorMessage: PropTypes.string.isRequired,
    studies: PropTypes.arrayOf(PropTypes.object),
    error: PropTypes.object,
    actions: PropTypes.object
};

export const mapStateToProps = (state) => {
    const {study} = state;
    if (!study || !study.all) {
        return {
            studies: undefined,
            error: undefined
        }
    }
    const {data, error} = study.all;
    return {
        studies: data && data.studies,
        error: error
    };
};

export const mapDispatchToProps = (dispatch) => {
    return {
        actions: {
            fetchStudies: () => {
                dispatch(fetchStudies());
            }
        },
    };
};

export default connect(mapStateToProps, mapDispatchToProps)(AllStudies);
