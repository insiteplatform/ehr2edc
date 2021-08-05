import React, {Fragment} from "react"
import ReactTable from "react-table";
import ReactTablePager from "../../../../common/components/react-table/react-table-pager";
import AddInvestigator from "./add-investigator";
import ExpandableText from "../../../../common/components/expandable-text/expandable-text";

export default class StudyInvestigatorsPane extends React.Component {
    constructor(data) {
        super(data);
        this.state = {
            investigators: StudyInvestigatorsPane.resolveInvestigators(data, []),
            onInvestigatorAdd: data.onInvestigatorAdd,
            onInvestigatorUnassign: data.onInvestigatorUnassign
        }
    }

    componentWillReceiveProps(nextProps) {
        this.setState({
            investigators: StudyInvestigatorsPane.resolveInvestigators(nextProps, this.props.investigators),
        });
    }

    static resolveInvestigators(props, defaultInvestigators) {
        return props.hasOwnProperty("investigators") && props.investigators ? props.investigators : defaultInvestigators;
    }

    render() {
        return (<Fragment>
            <div className="grid-block padding margin-small-rl shrink">
                <div className="grid-content collapse">
                    <h3 className="subheader">Investigators</h3>
                </div>
                <AddInvestigator potentialInvestigators={this.props.potentialInvestigators}
                                 onInvestigatorAdd={this.props.onInvestigatorAdd}
                                 inProgress={this.props.addInProgress}
                />
            </div>
            <div className="margin-rl padding-bottom">
                <div className="alert-box small info margin-small-rl padding">
                    <ExpandableText>
                        <p>Here you can see all investigators assigned to this study. Only assigned investigators can
                            register subjects to a study and perform subject data population. If you are a data
                            relationship
                            manager, you can add new investigators to this study by their username or unassign
                            individual
                            investigators.</p>
                    </ExpandableText>
                </div>
            </div>
            {this.renderInvestigators()}
        </Fragment>)
    }

    renderInvestigators() {
        if (this.state.investigators.length === 0) {
            return this.renderNoInvestigators();
        } else {
            return this.renderInvestigatorsTable();
        }
    }

    renderNoInvestigators() {
        return <p className="margin-medium-rl">No investigators assigned</p>;
    }

    renderInvestigatorsTable() {
        return (<ReactTable data={this.state.investigators}
                            columns={this.buildColumns()}
                            defaultPageSize={10} pageSizeOptions={[10, 50, 100]}
                            PaginationComponent={ReactTablePager}
                            resizable={false}
                            minRows={0}/>)
    }

    buildColumns() {
        return [{
            Header: "Investigator",
            accessor: "name",
            sortable: false,
            filterable: false,
            show: true
        }, {
            Header: "Actions",
            accessor: "id",
            sortable: false,
            filterable: false,
            show: this.state.investigators.some(investigator => investigator.removable),
            headerClassName: 'button-cell text-right',
            className: 'button-cell text-right',
            style: {
                "flex": "none"
            },
            Cell: row => (this.renderUnassignInvestigatorButton(row))
        }];
    }

    renderUnassignInvestigatorButton(row) {
        if (this.props.removeInProgress) {
            return <a className="unassign-icon"
                      title={'Unassign request in progress'}>
                <i className="fa fa-circle-o-notch fa-spin"></i>
            </a>

        }
        return <a className="unassign-icon"
                  onClick={() => this.state.onInvestigatorUnassign(row.original)}
                  title={'Unassign ' + row.original.id}>
            <i className="fa fa-times"></i>
        </a>
    }
}