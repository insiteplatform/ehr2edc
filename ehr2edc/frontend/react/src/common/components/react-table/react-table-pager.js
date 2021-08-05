import React from "react";

const defaultButton = props => <a {...props}>{props.children}</a>;

export default class ReactTablePager extends React.Component {
    constructor(props) {
        super(props);
        this.changePage = this.changePage.bind(this);
        this.changePageSize = this.changePageSize.bind(this);

        this.state = {
            visiblePages: this.getVisiblePages(props.page, props.pages)
        };
    }

    componentWillReceiveProps(nextProps) {
        if (this.props.pages !== nextProps.pages) {
            this.setState({
                visiblePages: this.getVisiblePages(nextProps.page, nextProps.pages)
            });
        }

        this.changePage(nextProps.page + 1);
    }

    filterPages(visiblePages, totalPages) {
        return visiblePages.filter(page => page <= totalPages);
    };

    getVisiblePages(page, total) {
        if (total < 7) {
            return this.filterPages([1, 2, 3, 4, 5, 6], total);
        } else {
            if (page % 5 >= 0 && page > 4 && page + 2 < total) {
                return [1, page - 1, page, page + 1, total];
            } else if (page % 5 >= 0 && page > 4 && page + 2 >= total) {
                return [1, total - 3, total - 2, total - 1, total];
            } else {
                return [1, 2, 3, 4, 5, total];
            }
        }
    };

    changePage(page) {
        const activePage = this.props.page + 1;

        if (page === activePage) {
            return;
        }

        const visiblePages = this.getVisiblePages(page, this.props.pages);

        this.setState({
            visiblePages: this.filterPages(visiblePages, this.props.pages)
        });

        this.props.onPageChange(page - 1);
    }

    changePageSize(pageSize) {
        this.setState({});
        this.props.onPageSizeChange(pageSize);
    }

    render() {
        const {PageButtonComponent = defaultButton} = this.props;
        const {visiblePages} = this.state;
        const activePage = this.props.page + 1;
        const pageSizes = this.props.pageSizeOptions;
        const currentPageSize = this.props.pageSize;

        const isFirstPage = activePage === 1;
        const isLastPage = activePage === this.props.pages;

        return (<zf-list-footer>
            <div className="pager-size">
                <span>Items per page: </span>
                <ul>
                    {pageSizes.map((pageSize) => (
                        <li key={pageSize}
                            className={pageSize === currentPageSize ? "page-size-" + pageSize + " is-active" : "page-size-" + pageSize}>
                            <a onClick={this.changePageSize.bind(null, pageSize)}>{pageSize}</a>
                        </li>
                    ))}
                </ul>
            </div>
            <div className="pager-middle"/>
            <ul className="pager">
                <li className={"pager-navigation-button first" + (isFirstPage ? " disabled" : "")}>
                    <PageButtonComponent onClick={() => this.changePage(0)} disabled={isFirstPage}>
                        <i className="fa fa-angle-double-left"/>
                    </PageButtonComponent>
                </li>
                <li className={"pager-navigation-button prev" + (isFirstPage ? " disabled" : "")}>
                    <PageButtonComponent onClick={() => this.changePage(activePage - 1)} disabled={isFirstPage}>
                        <i className="fa fa-angle-left"/>
                    </PageButtonComponent>
                </li>
                {visiblePages.map((page, index, array) => {
                    const pageButton = (<li key={`pagebutton-${page}`}
                                            className={activePage === page
                                                ? "pager-navigation-page is-active page-" + page
                                                : "pager-navigation-page page-" + page}>
                        <PageButtonComponent key={page} onClick={this.changePage.bind(null, page)}>
                            {page}
                        </PageButtonComponent>
                    </li>);
                    const pageNeedsDots = array[index - 1] + 2 < page;
                    if (pageNeedsDots) {
                        return (<React.Fragment key={index}>
                            <PageButtonComponent className="pager-navigation-page">...</PageButtonComponent>
                            {pageButton}
                        </React.Fragment>);
                    } else {
                        return pageButton;
                    }
                })}
                <li className={"pager-navigation-button next" + (isLastPage ? " disabled" : "")}>
                    <PageButtonComponent onClick={() => this.changePage(activePage + 1)} disabled={isLastPage}>
                        <i className="fa fa-angle-right"/>
                    </PageButtonComponent>
                </li>
                <li className={"pager-navigation-button last" + (isLastPage ? " disabled" : "")}>
                    <PageButtonComponent onClick={() => this.changePage(this.props.pages)} disabled={isLastPage}>
                        <i className="fa fa-angle-double-right"/>
                    </PageButtonComponent>
                </li>
            </ul>
        </zf-list-footer>);
    }
}