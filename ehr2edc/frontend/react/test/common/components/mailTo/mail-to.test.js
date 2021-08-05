import React from 'react';
import MailTo from "../../../../src/common/components/mail-to/mail-to";

describe("Rendering the component", () => {

    test("The component renders anchor and children correctly", () => {
        function rendersCorrectlyWith(subject, emailBody) {
            const children = <span>This is mail to link.</span>;

            const app = shallow(<MailTo recipient="someone@company.com"
                                        subject={subject}
                                        body={emailBody}>
                {children}
            </MailTo>);

            expect(app.find('a').exists()).toEqual(true);
            expect(app.find('a span').exists()).toEqual(true);
            expect(app.find('a').prop('target')).toEqual('_blank');
            expect(app.find('a').prop('href')).toEqual(`mailto:someone@company.com?subject=${subject}&amp;body=${emailBody}`);
            expect(app.text()).toEqual('This is mail to link.');
        }

        rendersCorrectlyWith("text", "text")
        rendersCorrectlyWith(123, 456)
    });

    test("The component renders anchor and children correctly when the body is empty", () => {
        function validateWith(emptyBody) {
            const children = <span>This is mail to link.</span>;

            const app = shallow(<MailTo recipient="someone@company.com"
                                        subject="this is my subject"
                                        body={emptyBody}>
                {children}
            </MailTo>);

            expect(app.find('a').exists()).toEqual(true);
            expect(app.find('a span').exists()).toEqual(true);
            expect(app.find('a').prop('target')).toEqual('_blank');
            expect(app.find('a').prop('href')).toEqual('mailto:someone@company.com?subject=this is my subject');
            expect(app.text()).toEqual('This is mail to link.');
        }

        validateWith("");
        validateWith("  ");
        validateWith(null);
        validateWith(undefined);
    });

    test("The component renders only children when the recipient is invalid", () => {
        function validateWith(recipient) {
            const children = <span>This is mail to link.</span>;
            const app = shallow(<MailTo recipient={recipient}
                                        subject="this is my subject"
                                        body="body">
                {children}
            </MailTo>);

            expect(app.find('a').exists()).toEqual(false);
            expect(app.find('span').exists()).toEqual(true);
            expect(app.text()).toEqual('This is mail to link.');
        }

        validateWith("");
        validateWith("  ");
        validateWith(null);
        validateWith(undefined);
        validateWith(123);
        validateWith("invalidEmail");
    });

    test("The component renders only children when the subject is invalid", () => {
        function validateWith(subject) {
            const children = <span>This is mail to link.</span>;
            const app = shallow(<MailTo recipient="someone@company.com"
                                        subject={subject}
                                        body="body">
                {children}
            </MailTo>);

            expect(app.find('a').exists()).toEqual(false);
            expect(app.find('span').exists()).toEqual(true);
            expect(app.text()).toEqual('This is mail to link.');
        }

        validateWith("");
        validateWith("  ");
        validateWith(null);
        validateWith(undefined);
    });
});