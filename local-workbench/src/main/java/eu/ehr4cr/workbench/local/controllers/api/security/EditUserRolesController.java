package eu.ehr4cr.workbench.local.controllers.api.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.custodix.insite.local.user.EditUserRoles;
import com.custodix.insite.local.user.EditUserRoles.Request;

import eu.ehr4cr.workbench.local.WebRoutes;
import eu.ehr4cr.workbench.local.controllers.BaseController;
import eu.ehr4cr.workbench.local.controllers.api.security.dto.EditUserRolesRequest;

@RestController
class EditUserRolesController extends BaseController {
	private final EditUserRoles editUserRoles;

	@Autowired
	EditUserRolesController(EditUserRoles editUserRoles) {
		this.editUserRoles = editUserRoles;
	}

	@PostMapping(WebRoutes.assignMembers)
	public void doPost(@RequestBody EditUserRolesRequest editUserRolesRequest) {
		Request request = Request.newBuilder()
				.withUserId(editUserRolesRequest.getUserId())
				.withUserRoles(editUserRolesRequest.getRoles())
				.build();
		editUserRoles.editRoles(request);
	}
}
