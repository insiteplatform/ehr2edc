import React from "react";
import Item from "./study-form-item";
import {errorFrom} from "./study-form";
import * as PropTypes from "prop-types";
import {ErrorTooltip} from "../../../../common/components/tooltip/tooltip";

export class ItemGroup extends React.Component {
    constructor(props) {
        super(props);
        this.handleGroupSelectionChanged = this.handleGroupSelectionChanged.bind(this);
    }

    render() {
        const {id} = this.props;
        return <div id={id} className={this.classes()}>
            {this.itemGroupHeading()}
            {this.renderItems()}
        </div>;
    }

    classes() {
        const classes = ["itemGroup", "margin-large-rl", "padding-small-top"];
        if (errorFrom(this.props) !== undefined) {
            classes.push("invalid")
        }
        return classes.join(" ");
    }

    itemGroupHeading() {
        const error = errorFrom(this.props);
        return <div className="border-bottom">
            <h5 className="subheader margin-none">
                <ErrorTooltip message={!!error ? error.message : undefined}>
                    {this.titleCheckbox()}
                    {this.titleLabel()}
                    {this.titleIcon()}
                </ErrorTooltip>
            </h5>
        </div>;
    }

    titleCheckbox() {
        const {groupKey, selected, inReview} = this.props;
        return inReview
            ? <input type="checkbox" id={"select-item-group-" + groupKey} checked={selected}
                     onChange={this.handleGroupSelectionChanged}/>
            : null;
    }

    titleLabel() {
        const {groupKey, name} = this.props;
        return <label htmlFor={"select-item-group-" + groupKey} style={{"fontSize": "1.25em"}}>{name}</label>;
    }

    titleIcon() {
        const error = errorFrom(this.props);
        return !!error ? <i className={"fa fa-exclamation-circle"}/> : null;
    }

    renderItems() {
        const {items} = this.props;
        return <div className="margin-bl">
            {items.map(item => this.renderItem(item))}
        </div>;
    }

    renderItem(item) {
        const {selectedItems, faultyItems, inReview, subjectId, eventId} = this.props;
        const itemIsSelected = selectedItems.indexOf(item.itemKey) !== -1;
        return <Item key={item.itemKey} {...item} selected={itemIsSelected} readOnly={!item.exportable}
                     faultyItems={faultyItems} inReview={inReview}
                     onItemSelected={this.props.onItemSelected} onItemDeselected={this.props.onItemDeselected}
                     subjectId={subjectId} eventId={eventId}/>;
    }

    handleGroupSelectionChanged() {
        const {groupKey, selected, inReview} = this.props;
        if (inReview) {
            if (selected) {
                this.props.onGroupDeselected(groupKey);
            } else {
                this.props.onGroupSelected(groupKey);
            }
        }
    }

}

ItemGroup.propTypes = {
    groupKey: PropTypes.string,
    id: PropTypes.string,
    subjectId: PropTypes.string,
    eventId: PropTypes.string,
    name: PropTypes.string,
    items: PropTypes.arrayOf(PropTypes.object),
    selected: PropTypes.bool,
    selectedItems: PropTypes.arrayOf(PropTypes.string),
    onItemSelected: PropTypes.func,
    onItemDeselected: PropTypes.func,
    onGroupSelected: PropTypes.func,
    onGroupDeselected: PropTypes.func
};