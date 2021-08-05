import React, {Component} from "react";
import * as PropTypes from "prop-types";
import moment from "moment";
import Form from "../study-forms/study-form";

const SubmissionMessage = ({reviewTime, reviewer}) => {
    return <div className="alert-box info padding margin margin-top-none">
        <i className="fa fa-info-circle"/> This event was submitted
        on {moment(reviewTime).format("ll [at] HH:mm")} by {reviewer}
    </div>;
};

const PopulationMessage = ({populationTime, referenceDate, populator}) => {
    const populatorText = populator === null ? "" : ` by ${populator}`;
    return <div className="alert-box info margin-rl padding">
        <i className="fa fa-info-circle"/> This event was populated
        on {moment(populationTime).format("ll [at] HH:mm")}{populatorText},
        with reference date {moment(referenceDate).format("ll")}
    </div>;
};

const FormOverview = ({reviewedForms, subjectId, reviewedEventId}) => {
    return <div className="study-form-container">
        {reviewedForms.map(form => <Form key={form.id} {...form}
                                         subjectId={subjectId}
                                         eventId={reviewedEventId}
                                         inReview={false}/>)}
    </div>;
};

export class ReviewedEventDetails extends Component {
    render() {
        const {reviewTime, reviewer, populationTime, referenceDate, populator, reviewedForms, subjectId, reviewedEventId} = this.props;
        return <div className={"grid-block vertical" + this.getWidth()}>
            <div className="padding-top">
                <SubmissionMessage reviewTime={reviewTime} reviewer={reviewer}/>
                <PopulationMessage populationTime={populationTime} referenceDate={referenceDate} populator={populator}/>
            </div>
            <FormOverview reviewedForms={reviewedForms} subjectId={subjectId} reviewedEventId={reviewedEventId} />
        </div>;
    }

    getWidth() {
        return this.props.visible ? " medium-8 large-9" : "";
    }
}

ReviewedEventDetails.propTypes = {
    subjectId: PropTypes.string,
    reviewedEventId: PropTypes.string,
    visible: PropTypes.bool,
    reviewTime: PropTypes.string,
    reviewer: PropTypes.string,
    populationTime: PropTypes.string,
    referenceDate: PropTypes.string,
    populator: PropTypes.string,
    reviewedForms: PropTypes.arrayOf(PropTypes.object)
};