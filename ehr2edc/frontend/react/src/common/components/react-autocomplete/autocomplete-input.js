import React from "react";
import Autocomplete from "react-autocomplete"
import * as PropTypes from "prop-types";

export default class AutocompleteInput extends React.Component {
    constructor(props) {
        super(props);
        this.state = AutocompleteInput.initState();

        this.handleKeyPress = this.handleKeyPress.bind(this);
    }

    componentDidMount() {
        document.addEventListener('keydown', this.handleKeyPress);
    }

    componentWillUnmount() {
        document.removeEventListener('keydown', this.handleKeyPress);
    }

    handleKeyPress(event) {
        this.setState({
            anyKeyPressed: true
        })
    }

    static initState() {
        return {
            selectedItem: AutocompleteInput.initSelectedItem(),
            filter: AutocompleteInput.initFilter(),
        };
    }

    static initSelectedItem() {
        return {
            value: "",
            label: ""
        };
    }

    static initFilter() {
        return "";
    }

    render() {
        const {options, placeholder, inputProps, menuStyle, openOnFocus} = this.props;
        const {filter, anyKeyPressed} = this.state;
        const actualOptions = openOnFocus || anyKeyPressed ? options : [];
        return (
            <div className="autocomplete-override">
                <Autocomplete items={actualOptions}
                              getItemValue={(item) => item.value}
                              renderInput={(inputProps) => <input type="text"
                                                                  className="autocomplete-input margin-none"
                                                                  placeholder={placeholder}
                                                                  {...inputProps}/>}
                              renderMenu={(items, value, style) => <div className="autocomplete-menu"
                                                                        style={{...style, ...this.menuStyle, ...menuStyle}}
                                                                        children={items}/>
                              }
                              inputProps={inputProps}
                              renderItem={this.renderItem()}
                              onSelect={(value) => this.onSelectItem(value)}
                              onChange={(e) => this.onChangeFilter(e.target.value)}
                              shouldItemRender={(item, input) => this.matches(item, input)}
                              value={filter}
                />
            </div>
        );
    }

    renderItem() {
        return (item, isHighlighted) => {
            const highlighted = isHighlighted ? ' highlighted' : '';
            const className = `autocomplete-menu-item${highlighted}`;
            return (
                <div key={item.value}
                     className={className}>
                    {item.label}
                </div>);
        };
    }

    onSelectItem(value) {
        const selectedItem = this.props.options.find((option) => option.value === value);
        this.setState({
            selectedItem: selectedItem,
            filter: selectedItem.label,
        });
        this.props.onSelectionChange(selectedItem);
    }

    onChangeFilter(input) {
        let inputTrimmed = input.trim();
        const selectedItem = this.resolveSelectedItem(inputTrimmed);
        this.setState({
            selectedItem: selectedItem,
            filter: input
        });
        this.props.onSelectionChange({value: inputTrimmed, label: inputTrimmed});
    }

    resolveSelectedItem(filter) {
        const {options} = this.props;
        if (options.length === 0) {
            return {id: filter, value: filter};
        }
        const selectedItem = options.find((option) => option.value === filter);
        return selectedItem ? selectedItem : AutocompleteInput.initSelectedItem();
    }

    matches(item, input) {
        const { alwaysRender } = this.props;
        return alwaysRender || !!item.label.match(new RegExp(input, 'i'));
    }

    clear() {
        this.setState(AutocompleteInput.initState());
    }
}

AutocompleteInput.propTypes = {
    onSelectionChange: PropTypes.func,
    alwaysRender: PropTypes.bool
};

AutocompleteInput.defaultProps = {
    options: [],
    openOnFocus: true,
    alwaysRender: false
};