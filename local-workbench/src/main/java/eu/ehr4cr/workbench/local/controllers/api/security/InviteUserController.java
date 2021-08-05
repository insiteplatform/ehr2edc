package eu.ehr4cr.workbench.local.controllers.api.security;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.custodix.insite.local.user.InviteUser;
import com.custodix.insite.local.user.InviteUser.Request;

import eu.ehr4cr.workbench.local.WebRoutes;
import eu.ehr4cr.workbench.local.controllers.BaseController;
import eu.ehr4cr.workbench.local.controllers.api.security.dto.InviteUserRequest;

@RestController
class InviteUserController extends BaseController {
	private final InviteUser inviteUser;

	InviteUserController(InviteUser inviteUser) {
		this.inviteUser = inviteUser;
	}

	@PostMapping(WebRoutes.inviteUser)
	public void doPost(@RequestBody InviteUserRequest inviteUserRequest) {
		Request request = Request.newBuilder()
				.withEmail(inviteUserRequest.getMail())
				.withUsername(inviteUserRequest.getUsername())
				.withRoles(inviteUserRequest.getRoles())
				.build();
		inviteUser.invite(request);
	}
}
