import React from 'react';
import RequestError, {requestErrorHasField} from "../../../../src/common/components/error/backend-request-error";
import renderer from "react-test-renderer";


const response_noIssues = {
    "issues": []
};
const response_basic = {
    "issues": [{
        "reference": null,
        "field": null,
        "message": "Something happened in the back-end"
    }]
};
const response_internalError = {
    "issues": [{
        "reference": "code",
        "field": null,
        "message": "error"
    }]
};
const response_constraintViolation = {
    "issues": [{
        "reference": null,
        "field": "field",
        "message": "user error"
    }]
};
const response_constraints = {
    "issues": [{
        "reference": null,
        "field": "field",
        "message": "Field validation failed."
    }, {
        "reference": null,
        "field": "field",
        "message": "And something else is wrong with this field too"
    }, {
        "reference": null,
        "field": "another-field",
        "message": "Another field validation failed."
    }]
};

describe("Building an error message from a error response", () => {

    test("Renders nothing without response data", () => {
        expect(shallow(<RequestError/>)).toEqual({});
        expect(shallow(<RequestError error={{}}/>)).toEqual({});
        expect(shallow(<RequestError error={{response: {}}}/>)).toEqual({});
    });

    test("Renders default message for faulty response", () => {
        hasDefaultHtml(shallow(<RequestError error={{response: {data: {}}}}/>));
        hasDefaultHtml(shallow(<RequestError error={{response: {data: ""}}}/>));
    });

    test("Renders default message for error without issues", () => {
        const error = {
            response: {data: response_noIssues}
        };
        hasDefaultHtml(shallow(<RequestError error={error}/>))
    });

    test("Render issue with errormessage", () => {
        const error = {
            response: {data: response_basic}
        };

        expect(renderer.create(<RequestError error={error}/>)).toMatchSnapshot();
    });

    test("Render issue with reference code", () => {
        const error = {
            response: {data: response_internalError}
        };

        let app = renderer.create(<RequestError error={error} contact="someone@company.com"/>);

        expect(app).toMatchSnapshot();
    });

    test("Render issue with fieldname", () => {
        const error = {
            response: {data: response_constraintViolation}
        };

        expect(renderer.create(<RequestError error={error}/>))
            .toMatchSnapshot()
    });

    test("Render multiple issues", () => {
        const error = {
            response: {data: response_constraints}
        };

        expect(renderer.create(<RequestError error={error}/>))
            .toMatchSnapshot()
    });

});

describe("requestErrorHasField-function", () => {

    test("Returns false for invalid input", () => {
        expect(requestErrorHasField(null, "someField")).toBe(false);
        expect(requestErrorHasField({}, "someField")).toBe(false);
        expect(requestErrorHasField({response: null}, "someField")).toBe(false);
        expect(requestErrorHasField({response: {data: null}}, "someField")).toBe(false);
        expect(requestErrorHasField({response: {data: {issues: null}}}, "someField")).toBe(false);
    });

    test("Returns false for missing field name", () => {
        expect(requestErrorHasField({response: {data: response_constraints}}, "not-present")).toBe(false);
    });

    test("Returns true for matching field", () => {
        expect(requestErrorHasField({response: {data: response_constraints}}, "field")).toBe(true);
        expect(requestErrorHasField({response: {data: response_constraints}}, "another-field")).toBe(true);
    });

});

function hasDefaultHtml(component) {
    expect(component.html()).toEqual("<p>An unspecified error occurred.</p>");
}
