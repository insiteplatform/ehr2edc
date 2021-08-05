import React from "react"

export default class ExpandableText extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            expanded: props.expanded
        };
    }

    render() {
        if (this.props.children !== undefined) {
            return <div className={this.getClasses()}>
                {this.props.children}
                {this.actionAnchor()}
            </div>;
        }
        return null;
    }

    getClasses() {
        const classes = ["expandable-text"];
        if (this.state.expanded) {
            classes.push("expanded");
        }
        return classes.join(" ");
    }

    actionAnchor() {
        return <p>
            <a data-enzyme="action" onClick={() => this.toggle()}>
                {this.state.expanded ? "Show less" : "Show more"}
            </a>
        </p>;
    }

    toggle() {
        this.setState({
            expanded: !this.state.expanded
        });
    }
}

ExpandableText.defaultProps = {
    expanded: false
};