import React from "react";

export function ErrorMessage(props) {
    return (<div className="error search-results-none-icon error-center">
        <i className="fa fa-times warning"/>
        <div className="align-center error-message">
            <p className="text-center">{props.message}</p>
        </div>
    </div>);
}
