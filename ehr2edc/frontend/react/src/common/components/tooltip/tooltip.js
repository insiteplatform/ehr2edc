import React, {Fragment} from "react"
import * as PropTypes from "prop-types";

export default class Tooltip extends React.Component {
    constructor(props) {
        super(props);
    }

    render() {
        const {children, message, additionalClassNames, messageClassNames} = this.props;
        return !!children
            ? this.renderTooltip(message, additionalClassNames, children, messageClassNames)
            : null;
    }

    renderTooltip(message, additionalClassNames, children, messageClassNames) {
        return !!message ? <Fragment>
            <div className={this.buildClasses("tooltip", additionalClassNames)}>
                {children}
                <span className={this.buildClasses("tooltip-text", messageClassNames)}>
                    {message}
                </span>
            </div>
        </Fragment> : children;
    }

    buildClasses(base, additionalClassNames) {
        const classes = [base];
        if (Array.isArray(additionalClassNames)) {
            classes.push(...additionalClassNames)
        }
        return classes.join(" ");
    }
}

export function ErrorTooltip(props) {
    const { messageClassNames } = props;
    const classes=["alert-box", "alert", "warning"];
    if (Array.isArray(messageClassNames)) {
        classes.push(...messageClassNames)
    }
    return <Tooltip {...props} messageClassNames={classes}/>
}

Tooltip.propTypes = {
    message: PropTypes.node,
    additionalClassNames: PropTypes.arrayOf(PropTypes.string),
    messageClassNames: PropTypes.arrayOf(PropTypes.string)
};