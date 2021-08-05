import React from "react";
import * as PropTypes from "prop-types";
import {ItemGroup} from "./study-form-itemgroup";
import moment from "moment";
import {ErrorTooltip} from "../../../../common/components/tooltip/tooltip";

export function errorFrom({id, faultyItems}) {
    return !!faultyItems ? faultyItems.find(issue => issue.field === id) : undefined;
}

export default class Form extends React.Component {
    constructor(props) {
        super(props);
        this.handleFormSelectionChanged = this.handleFormSelectionChanged.bind(this);
    }

    render() {
        const {id} = this.props;
        return <div id={id} className={this.classes()}>
            {this.collapsibleToggle()}
            {this.formHeading()}
            {this.renderItemGroups()}
        </div>;
    }

    classes() {
        const classes = ["form"];
        if (errorFrom(this.props) !== undefined) {
            classes.push("invalid")
        }
        return classes.join(" ");
    }

    collapsibleToggle() {
        const {formKey} = this.props;
        return <>
            <input type="checkbox" id={"collapse-form-" + formKey} className="collapsed"/>
            <label htmlFor={"collapse-form-" + formKey} className="collapsible-toggle"/>
        </>;
    }

    formHeading() {
        const error = errorFrom(this.props);
        return <div className="border-bottom padding-rl study-form-header">
            <ErrorTooltip message={!!error ? error.message : undefined}>
                <h4 className="subheader">
                    {this.titleCheckbox()}
                    {this.titleLabel()}
                    {this.titleIcon()}
                </h4>
            </ErrorTooltip>
        </div>;
    }

    titleCheckbox() {
        const {formKey, selected, inReview} = this.props;
        return inReview
            ? <input type="checkbox" id={"select-form-" + formKey} className="margin-small" checked={selected}
                     onChange={this.handleFormSelectionChanged}/>
            : null;
    }

    titleLabel() {
        const {formKey, name} = this.props;
        return <label htmlFor={"select-form-" + formKey} style={{"fontSize": "inherit"}}>
            {name}
            {this.informationLabel()}
        </label>;
    }

    informationLabel() {
        const {inReview, populationTime, referenceDate} = this.props;
        return inReview
            ? <small style={{"fontSize": "80%"}}> populated on {moment(populationTime).format("ll [at] HH:mm")},
                referenced on {moment(referenceDate).format("ll")}</small>
            : null;
    }

    titleIcon() {
        const error = errorFrom(this.props);
        return !!error ? <i className={"fa fa-exclamation-circle"}/> : null;
    }

    renderItemGroups() {
        const {itemGroups} = this.props;
        return itemGroups.map(itemGroup => this.renderItemGroup(itemGroup));
    }

    renderItemGroup(itemGroup) {
        const {inReview, selectedItems, faultyItems, subjectId, eventId} = this.props;
        const exportableItems = itemGroup.items.filter(item => !!item.exportable);
        const groupSelected = exportableItems.length !== 0 && exportableItems.filter(item => selectedItems.indexOf(item.itemKey) === -1).length === 0;
        return <ItemGroup key={itemGroup.groupKey} {...itemGroup}
                          selectedItems={selectedItems}
                          selected={groupSelected}
                          faultyItems={faultyItems}
                          inReview={inReview}
                          onItemSelected={this.props.onItemSelected}
                          onItemDeselected={this.props.onItemDeselected}
                          onGroupSelected={this.props.onGroupSelected}
                          onGroupDeselected={this.props.onGroupDeselected}
                          subjectId={subjectId}
                          eventId={eventId}/>;
    }


    handleFormSelectionChanged() {
        const {formKey, selected, inReview} = this.props;
        if (inReview) {
            if (selected) {
                this.props.onFormDeselected(formKey);
            } else {
                this.props.onFormSelected(formKey);
            }
        }
    }
}

Form.propTypes = {
    formKey: PropTypes.string,
    id: PropTypes.string,
    subjectId: PropTypes.string,
    eventId: PropTypes.string,
    name: PropTypes.string,
    populationTime: PropTypes.string,
    referenceDate: PropTypes.string,
    itemGroups: PropTypes.arrayOf(PropTypes.object),
    selected: PropTypes.bool,
    selectedItems: PropTypes.arrayOf(PropTypes.string),
    onItemSelected: PropTypes.func,
    onItemDeselected: PropTypes.func,
    onGroupSelected: PropTypes.func,
    onGroupDeselected: PropTypes.func,
    onFormSelected: PropTypes.func,
    onFormDeselected: PropTypes.func
};
Form.defaultProps = {
    selectedItems: []
};
