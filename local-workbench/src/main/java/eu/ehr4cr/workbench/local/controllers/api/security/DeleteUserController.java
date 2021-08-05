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
public class DeleteUserController extends BaseController {

	private final IUserMgrService userMgrService;

	@Autowired
	public DeleteUserController(IUserMgrService iUserMgrService) {
		this.userMgrService = iUserMgrService;
		methodAuthorities.put(METHOD_POST, AuthorityType.VIEW_ACCOUNTS);
	}

	/**
	 * deletes a user
	 *
	 * @param userId
	 *            database id of the user to be deleted
	 */

	@RequestMapping(value = WebRoutes.deleteMember,
					method = RequestMethod.POST)
	@ResponseBody
	@HasPermission(AuthorityType.VIEW_ACCOUNTS)
	public void doPost(@RequestParam long userId) {
		userMgrService.deleteUser(userId);
	}
}
