import React from "react";
import MailTo from "../mail-to/mail-to";

const Error = ({error, contact}) => {
    return Array.isArray(error.issues) && error.issues.length > 0
        ? <Issues issues={error.issues} contact={contact}/>
        : <GenericError/>
};

const Issues = ({issues, contact}) => (
    <ul className="bullet">
        {issues.map((issue, index) => <Issue key={index} issue={issue} contact={contact}/>)}
    </ul>
);

const Issue = ({issue, contact}) => {
    return (issue.reference && <ErrorMessageWithReference message={issue.message} reference={issue.reference} contact={contact}/>) ||
        (issue.field && <ErrorMessageWithField message={issue.message} field={issue.field}/>) ||
        <ErrorMessage message={issue.message}/>;
};

const ErrorMessage = ({message}) => (
    <li>{message}</li>
);

const ErrorMessageWithField = ({message, field}) => (
    <li>{field}: {message}</li>
);

const ErrorMessageWithReference = ({message, reference, contact}) => (
    <li>
        {message} Please try again or <MailTo recipient={contact} subject={`Support request for issue ${reference}`}>contact
        support</MailTo> if the issue persists.
    </li>
);

const GenericError = () => (
    <p>An unspecified error occurred.</p>
);

const RequestError = ({error, contact}) => {
    const errorData = error && !!error.response ? error.response.data : undefined;
    return errorData !== undefined && <Error error={errorData} contact={contact}/>;
};

export function requestErrorHasField(error, field) {
    return !!error && !!error.response && !!error.response.data && !!error.response.data.issues
        && error.response.data.issues.some(issue => issue.field === field);
}

export default RequestError;