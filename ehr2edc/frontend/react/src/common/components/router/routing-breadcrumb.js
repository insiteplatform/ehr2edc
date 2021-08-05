import React from "react";
import {Link, Route, Switch} from "react-router-dom";

export function BreadcrumbLink(route) {
    return (
        <Switch>
            {currentBreadcrumbLink(route)}
            {ancestorBreadcrumbLink(route)}
        </Switch>);
}

function currentBreadcrumbLink(route) {
    return <Route path={route.path}
                  exact={true}
                  children={(props) => (
                      <li>
                          {replaceReactRouterParams(route.title, {
                              ...props.match.params,
                              ...route.breadcrumbParams
                          })}
                      </li>)}/>;
}

function ancestorBreadcrumbLink(route) {
    return <Route path={route.path}
                  exact={route.default}
                  children={(props) => {
                      return (<li>
                          <Link to={replaceReactRouterParams(route.path, props.match.params)} {...props}>
                              {replaceReactRouterParams(
                                  route.title,
                                  {
                                      ...props.match.params,
                                      ...route.breadcrumbParams
                                  }
                              )}
                          </Link>
                      </li>)
                  }}/>;
}

function replaceReactRouterParams(toReplace, params) {
    for (let param in params) {
        toReplace = toReplace.split(":" + param).join(params[param])
    }
    return toReplace;
}
