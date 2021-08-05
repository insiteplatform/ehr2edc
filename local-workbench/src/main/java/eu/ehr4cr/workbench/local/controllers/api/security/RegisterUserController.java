package eu.ehr4cr.workbench.local.controllers.api.security;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import eu.ehr4cr.workbench.local.WebRoutes;
import eu.ehr4cr.workbench.local.auth.RegisterForm;
import eu.ehr4cr.workbench.local.controllers.BaseController;
import eu.ehr4cr.workbench.local.usecases.user.register.RegisterUser;
import eu.ehr4cr.workbench.local.usecases.user.register.RegisterUser.Request;

@RestController
class RegisterUserController extends BaseController {

	private final RegisterUser registerUser;

	RegisterUserController(RegisterUser registerUser) {
		this.registerUser = registerUser;
	}

	@PostMapping(WebRoutes.registerAccount)
	protected void doPost(@RequestBody RegisterForm registerForm) {
		Request request = Request.newBuilder()
				.withUserEmail(registerForm.getEmail())
				.withUserName(registerForm.getUsername())
				.build();
		registerUser.register(request);
	}
}