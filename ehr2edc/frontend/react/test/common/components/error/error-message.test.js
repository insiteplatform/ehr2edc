import React from "react";
import {StaticRouter} from "react-router-dom";
import {ErrorMessage} from "../../../../src/common/components/error/error-message";

describe('Rendering component', () => {
    test('renders error message correctly', () => {
        const app = mount(<StaticRouter>
            <ErrorMessage message={'Error Message'}/>
        </StaticRouter>);

        expect(app.find('div.error').exists()).toBe(true);
        expect(app.find('div.error').hasClass('error-center')).toBe(true);
        expect(app.find('div.error').hasClass('search-results-none-icon')).toBe(true);
        expect(app.find('div.error').find('i[className="fa fa-times warning"]').exists()).toBe(true);
        expect(app.find('div.error').find('div[className="align-center error-message"]').exists()).toBe(true);
        expect(app.find('div.error').find('div[className="align-center error-message"]').find('p[className="text-center"]').exists()).toBe(true);
        expect(app.find('div.error').find('div[className="align-center error-message"]').find('p[className="text-center"]').text()).toBe('Error Message');
    });
});
