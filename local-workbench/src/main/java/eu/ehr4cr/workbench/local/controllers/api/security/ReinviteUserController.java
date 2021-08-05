package eu.ehr4cr.workbench.local.controllers.api.security;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.custodix.insite.local.user.ReinviteUser;
import com.custodix.insite.local.user.ReinviteUser.Request;

import eu.ehr4cr.workbench.local.WebRoutes;
import eu.ehr4cr.workbench.local.controllers.BaseController;

@RestController
class ReinviteUserController extends BaseController {
	private final ReinviteUser reinviteUser;

	ReinviteUserController(ReinviteUser reinviteUser) {
		this.reinviteUser = reinviteUser;
	}

	@PostMapping(WebRoutes.reinviteUser)
	public void reinvite(@RequestParam("userId") long userId) {
		Request request = Request.newBuilder()
				.withUserId(userId)
				.build();
		reinviteUser.reinvite(request);
	}
}
