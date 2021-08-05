import React from "react"

export class Sidebar extends React.Component {

    constructor(props) {
        super(props);
    }

    render() {
        const {collapsible, collapsed, children} = this.props;
        return (<div className={this.classes()}>
            {!collapsible || (collapsible && !collapsed)? children: null}
            {this.collapsibleToggle()}
        </div>);
    }

    classes() {
        const {hidden, additionalClasses, collapsible, collapsed} = this.props;
        const classes = ["sidebar", "grid-block", "vertical"];
        if (hidden) {
            classes.push("hide")
        }
        if (collapsible) {
            classes.push("collapsible")
        }
        if (collapsed) {
            classes.push("collapsed")
        }
        if (additionalClasses) {
            classes.push(...additionalClasses);
        }
        return classes.join(" ");
    }

    collapsibleToggle() {
        const {collapsible, collapsed, onCollapseToggle} = this.props;
        if (collapsible) {
            return <div className={"toggle border-top"} onClick={onCollapseToggle}>
                <i title={collapsed ? "Show" : "Hide"} className={`fa fa-angle-double-${collapsed ? 'right': 'left'}`} />
                {!collapsed ? <span>Hide</span> : null}
            </div>
        }
    }
}
