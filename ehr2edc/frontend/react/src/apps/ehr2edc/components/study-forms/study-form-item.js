import React, {Fragment} from "react";
import {errorFrom} from "./study-form";
import * as PropTypes from "prop-types";
import {ErrorTooltip} from "../../../../common/components/tooltip/tooltip";
import {connect} from "react-redux";
import {openItemDetails, closeItemDetails} from "../../actions/item-provenance.actions";

export class Item extends React.Component {

    constructor(props) {
        super(props);
        this.handleItemSelectionChanged = this.handleItemSelectionChanged.bind(this);
    }

    render() {
        return <div {...this.wrapperProps()}>
            {this.renderCheckbox()}
            {this.renderItem()}
        </div>;
    }

    wrapperProps() {
        const {id} = this.props;
        return {
            id,
            title: this.title(),
            className: this.classes()
        }
    }

    title() {
        const {readOnly, inReview} = this.props;
        return readOnly && inReview
            ? "This data point was populated to facilitate review only and cannot be exported (it will not be sent to any EDC system linked to this study)"
            : undefined;
    }

    classes() {
        const {detailsOpened} = this.props;
        const classes = ["item padding-small-tb"];
        if (errorFrom(this.props) !== undefined) {
            classes.push("invalid")
        }
        if (detailsOpened) {
            classes.push("is-active")
        }
        return classes.join(" ");
    }

    renderCheckbox() {
        const {itemKey, selected, readOnly, inReview} = this.props;
        return inReview && !readOnly
            ? <input id={"select-form-item-" + itemKey} type="checkbox" checked={selected}
                     onChange={this.handleItemSelectionChanged}/>
            : null;
    }

    renderItem() {
        const {itemKey, readOnly, inReview, submittedToEdc} = this.props;
        return inReview && !readOnly
            ? <label className="inline-label" htmlFor={"select-form-item-" + itemKey}>
                {this.renderName()}
                {this.renderValue()}
            </label>
            : <div className="disabled padding-small-rl" style={{display: "flex"}}>
                {
                    submittedToEdc
                    ? <Fragment>
                            {this.renderName()}
                            {this.renderValue()}
                    </Fragment>
                    : <small style={{width: "100%", display: "flex"}}>
                            {this.renderName()}
                            {this.renderValue()}
                    </small>
                }
            </div>;
    }

    renderName() {
        const {name, unit} = this.props;
        const error = errorFrom(this.props);

        return <ErrorTooltip additionalClassNames={["small-8"]} message={!!error ? error.message : undefined}>
            <span className={!error ? "small-8" : null}>
                {name}
                {unit != null ? <small> ({unit})</small> : null}
                {error != null ? <i className={"fa fa-exclamation-circle"}/> : null}
            </span>
        </ErrorTooltip>
    }

    renderValue() {
        const {value, valueLabel} = this.props;
        return <span className={"small-4 text-right"}>
            {value}
            {valueLabel != null ? <small> ({valueLabel})</small> : null}
            {this.renderDetailsButton()}
        </span>;
    }

    handleItemSelectionChanged() {
        const {itemKey, selected, inReview} = this.props;
        if (inReview) {
            if (selected) {
                this.props.onItemDeselected(itemKey);
            } else {
                this.props.onItemSelected(itemKey);
            }
        }
    }

    renderDetailsButton() {
        return <a className="open-item-details margin-small-left" title="Open item details"
                  onClick={(event) => this.handleDetailsClicked(event)}>
            <i className="fa fa-file-text-o"/>
        </a>;
    }

    handleDetailsClicked(event) {
        event.preventDefault();
        const {actions, detailsOpened} = this.props;
        if (detailsOpened) {
            actions.closeItemDetails();
        } else {
            this.openDetails();
        }
    }

    openDetails() {
        const {actions, subjectId, eventId, id, inReview, name, value} = this.props;
        const payload = {
            subjectId: subjectId,
            eventId: eventId,
            itemId: id,
            inReview: inReview,
            name: name,
            value: value
        };
        actions.openItemDetails(payload);
    }
}

Item.propTypes = {
    itemKey: PropTypes.string,
    id: PropTypes.string,
    subjectId: PropTypes.string,
    eventId: PropTypes.string,
    name: PropTypes.string,
    value: PropTypes.string,
    valueLabel: PropTypes.string,
    unit: PropTypes.string,
    readOnly: PropTypes.bool,
    submittedToEdc: PropTypes.bool,
    selected: PropTypes.bool,
    onItemSelected: PropTypes.func,
    onItemDeselected: PropTypes.func,
    actions: PropTypes.object
};

export const mapStateToProps = (state, ownProps) => {
    const {itemProvenance} = state;
    const selectedItem = itemProvenance && itemProvenance.itemId;
    return {
        detailsOpened: selectedItem && itemProvenance.itemId === ownProps.id
    }
};

export const mapDispatchToProps = dispatch => {
    return {
        actions: {
            openItemDetails: (payload) => dispatch(openItemDetails(payload)),
            closeItemDetails: () => dispatch(closeItemDetails())
        }
    }
};

export default connect(mapStateToProps, mapDispatchToProps)(Item)