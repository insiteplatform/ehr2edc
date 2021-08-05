import React, {Fragment} from "react"
import {Area, AreaChart, ReferenceLine, Tooltip, XAxis, YAxis} from "recharts";
import moment from "moment"

export default class SubjectObservationSummaryGraph extends React.Component {
    constructor(props) {
        super(props);
        this.state = {};
        this.handleActiveDotClicked = this.handleActiveDotClicked.bind(this);
        this.handleAreaClicked = this.handleAreaClicked.bind(this);
    }

    render() {
        return (<AreaChart width={800} height={350} data={this.props.items}
                           margin={{top: 20, right: 60, left: 0, bottom: 0}} onClick={this.handleAreaClicked}>
                <XAxis dataKey="date" tickFormatter={this.formatAxisDate}/>
                <YAxis/>
                <Tooltip labelFormatter={this.formatTooltipDate} formatter={this.formatTooltip}/>
                <Area type='monotone' dataKey='amountOfObservations' stroke='#4978B9' fill='#6DA4DC'
                      activeDot={{onClick: this.handleActiveDotClicked}}/>
                {this.renderSelectedDateLine()}
            </AreaChart>
        )
    }

    renderSelectedDateLine() {
        if (!this.props.hasOwnProperty("dateSelected")) {
            return <Fragment/>
        }
        return <ReferenceLine x={this.props.dateSelected.format("YYYY-MM-DD")} stroke="#00bb9e"
                              label={{value: this.props.dateSelected.format("LL"), position: "top"}}/>;
    }

    formatTooltip(value, name, props) {
        return [value, "Amount of observations"];
    }

    formatAxisDate(tickItem) {
        return moment.utc(tickItem).format("MMM YYYY")
    }

    formatTooltipDate(t) {
        return moment.utc(t).format("LL");
    }

    handleActiveDotClicked(data) {
        this.handleDateSelected(moment.utc(data.payload.date));
    }

    handleAreaClicked(data) {
        this.handleDateSelected(moment.utc(data.activePayload[0].payload.date));
    }

    handleDateSelected(selectedDate) {
        this.props.onDateSelected(selectedDate);
    }
}

SubjectObservationSummaryGraph.defaultProps = {
    items: [],
    onDateSelected: () => {
    }
};