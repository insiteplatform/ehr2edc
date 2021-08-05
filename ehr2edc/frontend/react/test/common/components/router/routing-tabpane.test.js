import React from 'react';
import {StaticRouter} from "react-router-dom";
import {TabPaneLink} from "../../../../src/common/components/router/routing-tabpane";

describe('Rendering the component', () => {
    test('renders active panel link correctly', () => {

        const app = mount(<StaticRouter>
                    <TabPaneLink activate={true} title={'linkTitle'} path={'/myPath'}/>
            </StaticRouter>);

        expect(app.find('a').exists()).toBe(true);
        expect(app.find('a').text()).toBe('linkTitle');
        expect(app.find('a').prop('href')).toBe('/myPath');
        expect(app.find('a').find('input').props()).toMatchObject(
            {"className": "radioTab",
                "id": "radioTablinkTitle",
                "name": "radioTab",
                "type": "radio",
                "value": "true"});
        expect(app.find('a').find('label').prop('htmlFor')).toBe( 'radioTablinkTitle');
    });

    test('renders unactive panel link correctly', () => {

        const app = mount(<StaticRouter>
            <TabPaneLink activate={false} title={'linkTitle'} />
        </StaticRouter>);

        expect(app.find('a').exists()).toBe(false);
        expect(app.find('div').text()).toBe('linkTitle');
        expect(app.find('div').find('input').props()).toMatchObject(
            {"className": "radioTab disabled",
                "id": "radioTablinkTitle",
                "name": "radioTab",
                "type": "radio"});
        expect(app.find('div').find('label').prop('htmlFor')).toBe( 'radioTablinkTitle');
    });
});