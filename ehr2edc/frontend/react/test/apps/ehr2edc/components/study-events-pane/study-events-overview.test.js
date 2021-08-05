import React from "react";
import {StaticRouter} from "react-router-dom";
import StudyEventsOverview from "../../../../../src/apps/ehr2edc/components/study-events-pane/study-events-overview";
import readDataFromFile from "../../../../__helper__/sample-data-file";

'use strict';

describe("Component renders the 'fetch data point' button", () => {
    test("Component renders enable 'fetch data point' button correctly when ready for event population ", async () => {
        const events = getEventApiToLoadOneEvent();

        const app = mount(<StaticRouter>
            <StudyEventsOverview isLoading={false} error={null} events={events} isReadyForPopulation={true}/>
        </StaticRouter>);

        expect(app.find(".fetch-event-event-1").hasClass("disabled")).toBe(false);

    });


    test("Component renders disable 'fetch data point' button correctly when not ready for event population", async () => {
        const events = getEventApiToLoadOneEvent();

        const app = mount(<StaticRouter>
            <StudyEventsOverview isLoading={false} error={null} events={events} isReadyForEventPopulation={false}/>
        </StaticRouter>);

        expect(app.find(".fetch-event-event-1").hasClass("disabled")).toBe(true);
    });

    test("Component renders disable 'fetch data point' button correctly when the isReadyForEventPopulation is undefined ", async () => {
        const events = getEventApiToLoadOneEvent();

        const app = mount(<StaticRouter>
            <StudyEventsOverview isLoading={false} error={null} events={events} isReadyForEventPopulation={undefined}/>
        </StaticRouter>);

        expect(app.find(".fetch-event-event-1").hasClass("disabled")).toBe(true);
    });

    function getEventApiToLoadOneEvent() {
        const response = readDataFromFile("studyeventdefinitionscontroller/listEventsForStudy-knownEvents-response.json");
        return response.eventDefinitionsInStudy
    }
});
