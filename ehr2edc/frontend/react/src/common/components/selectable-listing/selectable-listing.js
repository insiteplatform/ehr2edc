import React, {Fragment} from "react"
import * as PropTypes from "prop-types";

const ListItem = ({item, selectedId, renderItemLabel, onItemSelected}) => {
    const className = item.id === selectedId ? "is-active" : undefined;
    return <li key={item.id} className={className} onClick={() => onItemSelected(item.id)}>
        <a>{renderItemLabel(item)}</a>
    </li>
};

const List = ({items, selectedId, renderItemLabel, onItemSelected}) => {
    return <div className="grid-block vertical medium-4 large-3 border-right">
        <ul className="grid-content shrink vertical menu-bar no-bullet padding-none">
            {items.map(item =>
                <ListItem key={item.id} item={item} selectedId={selectedId} renderItemLabel={renderItemLabel}
                          onItemSelected={onItemSelected}/>)}
        </ul>
    </div>
};

export default class SelectableListing extends React.Component {
    render() {
        const {visible, items, selectedId, renderItemLabel, onItemSelected, children} = this.props;
        return <Fragment>
            {visible && <List items={items} selectedId={selectedId} renderItemLabel={renderItemLabel}
                              onItemSelected={onItemSelected}/>}
            {children}
        </Fragment>;
    }
}

SelectableListing.propTypes = {
    items: PropTypes.arrayOf(PropTypes.shape({
        id: PropTypes.string.isRequired
    })).isRequired,
    renderItemLabel: PropTypes.func.isRequired,
    selectedId: PropTypes.string,
    onItemSelected: PropTypes.func.isRequired,
    visible: PropTypes.bool.isRequired
};