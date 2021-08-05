import './polyfills/polyfills'
import "@babel/polyfill";

import React from "react";
import ReactDOM from "react-dom";
import {Provider} from "react-redux"
import configureStore from "./apps/ehr2edc/store/configureStore";

import api from "./api/api";
import {patientRegistrationApiForRegisterSubjectComponent} from "./api/api-ext";

import EHR2EDC from "./apps/ehr2edc/ehr2edc";
import RegisterSubject from "./apps/register-subject/register-subject";

import '../../css'

window.appendEhr2EdcStudies = function (element, data) {
    const baseUrl = data.baseUrl ? data.baseUrl : "/";
    const studyApi = api.posts(data.studyListUrl);

    const store = configureStore();
    return ReactDOM.render(<Provider store={store}>
        <EHR2EDC
            baseUrl={baseUrl}
            studyApi={studyApi}
            emptyMessage={data.emptyMessage}
            errorMessage={data.errorMessage}
            studyPageUrl={data.studyPageUrl}
            populateEventActionText={data.populateEventActionText}
            contactAddress={data.contactAddress}/>
    </Provider>, element);
};

window.appendEhr2EdcRegisterSubject = function (element, data, patientId) {
    const baseUrl = data.baseUrl ? data.baseUrl : "/";
    const patientStudyApi = api.posts(data.patientStudyListUrl);
    const patientRegistrationApi = patientRegistrationApiForRegisterSubjectComponent(data.patientRegistrationUrl);
    return ReactDOM.render(<RegisterSubject
        baseUrl={baseUrl}
        patientStudyApi={patientStudyApi}
        patientRegistrationApi={patientRegistrationApi}
        patientSource={data.patientSource}
        patientId={patientId}
        onClose={data.closeFunction}/>, element);
};
