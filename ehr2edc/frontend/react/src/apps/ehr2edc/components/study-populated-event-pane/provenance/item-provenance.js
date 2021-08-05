import React from "react";
import RequestError from "../../../../../common/components/error/backend-request-error";

export class ItemProvenance extends React.Component {

    render() {
        const {item} = this.props;
        return !!item ? <>
            <h4 className="facts-title">Source Data Details</h4>
            {this.info(item)}
            {this.provenance(item)}
        </> : null;
    }

    info({name, value}) {
        return <dl className="margin-none">
            <h5>Name</h5>
            <dd>{name}</dd>
            <h5>Submitted Value</h5>
            <dd>{value}</dd>
        </dl>;
    }

    provenance({provenance}) {
        if (!!provenance) {
            const {error, data} = provenance;
            return <>
                <h5>Source Data</h5>
                {!error && !data ? this.provenanceLoading() : this.provenanceLoaded(provenance)}
            </>;
        }
    }

    provenanceLoading() {
        return <p>Loading...</p>
    }

    provenanceLoaded(provenance) {
        const {error, data} = provenance;
        return <>
            {this.provenanceError(error)}
            {this.provenanceDataListing(data)}
        </>;
    }

    provenanceError(error) {
        return !!error ?
            <div className="alert-box alert warning margin-trl">
                <RequestError error={error}/>
            </div> : null;
    }

    provenanceDataListing(data) {
        return !!data ? <div>
            {this.renderGroups(data.groups)}
            {this.renderItems(data.items)}
        </div> : null;
    }

    renderGroups(groups) {
        return groups.map((group, idx) => {
            const {label, items} = group;
            return <div key={idx} className="row">
                <h6>{label}</h6>
                {this.renderItems(items)}
            </div>
        })
    }

    renderItems(items) {
        return <dl className="responsive">
            {items.map((item, idx) => {
                const {label, value} = item;
                return <div key={idx} className="row">
                    <dt>{label}</dt>
                    <dd>{this.renderItemValue(value)}</dd>
                </div>;
            })}
        </dl>
    }

    renderItemValue(value) {
        return !!value ? value : <i className="disabled">none</i>
    }
}