import React from 'react';
import renderer from 'react-test-renderer';
import ListStudies from "../../../src/apps/register-subject/list-studies";
import {ENTER_KEY, ESCAPE_KEY} from "../../../src/common/components/forms/form-constants";

const registeredStudies = [
    {
        studyId: 'SID_001',
        name: 'MCT',
        description: 'Melanoma Clinical Trial',
        subject: {
            edcSubjectReference: 'SUB_001',
            dateOfConsent: '2019-04-08 14:32:04'
        }
    }
];

const availableStudies = [
    {
        studyId: 'SID_002',
        name: 'VCT',
        description: 'Vitiligo Clinical Trial',
    }, {
        studyId: 'SID_003',
        name: 'VVCT',
        description: 'Vitiligo Vulgaris Clinical Trial',
    }
];
beforeEach(() => {
    Date.now = () => TEST_DATE.getTime();
});

const TEST_DATE = new Date(2019, 5, 27);
const NO_REGISTERED_STUDIES = "There's no registered studies for the patient";
const NO_AVAILABLE_STUDIES = "There's no available studies for the patient";
const DATE_OF_CONSENT = TEST_DATE;
const INITIAL_STATE = {
    selectedStudy: null,
    selectedStudyIndex: null,
    subjectId: "",
    dateOfConsent: TEST_DATE,
    expanded: {},
};

describe("Rendering the component", () => {
    'use strict';

    const onClose = () => {
    };

    afterEach(() => {
        jest.clearAllMocks();
    });

    test("The page is rendered", () => {
        const app = renderer.create(<ListStudies/>);

        const tree = app.toJSON();
        expect(tree).toMatchSnapshot();
    });

    test("The page is loading registered studies", () => {
        const app = renderer.create(
            <ListStudies
                studies={[]}
                isLoading={true}
                noDataMessage={NO_REGISTERED_STUDIES}
            />
        );

        const tree = app.toJSON();
        expect(tree).toMatchSnapshot();
    });

    test("The page is loading available studies", () => {
        const app = renderer.create(
            <ListStudies
                studies={[]}
                isLoading={true}
                noDataMessage={NO_AVAILABLE_STUDIES}
                editable={true}
                onClose={onClose}
                onSubjectRegistration={() => closed = true}
            />
        );

        const tree = app.toJSON();
        expect(tree).toMatchSnapshot();
    });

    test("The page is loaded without registered studies", () => {
        const app = renderer.create(
            <ListStudies
                studies={[]}
                isLoading={false}
                noDataMessage={NO_REGISTERED_STUDIES}
            />
        );

        const tree = app.toJSON();
        expect(tree).toMatchSnapshot();
    });

    test("The page is rendering registered studies", () => {
        const app = renderer.create(
            <ListStudies
                studies={registeredStudies}
                isLoading={false}
                noDataMessage={NO_REGISTERED_STUDIES}
            />
        );

        const tree = app.toJSON();
        expect(tree).toMatchSnapshot();
    });

    test("The page is rendering available studies", () => {
        let closed = false;
        const app = renderer.create(
            <ListStudies
                studies={availableStudies}
                isLoading={false}
                noDataMessage={NO_AVAILABLE_STUDIES}
                editable={true}
                onClose={onClose}
                onSubjectRegistration={() => closed = true}
            >
            </ListStudies>);

        const tree = app.toJSON();
        expect(tree).toMatchSnapshot();
    });

    test("Select an available study", () => {
        // Given: A List with selectable studies
        let closed = false;
        const edcSpy = jest.spyOn(ListStudies.prototype, "retrieveAvailableSubjectIds");
        const panel = mount(
            <ListStudies
                studies={availableStudies}
                isLoading={false}
                noDataMessage={NO_AVAILABLE_STUDIES}
                editable={true}
                onClose={onClose}
                onSubjectRegistration={() => closed = true}
            >
            </ListStudies>);

        // When: I select the first study
        panel.find('.rt-tbody .rt-tr').first().simulate('click');
        panel.update();

        // Then: The selected study matches the first available study
        const expectedStudy = availableStudies[0];
        expect(panel.state('selectedStudy').studyId).toEqual(expectedStudy.studyId);
        expect(panel.state('selectedStudy').name).toEqual(expectedStudy.name);
        expect(panel.state('selectedStudy').description).toEqual(expectedStudy.description);
        // And: The study's availableSubjectIds are retrieved
        expect(edcSpy).toHaveBeenCalledWith(expectedStudy);
        // And: The subject registration form is rendered
        expect(panel).toMatchSnapshot();
    });

    test("The Subject Registration form is rendered correctly", () => {
        // Given: A List-component with available studies
        let closed = false;
        const list = mount(<ListStudies
            studies={availableStudies}
            isLoading={false}
            noDataMessage={NO_AVAILABLE_STUDIES}
            editable={true}
            onClose={onClose}
            onSubjectRegistration={() => closed = true}/>);

        // When: The first study is selected
        list.setState({
            expanded: [true],
            selectedStudy: availableStudies[0],
            selectedStudyIndex: 0,
            subjectId: "SUBJECT-ID"
        });
        list.update();

        // Then: A SubjectIdField is rendered
        const subjectIdField = list.find("SubjectIdField");
        expect(subjectIdField.exists()).toBe(true);
        // And: The SubjectIdField mirrors the subjectId-value from the List-component
        expect(subjectIdField.prop("value")).toBe("SUBJECT-ID");
        // And: A DatePicker is rendered
        expect(list.exists("input#inputDateOfConsent")).toBe(true);
    });

    test("Register an available study", () => {
        const SUBJECT_ID = "SUBJ_042";
        let closed = false;
        const panel = mount(<ListStudies
            studies={availableStudies}
            isLoading={false}
            noDataMessage={NO_AVAILABLE_STUDIES}
            editable={true}
            onClose={onClose}
            onSubjectRegistration={() => closed = true}/>);
        const row = panel.find('.rt-tbody .rt-tr-group .rt-tr').first();
        row.simulate('click');
        panel.update();

        expect(panel.state('selectedStudy').studyId).toEqual("SID_002");
        expect(panel.state('selectedStudy').name).toEqual("VCT");
        expect(panel.state('selectedStudy').description).toEqual("Vitiligo Clinical Trial");

        panel.setState({
            subjectId: SUBJECT_ID,
            dateOfConsent: DATE_OF_CONSENT
        });
        panel.update();

        expect(panel.state('selectedStudy').studyId).toEqual("SID_002");
        expect(panel.state('selectedStudy').name).toEqual("VCT");
        expect(panel.state('selectedStudy').description).toEqual("Vitiligo Clinical Trial");
        expect(panel.state('subjectId')).toEqual(SUBJECT_ID);
        expect(panel.state('dateOfConsent')).toEqual(DATE_OF_CONSENT);

        const registerButton = panel.find('.rt-tbody .rt-tr-group a.button.icon');
        registerButton.simulate('click');
        expect(closed).toBe(true);
    });

    test("The form is disabled while fetching available subjectIds", () => {
        const app = mount(
            <ListStudies
                studies={availableStudies}
                isLoading={false}
                noDataMessage={NO_AVAILABLE_STUDIES}
                editable={true}
                onClose={onClose}
                onSubjectRegistration={() => closed = true}
            />
        );
        app.setState({
            selectedStudy: {
                studyId: 'SID_002',
                name: 'VCT',
                description: 'Vitiligo Clinical Trial',
            },
            expanded: {
                0: true,
                1: false
            }
        });

        app.setState({fetchingSubjectIds: true});
        expect(app.exists("SubjectIdField")).toBe(true);
        expect(app.find("SubjectIdField").prop("disabled")).toBe(true);
        expect(app.exists("input#inputDateOfConsent")).toBe(true);
        expect(app.find("input#inputDateOfConsent").prop("disabled")).toBe(true);
        expect(app.exists("a.button.disabled")).toBe(true);

        app.setState({fetchingSubjectIds: false});
        app.setState({subjectId: "SID", dateOfConsent: new Date()});
        expect(app.exists("SubjectIdField")).toBe(true);
        expect(app.find("SubjectIdField").prop("disabled")).toBe(false);
        expect(app.exists("input#inputDateOfConsent")).toBe(true);
        expect(app.find("input#inputDateOfConsent").prop("disabled")).toBe(false);
        expect(app.exists("a.button.disabled")).toBe(false);
    })
});

describe("Interaction with the form", () => {
    function mountApp() {
        return mount(<ListStudies
            studies={availableStudies}
            isLoading={false}
            noDataMessage={NO_AVAILABLE_STUDIES}
            editable={true}
            onSubjectRegistration={() => closed = true}>
        </ListStudies>);
    }

    function selectStudy(app) {
        app.find('.rt-tbody .rt-tr').first().simulate('click');
        app.update();
    }

    function formIn(app) {
        return app.find("form");
    }

    function pressKey(keyCode, app) {
        document.dispatchEvent(new KeyboardEvent('keydown', {keyCode: keyCode}));
        app.update();
    }

    test("The form is opened when a study is selected", () => {
        // Given: A ListStudies-component
        const app = mountApp();

        // When: selecting a study
        selectStudy(app);

        // Then: A form is present
        expect(formIn(app).exists()).toBe(true)
    });
    test("Closing the form with the 'Esc'-key", () => {
        // Given: An opened form
        const app = mountApp();
        selectStudy(app);
        expect(formIn(app).exists()).toBe(true);

        // When: Pressing the 'Esc'-key
        pressKey(ESCAPE_KEY, app);

        // Then: The form is closed
        expect(formIn(app).exists()).toBe(false);
    });
    test("Submitting the form with the 'Enter'-key", () => {
        // Given: A submit-function
        const spyOnSubmit = jest.spyOn(ListStudies.prototype, 'onRegisterPatientSubmit');
        // And: An opened form
        const app = mountApp();
        selectStudy(app);
        expect(formIn(app).exists()).toBe(true);

        // When: Pressing the 'Enter'-key
        pressKey(ENTER_KEY, app);

        // Then: The submit function is called
        expect(spyOnSubmit).toHaveBeenCalled();
    })
});

describe("Interaction with parent component", () => {
    test("Setting the props to loading, clears state", () => {
        const comp = mount(<ListStudies
            studies={availableStudies}
            isLoading={false}
            noDataMessage={NO_AVAILABLE_STUDIES}
            editable={true}
            onClose={() => {
            }}
            onSubjectRegistration={() => {
            }}
        />);

        comp.setState({
            selectedStudy: "aStudy",
            selectedStudyIndex: "idx",
            subjectId: "subject"
        });
        expect(comp.state()).not.toStrictEqual(INITIAL_STATE);

        comp.setProps({isLoading: true});

        expect(comp.state()).toStrictEqual(INITIAL_STATE);
    })
});