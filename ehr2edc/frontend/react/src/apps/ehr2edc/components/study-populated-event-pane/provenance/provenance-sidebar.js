import React from "react"
import {Sidebar} from "../../../../../common/components/sidebar/sidebar"
import {Portal} from "react-overlays";
import {ItemProvenance} from "./item-provenance";
import {connect} from "react-redux";
import {
    closeItemDetails,
    fetchItemProvenance,
    fetchSubmittedItemProvenance
} from "../../../actions/item-provenance.actions";

const SidebarPortal = ({children}) => {
    const container = document.getElementById("study-details-content-pane");
    return <Portal container={container}>
        {children}
    </Portal>
};

export class ProvenanceSidebar extends React.Component {

    constructor(props) {
        super(props);
        this.state = {};

        this.onClose = this.onClose.bind(this);
    }

    componentWillUnmount() {
        if (this.hasData()) {
            this.onClose();
        }
    }

    componentDidUpdate(prevProps) {
        if (this.shouldFetchProvenance(prevProps)) {
            this.fetchProvenance();
        }
    }

    shouldFetchProvenance(previous) {
        const {eventId: prevEventId, subjectId: prevSubjectId, itemId: prevItemId} = previous;
        const {eventId, subjectId, itemId} = this.props;
        return !!eventId && eventId !== prevEventId
            || !!subjectId && subjectId !== prevSubjectId
            || !!itemId && itemId !== prevItemId;
    }

    fetchProvenance() {
        const {actions, studyId, eventId, subjectId, itemId, inReview} = this.props;
        if (!!inReview) {
            actions.fetchItemProvenance({studyId, eventId, subjectId, itemId});
        } else {
            actions.fetchSubmittedItemProvenance({studyId, eventId, subjectId, itemId})
        }
    }

    render() {
        const {name, value, provenance} = this.props;
        return this.hasData()
            ? <SidebarPortal>
                <Sidebar additionalClasses={["small-4", "border-left", "slideInLeft", "slideOutRight"]}>
                    <div className="sidebar-right">
                        {this.closeIcon()}
                        <div className="grid-block shrink vertical">
                            <ItemProvenance item={{name, value, provenance}}/>
                        </div>
                    </div>
                </Sidebar>
            </SidebarPortal> : null;
    }

    hasData() {
        const {name, value, provenance} = this.props;
        return !!name && name.length > 0
            && !!value && name.length > 0
            && !!provenance && typeof provenance === "object";
    }

    closeIcon() {
        return <a className="float-right close-icon" onClick={this.onClose}>
            <i className="fa fa-close"/>
        </a>;
    }

    onClose() {
        const {actions} = this.props;
        actions.closeItemDetails();
    }
}

const mapStateToProps = function (state, ownProps) {
    const {itemProvenance} = state;
    const {studyId} = ownProps;
    return {
        studyId,
        ...itemProvenance
    }
};

export const mapDispatchToProps = function (dispatch) {
    return {
        actions: {
            fetchItemProvenance: (payload) => dispatch(fetchItemProvenance(payload)),
            fetchSubmittedItemProvenance: (payload) => dispatch(fetchSubmittedItemProvenance(payload)),
            closeItemDetails: () => dispatch(closeItemDetails()),
        }
    }
};

export default connect(mapStateToProps, mapDispatchToProps)(ProvenanceSidebar);