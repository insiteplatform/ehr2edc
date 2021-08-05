import Enzyme, {mount, render, shallow} from 'enzyme';
import Adapter from 'enzyme-adapter-react-16';
// React 16 Enzyme adapter
Enzyme.configure({adapter: new Adapter()});
// Make Enzyme functions available in all test files without importing
global.shallow = shallow;
global.render = render;
global.mount = mount;

/*
    Fail on warnings & errors during tests:
*/
if (!process.env.LISTENING_TO_UNHANDLED_REJECTION) {
    process.on('unhandledRejection', reason => {
        throw reason
    });
    // Avoid memory leak by adding too many listeners
    process.env.LISTENING_TO_UNHANDLED_REJECTION = true
}
global.console.warn = (message) => {
    throw message
};
global.console.error = (message) => {
    throw message
};
