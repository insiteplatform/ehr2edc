import React from "react";
import {Sidebar} from "../../../../common/components/sidebar/sidebar";
import {ehr2edcRoutes as routes} from "../../ehr2edc.routes";
import {TabPaneLink} from "../../../../common/components/router/routing-tabpane";

export default class NavigationSidebar extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            collapsed: false
        };
        this.toggleCollapse = this.toggleCollapse.bind(this);
    }

    render() {
        const {hideSidebar, study} = this.props;
        const {collapsed} = this.state;
        return (
            <Sidebar additionalClasses={["shrink", "sidebar-nav", "border-right"]}
                     collapsible={true}
                     hidden={hideSidebar}
                     collapsed={collapsed}
                     onCollapseToggle={this.toggleCollapse}>
                <div className="grid-content padding shrink border-bottom">
                    <h4>{study.name}</h4>
                </div>
                <div className="grid-block vertical">
                    <ul className="side-bar-navigation">{this.renderTabs()}</ul>
                </div>
            </Sidebar>);
    }

    toggleCollapse() {
        const {collapsed} = this.state;
        this.setState({collapsed: !collapsed});
    }

    renderTabs() {
        const {studyId} = this.props;
        return routes
            .filter(route => route.isTab)
            .sort((a, b) => a.navigation.order - b.navigation.order)
            .map((route, index) => (
                <li key={index}>
                    <TabPaneLink path={route.path.replace(':studyId', studyId)}
                                 title={route.navigation.title.startsWith(":") ? "Overview" : route.navigation.title}
                                 isDefault={route.default}
                                 activate={route.isActivate(this.props)}
                    />
                </li>));
    }
}