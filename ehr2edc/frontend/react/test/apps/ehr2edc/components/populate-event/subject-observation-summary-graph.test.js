import React from 'react';
import renderer from 'react-test-renderer';
import SubjectObservationSummaryGraph
    from "../../../../../src/apps/ehr2edc/components/populate-event/subject-observation-summary-graph";
import moment from "moment";
import readDataFromFile from "../../../../__helper__/sample-data-file";

'use strict';

const observationSummary = readDataFromFile("subjectobservationsummarycontroller/getSubjectObservationSummary.json");

describe("Rendering the component", () => {
    test("Component renders correctly", () => {
        const app = renderer.create(<SubjectObservationSummaryGraph items={observationSummary.summaryItems}/>);
        expect(app).toMatchSnapshot();

        const appDateSelected = renderer.create(<SubjectObservationSummaryGraph items={observationSummary.summaryItems}
                                                                    dateSelected={moment("2018-10-17")}/>);
        expect(appDateSelected).toMatchSnapshot();
    });
});

describe("Selecting a date", () => {
    const selectedDatePayloadObject = {payload: {date: "2018-10-17"}};

    test("Via clicking the tooltip", () => {
        const onDateSelected = jest.fn();
        const app = shallow(<SubjectObservationSummaryGraph items={observationSummary.summaryItems}
                                                            onDateSelected={onDateSelected}/>);

        app.instance().handleActiveDotClicked(selectedDatePayloadObject);

        expect(onDateSelected).toHaveBeenCalledTimes(1);
        expect(onDateSelected).toHaveBeenCalledWith(moment.utc("2018-10-17"));
    });
    test("Via clicking the area chart", () => {
        const onDateSelected = jest.fn();
        const app = shallow(<SubjectObservationSummaryGraph items={observationSummary.summaryItems}
                                                            onDateSelected={onDateSelected}/>);

        app.instance().handleAreaClicked({activePayload: [selectedDatePayloadObject]});

        expect(onDateSelected).toHaveBeenCalledTimes(1);
        expect(onDateSelected).toHaveBeenCalledWith(moment.utc("2018-10-17"));
    });
});