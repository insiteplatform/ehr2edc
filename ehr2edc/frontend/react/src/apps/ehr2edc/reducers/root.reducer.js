import {combineReducers} from "redux";
import ItemProvenanceReducer from './item-provenance.reducer';
import StudyReducer from './study.reducer';

const rootReducer = combineReducers({
    itemProvenance: ItemProvenanceReducer,
    study: StudyReducer,
});

export default rootReducer;