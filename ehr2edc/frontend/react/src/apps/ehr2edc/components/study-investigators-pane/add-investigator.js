import React from "react";
import AutocompleteInput from "../../../../common/components/react-autocomplete/autocomplete-input";
import {ESCAPE_KEY} from "../../../../common/components/forms/form-constants";

export default class AddInvestigator extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            isOpen: !!props.isOpen,
            selectedUser: AddInvestigator.initSelectedUser()
        };

        this.autocompleteRef = React.createRef();

        this.openInvestigatorsForm = this.openInvestigatorsForm.bind(this);
        this.closeInvestigatorsForm = this.closeInvestigatorsForm.bind(this);
        this.updateSelectedUser = this.updateSelectedUser.bind(this);

        this.handleSubmit = this.handleSubmit.bind(this);
        this.handleKeyPress = this.handleKeyPress.bind(this);
    }

    componentDidMount() {
        document.addEventListener('keydown', this.handleKeyPress);
    }

    componentWillUnmount() {
        document.removeEventListener('keydown', this.handleKeyPress);
    }

    handleKeyPress(e) {
        if (e.keyCode === ESCAPE_KEY && this.shouldRenderForm()) {
            this.closeInvestigatorsForm();
        }
    }

    render() {
        const {potentialInvestigators, inProgress} = this.props;
        const hasNoPotentialInvestigators = !potentialInvestigators || potentialInvestigators.length === 0;
        if (hasNoPotentialInvestigators && !inProgress) {
            return this.renderEmptyState();
        } else if (this.shouldRenderForm()) {
            return this.renderOpenState();
        } else {
            return this.renderClosedState();
        }
    }

    shouldRenderForm() {
        const {inProgress} = this.props;
        return this.state.isOpen || inProgress;
    }

    renderEmptyState() {
        return <div className="grid-content collapse shrink"
                    title="All available users have been assigned as investigator">
            <a className="button open-add-investigator disabled">
                <i className="fa fa-plus"/> Add investigator
            </a>
        </div>;
    }

    renderClosedState() {
        return (<div className="grid-content collapse shrink">
            <a className="button open-add-investigator" onClick={() => this.openInvestigatorsForm()}>
                <i className="fa fa-plus"/> Add investigator
            </a>
        </div>);
    }

    renderOpenState() {
        return (<div className="grid-content collapse shrink">
            {this.renderAddInvestigatorsForm()}
        </div>);
    }

    renderAddInvestigatorsForm() {
        return <form onSubmit={this.handleSubmit}>
            <span className="inline-label">
                <AutocompleteInput
                    ref={this.autocompleteRef}
                    options={this.props.potentialInvestigators.map(pi => {
                        return {
                            value: pi.id,
                            label: pi.name
                        };
                    })}
                    placeholder="Add investigator ..."
                    onSelectionChange={this.updateSelectedUser}
                    openOnFocus={false}
                    inputProps={{
                        autoFocus: true
                    }}/>
                {this.renderAddButton()}
                <a className="button secondary close-add-investigator" onClick={() => this.closeInvestigatorsForm()}>
                    <i className="fa fa-times margin-small-right"/>
                    Cancel
                </a>
            </span>
        </form>;
    }

    renderAddButton() {
        const {selectedUser} = this.state;
        const {inProgress} = this.props;
        if (inProgress) {
            return (<a className="button add-investigator in-progress">
                <i className="fa fa-plus margin-small-right"/> Add
                <div className={"bar"}/>
            </a>);
        } else if (this.isSelectedUserNotPotentialInvestigators(selectedUser)) {
            return (<a className="button disabled add-investigator">
                <i className="fa fa-plus margin-small-right"/> Add
            </a>);
        } else {
            return (<a className="button add-investigator" onClick={this.handleSubmit}>
                <i className="fa fa-plus margin-small-right"/> Add
            </a>);
        }
    }

    isSelectedUserNotPotentialInvestigators(selectedUser) {
        const {potentialInvestigators} = this.props;
        return !potentialInvestigators.find((potentialInvestigator) => potentialInvestigator.id === selectedUser.id);
    }

    openInvestigatorsForm() {
        this.setState({isOpen: true})
    }

    closeInvestigatorsForm() {
        this.setState({
            isOpen: false,
            selectedUser: AddInvestigator.initSelectedUser(),
        })
    }

    handleSubmit(event) {
        event.preventDefault();
        if (this.canSubmit()) {
            this.doAddInvestigator();
        }
    }

    canSubmit() {
        const {selectedUser} = this.state;
        const {inProgress} = this.props;
        return !inProgress && selectedUser.id !== "" && selectedUser.name !== "";
    }

    doAddInvestigator() {
        this.props.onInvestigatorAdd(this.state.selectedUser);
        this.autocompleteRef.current.clear();
    }

    updateSelectedUser(item) {
        this.setState({
            selectedUser: {
                id: item.value,
                name: item.label,
            }
        });
    }

    static initSelectedUser() {
        return {
            id: "",
            name: ""
        };
    }
}