import React from "react";
import * as PropTypes from "prop-types";
import * as ReactDOM from "react-dom";

function CloseNotification({onClose}) {
    return <a onClick={onClose} className="close-button">&times;</a>;
}

function NotificationContent({message}) {
    return <div className="notification-content">
        <div className="padding-rl">
            <i className="fa fa-info-circle"/>
            {message}
        </div>
    </div>;
}

function NotificationPortal({portal, type, show, onClose, message}) {
    const active = show ? 'is-active' : '';
    return ReactDOM.createPortal(
        <div className={`static-notification top-right ${type} ${active}`}>
            <CloseNotification onClose={onClose}/>
            <NotificationContent message={message}/>
        </div>, portal);
}

export default class Notification extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            show: false
        };
        this.timer = null;
        this.el = document.createElement("div");
        this.el.setAttribute("id", this.props.id ? this.props.id : "notifications");

        this.hideNotification = this.hideNotification.bind(this);
    };

    componentDidMount() {
        document.body.appendChild(this.el);
    };

    componentWillUnmount() {
        document.body.removeChild(this.el);
        clearTimeout(this.timer);
    };

    render() {
        const {message, type = '', show = false} = this.state;
        return !!message &&
            <NotificationPortal portal={this.el}
                                message={message}
                                type={type}
                                show={show}
                                onClose={this.hideNotification}/>;
    }

    show(message, type = "") {
        this.scheduleHide();
        this.setState({
            show: true,
            message: message,
            type: type,
        });
    }

    scheduleHide() {
        const {timeout} = this.props;
        this.setState({
            timer: setTimeout(this.hideNotification, timeout)
        });
    }

    hideNotification() {
        this.setState({
            show: false,
            message: undefined,
            type: undefined,
        });
    }
}

Notification.propTypes = {
    timeout: PropTypes.number,
    id: PropTypes.string
};
Notification.defaultProps = {
    timeout: 10000
};