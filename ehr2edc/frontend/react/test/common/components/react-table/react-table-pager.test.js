import React from 'react';
import renderer from 'react-test-renderer';
import ReactTablePager from "../../../../src/common/components/react-table/react-table-pager";

describe("Pager rendering", () => {
    test("Pager arguments get rendered well", () => {
        const pager = renderer.create(<ReactTablePager page={12} pageSize={10} pages={30} pageSizeOptions={[2, 5, 10]}/>);
        let tree = pager.toJSON();
        expect(tree).toMatchSnapshot();
    });
    test("A pager with a small amount of pages gets rendered well", () => {
        const pager = renderer.create(<ReactTablePager page={1} pageSize={10} pages={3} pageSizeOptions={[2, 5, 10]}/>);
        let tree = pager.toJSON();
        expect(tree).toMatchSnapshot();
    });
    test("First page selected gets rendered well", () => {
        const pager = renderer.create(<ReactTablePager page={0} pageSize={10} pages={30} pageSizeOptions={[2, 5, 10]}/>);
        let tree = pager.toJSON();
        expect(tree).toMatchSnapshot();
    });
    test("Last page selected gets rendered well", () => {
        const pager = renderer.create(<ReactTablePager page={29} pageSize={10} pages={30} pageSizeOptions={[2, 5, 10]}/>);
        let tree = pager.toJSON();
        expect(tree).toMatchSnapshot();
    });
    test("Pager change called gets rendered well", () => {
        let pageChangedEvent = jest.fn();
        let pager = renderer.create(<ReactTablePager page={30} pageSize={10} pages={30} pageSizeOptions={[2, 5, 10]} onPageChange={pageChangedEvent}/>);

        const props = {
            page: 5,
            pageSize: 20,
            pages: 10,
            pageSizeOptions: [5, 20, 50]
        };
        pager.getInstance().componentWillReceiveProps(props);

        expect(pageChangedEvent.mock.calls.length).toEqual(1);
    });
    test("Pager change called with the same amount of pages gets rendered well", () => {
        let pageChangedEvent = jest.fn();
        let pager = renderer.create(<ReactTablePager page={30} pageSize={10} pages={30} pageSizeOptions={[2, 5, 10]} onPageChange={pageChangedEvent}/>);

        const props = {
            page: 5,
            pageSize: 30,
            pages: 30,
            pageSizeOptions: [5, 20, 50]
        };
        pager.getInstance().componentWillReceiveProps(props);

        expect(pageChangedEvent.mock.calls.length).toEqual(1);
    });
});
describe("Page size navigation", () => {
    test("Changing the pager size triggers the event", () => {
        let pageSizeChangedEvent = jest.fn();
        let pager = mount(<ReactTablePager page={30} pageSize={10} pages={30} pageSizeOptions={[2, 5, 10]} onPageSizeChange={pageSizeChangedEvent}/>);

        pager.find('.page-size-5 a').simulate('click');

        expect(pageSizeChangedEvent.mock.calls.length).toEqual(1);
        pager.unmount();
    });
});
describe("Page navigation", () => {
    test("Going to the first page triggers the event", () => {
        let pageChangedEvent = jest.fn();
        let pager = mount(<ReactTablePager page={30} pageSize={10} pages={30} pageSizeOptions={[2, 5, 10]} onPageChange={pageChangedEvent}/>);

        pager.find('.pager-navigation-button.first a').simulate('click');

        expect(pageChangedEvent.mock.calls.length).toEqual(1);
        pager.unmount();
    });
    test("Going to the previous page triggers the event", () => {
        let pageChangedEvent = jest.fn();
        let pager = mount(<ReactTablePager page={30} pageSize={10} pages={30} pageSizeOptions={[2, 5, 10]} onPageChange={pageChangedEvent}/>);

        pager.find('.pager-navigation-button.prev a').simulate('click');

        expect(pageChangedEvent.mock.calls.length).toEqual(1);
        pager.unmount();
    });
    test("Going to the next page triggers the event", () => {
        let pageChangedEvent = jest.fn();
        let pager = mount(<ReactTablePager page={30} pageSize={10} pages={30} pageSizeOptions={[2, 5, 10]} onPageChange={pageChangedEvent}/>);

        pager.find('.pager-navigation-button.next a').simulate('click');

        expect(pageChangedEvent.mock.calls.length).toEqual(1);
        pager.unmount();
    });
    test("Going to the last page triggers the event", () => {
        let pageChangedEvent = jest.fn();
        let pager = mount(<ReactTablePager page={30} pageSize={10} pages={30} pageSizeOptions={[2, 5, 10]} onPageChange={pageChangedEvent}/>);

        pager.find('.pager-navigation-button.last a').simulate('click');

        expect(pageChangedEvent.mock.calls.length).toEqual(1);
        pager.unmount();
    });
    test("Going to a certain page triggers the event", () => {
        let pageChangedEvent = jest.fn();
        let pager = mount(<ReactTablePager page={30} pageSize={10} pages={30} pageSizeOptions={[2, 5, 10]} onPageChange={pageChangedEvent}/>);

        pager.find('.pager-navigation-page.page-28 a').simulate('click');

        expect(pageChangedEvent.mock.calls.length).toEqual(1);
        pager.unmount();
    });
    test("Clicking the same page as you're on does not trigger the event", () => {
        let pageChangedEvent = jest.fn();
        let pager = mount(<ReactTablePager page={28} pageSize={10} pages={30} pageSizeOptions={[2, 5, 10]} onPageChange={pageChangedEvent}/>);

        pager.find('.pager-navigation-page.page-29 a').simulate('click');

        expect(pageChangedEvent.mock.calls.length).toEqual(0);
        pager.unmount();
    });
});