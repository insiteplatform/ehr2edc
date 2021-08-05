import React from "react"

export default class StudyOverviewPane extends React.Component {
    constructor(data) {
        super(data);
        this.state = {
            study: data.hasOwnProperty("study") ? data.study : {}
        };
    }

    render() {
        return (<div className="grid-content margin">
            <h3 className="subheader">{this.state.study.id}&nbsp;&mdash;&nbsp;{this.state.study.name}</h3>
            <p className="margin-tb">{this.state.study.description}</p>
        </div>);
    }
}