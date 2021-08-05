import React from "react"
import ReactTable from "react-table";
import ReactTablePager from "../../../../common/components/react-table/react-table-pager";
import {Link} from "react-router-dom";

export default class StudiesOverviewTable extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            data: props.hasOwnProperty("data") ? props.data : [],
        };
    }

    render() {
        const {data} = this.state;
        return (<ReactTable data={data}
                            columns={
                                [
                                    {
                                        Header: "ID",
                                        accessor: "id",
                                        sortable: true,
                                        headerClassName: "small-2",
                                        className: "small-2",
                                        filterable: false,
                                        show: false
                                    },
                                    {
                                        Header: "Name",
                                        accessor: "name",
                                        sortable: true,
                                        headerClassName: "small-4",
                                        className: "small-4",
                                        filterable: false,
                                        Cell: function (colData) {
                                            return <div data-cy={"study-id-col"}>
                                                <Link to={`./${colData.original.id}`}>{colData.value}</Link>
                                            </div>
                                        },
                                    },
                                    {
                                        Header: "Description",
                                        accessor: "description",
                                        sortable: true,
                                        headerClassName: "small-8",
                                        className: "small-8",
                                        filterable: false
                                    }
                                ]
                            }
                            defaultPageSize={50} pageSizeOptions={[10, 25, 50]}
                            PaginationComponent={ReactTablePager}
                            resizable={false}
                            minRows={0}
                            defaultSorted={[
                                {
                                    id: "name",
                                    desc: false
                                }
                            ]}
        />);
    }
}