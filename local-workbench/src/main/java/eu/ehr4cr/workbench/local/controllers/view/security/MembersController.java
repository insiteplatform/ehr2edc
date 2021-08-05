package eu.ehr4cr.workbench.local.controllers.view.security;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import eu.ehr4cr.workbench.local.WebRoutes;
import eu.ehr4cr.workbench.local.WebViews;
import eu.ehr4cr.workbench.local.controllers.BaseController;
import eu.ehr4cr.workbench.local.controllers.NavigationItem;
import eu.ehr4cr.workbench.local.global.AuthorityType;
import eu.ehr4cr.workbench.local.model.security.UserRole;
import eu.ehr4cr.workbench.local.usecases.user.GetDetailedUsers;
import eu.ehr4cr.workbench.local.usecases.user.GetDetailedUsers.Response;
import eu.ehr4cr.workbench.local.usecases.user.GetDetailedUsers.Response.UserInfo;

@Controller
class MembersController extends BaseController {
	private final GetDetailedUsers getDetailedUsers;

	MembersController(GetDetailedUsers getDetailedUsers) {
		this.getDetailedUsers = getDetailedUsers;
		methodAuthorities.put(METHOD_GET, AuthorityType.VIEW_ACCOUNTS);
	}

	@GetMapping(value = WebRoutes.manageMembers)
	public String doGet(Model model) {
		indexSegmentSpring.process(model, context, this.getUser(), NavigationItem.members, WebViews.manageMembers);
		model.addAttribute("users", getUsers());
		model.addAttribute("roles", UserRole.values());
		return WebViews.index;
	}

	private List<UserInfo> getUsers() {
		Response response = getDetailedUsers.getUsers();
		return response.getUsers();
	}
}
