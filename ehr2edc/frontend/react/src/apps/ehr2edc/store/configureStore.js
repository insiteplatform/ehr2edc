import {applyMiddleware, compose, createStore} from "redux";
import promise from 'redux-promise-middleware'
import rootReducer from "../reducers/root.reducer";

export default function configureStore(initialState) {
    const store = createStore(
        rootReducer,
        initialState,
        compose(
            applyMiddleware(promise),
            window.__REDUX_DEVTOOLS_EXTENSION__ ? window.__REDUX_DEVTOOLS_EXTENSION__() : f => f,
        )
    );

    if (module.hot) {
        // HMR for reducers
        module.hot.accept('../reducers/root.reducer', () => {
            const nextRootReducer = require('../reducers/root.reducer').default;
            store.replaceReducer(nextRootReducer);
        });
    }

    return store;
}