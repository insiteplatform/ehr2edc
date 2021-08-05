import React from "react";
import {Link, Route} from "react-router-dom";

export function TabPaneLink(props) {
    return (<Route path={props.path}
                   exact={true}
                   children={tabPaneLinkWith(props)}/>);
}

function generateActivePanelLink(props) {
    return ({match}) => {
        const path = props.isDefault ? '/' : props.path;
        const className = !!match && match.isExact ? "radioTab active" : "radioTab";
        const linkId = `radioTab${props.title}`;
        return (
            <Link to={path}>
                <input type="radio" name="radioTab" value="true"
                       className={className} id={linkId}/>
                <label htmlFor={linkId}>{props.title}</label>
            </Link>)
    };
}

function generateUnactivePanelLink(props) {
    return ({match}) => {
        const linkId = `radioTab${props.title}`;
        return (
            <div>
            <input type="radio" name="radioTab" className="radioTab disabled" id={linkId}/>
                   <label htmlFor={linkId}>{props.title}</label>
            </div>)
    };
}

function tabPaneLinkWith(props) {
    const {activate} = props;
    return activate ?  generateActivePanelLink(props) : generateUnactivePanelLink(props);
}
