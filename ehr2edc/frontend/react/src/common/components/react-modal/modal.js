import React from "react"
import ReactModal from "react-modal";
import RequestError from "../error/backend-request-error";

ReactModal.setAppElement('body');
ReactModal.defaultStyles = {};

export default class Modal extends React.Component {

    constructor(props) {
        super(props);

        this.handleConfirm = this.handleConfirm.bind(this);
        this.handleClose = this.handleClose.bind(this);
    }

    render() {
        const {title, open, children, inProgress} = this.props;
        return (
            <ReactModal title={title}
                        portalClassName="react-modal"
                        isOpen={open}
                        style={this.createStyle()}
                        onRequestClose={this.handleClose}
                        shouldCloseOnEsc={true}
                        shouldCloseOnOverlayClick={false}>
                <div className="modal-titlebar">
                    <div className="modal-title">{title}</div>
                    <div className="modal-close-icon" onClick={this.handleClose}>
                        <i className="fa fa-times"/>
                    </div>
                </div>
                <div className="modal-content">
                    {children}
                </div>
                <div className="modal-buttons">
                    <a className={`button modal-ok ${inProgress ? 'in-progress' : ''}`}
                       onClick={inProgress ? null : this.handleConfirm}
                       data-cy={"modal-ok-button"}
                    >
                        OK
                        <div className={"bar"}/>
                    </a>
                    <a className="button secondary modal-cancel" onClick={this.handleClose}>Cancel</a>
                </div>
            </ReactModal>);
    }

    createStyle() {
        const {width, height} = this.props;
        let style = {
            content: {
                width: width ? width : "450px",
                maxHeight: this.calculateMaxHeight()
            }
        };
        height && (style.content.height = height);
        return style;
    }

    calculateMaxHeight() {
        return window.innerHeight - 80 + "px";
    }

    handleConfirm() {
        if (this.props.onOkButtonClick) {
            this.props.onOkButtonClick();
        }
    }

    handleClose() {
        if (this.props.onClose) {
            this.props.onClose();
        }
    }
}

export class ModalErrors extends React.Component {

    constructor(props) {
        super(props);
        this.message = "The form returned some errors";
        this.myRef = React.createRef();
    }

    componentDidMount() {
        this.scrollToErrorMessage();
    }

    render() {
        const validationErrors = this.parsedValidationErrors();
        return <div className="alert-box alert margin-top" ref={this.myRef}>
            {this.message}
            <ul className="bullet">{validationErrors}</ul>
            <RequestError error={this.props.requestError} contact={this.props.contact}/>
        </div>;
    }

    parsedValidationErrors() {
        return this.props.validationErrors
            ? this.props.validationErrors.map((message, key) => <li key={key}>{message}</li>)
            : null;
    }

    scrollToErrorMessage() {
        this.myRef.current.scrollIntoView();
    }
}