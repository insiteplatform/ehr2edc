package eu.ehr4cr.workbench.local.controllers.api.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import eu.ehr4cr.workbench.local.WebRoutes;
import eu.ehr4cr.workbench.local.global.AuthorityType;
import eu.ehr4cr.workbench.local.controllers.BaseController;
import eu.ehr4cr.workbench.local.security.annotation.HasPermission;
import eu.ehr4cr.workbench.local.service.IUserMgrService;

@Controller
public class AcceptUserController extends BaseController {

	private final IUserMgrService userService;

	@Autowired
	public AcceptUserController(IUserMgrService userService) {
		this.userService = userService;
		methodAuthorities.put(METHOD_POST, AuthorityType.VIEW_ACCOUNTS);
	}

	/**
	 * accepts a user
	 *
	 * @param userId
	 *            database id of the user that should be accepted
	 */
	@RequestMapping(value = WebRoutes.acceptMember,
					method = RequestMethod.POST)
	@ResponseBody
	@HasPermission(AuthorityType.VIEW_ACCOUNTS)
	public String doPost(@RequestParam long userId) {
		userService.acceptUser(userId);
		return "OK";
	}
}
