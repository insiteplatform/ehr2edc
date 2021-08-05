import React from "react";

export default class MailTo extends React.Component {

    constructor(props) {
        super(props);
    }

    render() {
        const {children} = this.props;
        if(this.hasValidParameters()) {
            return this.renderMailTo();
        }
        return children;
    }

    hasValidParameters() {
        return this.hasValidRecipient() && this.hasValidSubject();
    }

    hasValidSubject() {
        const {subject} = this.props;
        if (typeof subject === 'string') {
            return  subject && subject.trim();
        }
        return subject
    }

    hasValidBody() {
        const {body} = this.props;
        if (typeof body === 'string') {
            return  body && body.trim();
        }
        return body
    }

    hasValidRecipient() {
        const {recipient} = this.props;
        return typeof recipient === 'string' && recipient && this.validEmail(recipient)
    }

    validEmail(email) {
        var re = /\S+@\S+\.\S+/;
        return re.test(email);
    }

    renderMailTo() {
        const {recipient, subject, children} = this.props;
        const mailto = `mailto:${recipient}?subject=${subject}${this.getBodyPart()}`;
        return<a href={mailto} target="_blank">{children}</a>
    }

    getBodyPart() {
        const {body} = this.props;
        if(this.hasValidBody()) {
            return `&amp;body=${body}`
        }
        return ''
    }
}