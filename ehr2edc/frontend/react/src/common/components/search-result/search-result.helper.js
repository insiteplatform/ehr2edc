import React from "react"

export function SearchResultsLoading() {
    return (<div className="grid-block search-results-loading">
        <span className="search-results-loading-icon"><i className="fa fa-circle-o-notch fa-spin"/></span>
    </div>);
}

export function SearchResultsError({message, dataCy, children}) {
    return (<div className="search-results-none-icon error-center" data-cy={dataCy}>
        <i className="fa fa-times warning"/>
        <div className="align-center error-message">
            {!!children && children}
            {!children && <p className="text-center">{message}</p>}
        </div>
    </div>);
}
