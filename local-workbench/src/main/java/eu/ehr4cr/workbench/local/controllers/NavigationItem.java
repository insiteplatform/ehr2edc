package eu.ehr4cr.workbench.local.controllers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.core.env.Environment;

import eu.ehr4cr.workbench.local.WebRoutes;
import eu.ehr4cr.workbench.local.global.AuthorityType;
import eu.ehr4cr.workbench.local.model.security.User;

public enum NavigationItem {
	EHR2EDC("ehr2edc", WebRoutes.EHR2EDC_STUDIES, null, false, ""),

	about("aboutInsite", WebRoutes.about, null, true, "fa-info-circle"),
	members("members", WebRoutes.manageMembers, AuthorityType.VIEW_ACCOUNTS, true, "fa-users"),
	myAccount("myAccount", WebRoutes.myAccount, null, true, "fa-user"),
	settings("settings", WebRoutes.trialDesignSettings, AuthorityType.MANAGE_PLACEMENT, true, "fa-cog"),
	logout("logOut", WebRoutes.logout, null, true, "fa-power-off");

	private final String name;
	private final String route;
	private final AuthorityType authorityType;
	private final boolean isUserMenu;
	private final String cssClass;

	NavigationItem(String name, String route, AuthorityType authorityType, boolean isUserMenu, String cssClass) {
		this.name = name;
		this.route = route;
		this.authorityType = authorityType;
		this.isUserMenu = isUserMenu;
		this.cssClass = cssClass;
	}

	public String getName() {
		return name;
	}

	public String getRoute() {
		return route;
	}

	public AuthorityType getAuthorityType() {
		return authorityType;
	}

	public boolean isUserMenu() {
		return isUserMenu;
	}

	public String getCssClass() {
		return cssClass;
	}

	public static List<NavigationItem> getNavigationItems(User user, boolean forUserMenu, Environment env) {
		Set<NavigationItem> hiddenItems = new HashSet<>();
		final ArrayList<NavigationItem> items = new ArrayList<>();
		for (NavigationItem item : NavigationItem.values()) {
			if (!hiddenItems.contains(item) && (item.authorityType == null || user.hasAuthority(item.authorityType))
					&& item.isUserMenu == forUserMenu) {
				items.add(item);
			}
		}
		return items;
	}
}
