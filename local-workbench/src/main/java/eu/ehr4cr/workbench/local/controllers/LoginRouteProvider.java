package eu.ehr4cr.workbench.local.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import eu.ehr4cr.workbench.local.WebRoutes;

@Component
public class LoginRouteProvider {
	private final String route;

	LoginRouteProvider(@Value("${oidc.enabled}") boolean oidcEnabled) {
		this.route = oidcEnabled ? WebRoutes.root : WebRoutes.login;
	}

	public String getRoute() {
		return route;
	}
}
