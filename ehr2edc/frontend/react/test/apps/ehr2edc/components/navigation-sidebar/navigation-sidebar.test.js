import React from 'react';
import NavigationSidebar from "../../../../../src/apps/ehr2edc/components/navigation-sidebar/navigation-sidebar";

describe('Rendering the component', () => {
    test('Subject TabPaneLink is activate correctly', () => {
        const canSubjectsBeViewed = false;
        const study = {
            name: 'studyName',
            permissions: {
                canSubjectsBeViewed: canSubjectsBeViewed
            }
        };

        const app = shallow(<NavigationSidebar study={study}/>);

        expect(app.find('TabPaneLink[title="Participants"]').exists()).toBe(true);
        expect(app.find('TabPaneLink[title="Participants"]').prop('activate')).toBe(canSubjectsBeViewed);
    });
});

describe('The onCollapseToggle handler toggles the sidebar collapse', () => {
    const props = {
        study: {
            name: 'studyName'
        }
    };

    test('A collapsed sidebar is expanded', () => {
        // Given: a collapsed sidebar
        const app = shallow(<NavigationSidebar {...props}/>);
        app.setState({collapsed: true});

        // When: the onCollapseToggle handler is called
        app.find("Sidebar").props().onCollapseToggle();

        // Then: the sidebar is expanded
        expect(app.find("Sidebar").prop("collapsed")).toBe(false)
    });

    test('An expanded sidebar is collapsed', () => {
        // Given: an expanded sidebar
        const app = shallow(<NavigationSidebar {...props}/>);

        // When: the onCollapseToggle handler is called
        app.find("Sidebar").props().onCollapseToggle();

        // Then: the sidebar is collapsed
        expect(app.find("Sidebar").prop("collapsed")).toBe(true)
    });
});
