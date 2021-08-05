package eu.ehr4cr.workbench.local.controllers.view.security;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.custodix.insite.local.user.GetUserForRecovery;
import com.custodix.insite.local.user.GetUserForRecovery.RecoveryAlreadyCompletedException;
import com.custodix.insite.local.user.GetUserForRecovery.RecoveryCompletionInvalidException;
import com.custodix.insite.local.user.GetUserForRecovery.Request;
import com.custodix.insite.local.user.GetUserForRecovery.Response;

import eu.ehr4cr.workbench.local.WebRoutes;
import eu.ehr4cr.workbench.local.WebViews;
import eu.ehr4cr.workbench.local.controllers.BaseController;
import eu.ehr4cr.workbench.local.controllers.LoginRouteProvider;

@Controller
class CompleteRecoveryViewController extends BaseController {
	private static final String ERROR_ALREADY_COMPLETED = "This user has already been recovered. Please log in or issue a new password recovery request.";
	private static final String ERROR_INVALID_URL = "Invalid recovery URL. Please use the link provided in the recovery mail.";

	private final GetUserForRecovery getUserForRecovery;
	private final LoginRouteProvider loginRouteProvider;

	public CompleteRecoveryViewController(GetUserForRecovery getUserForRecovery, LoginRouteProvider loginRouteProvider) {
		this.getUserForRecovery = getUserForRecovery;
		this.loginRouteProvider = loginRouteProvider;
	}

	@GetMapping(WebRoutes.completeRecovery)
	protected String doGet(@RequestParam("userId") long userId, @RequestParam("password") String tempPassword,
			Model model) {
		Request request = createRequest(userId, tempPassword);
		try {
			Response response = getUserForRecovery.getUser(request);
			populateModel(model, response);
		} catch (RecoveryAlreadyCompletedException e) {
			model.addAttribute("error", ERROR_ALREADY_COMPLETED);
		} catch (RecoveryCompletionInvalidException e) {
			model.addAttribute("error", ERROR_INVALID_URL);
		}
		return WebViews.completeRecovery;
	}

	private void populateModel(Model model, Response response) {
		model.addAttribute("userId", response.getUserId());
		model.addAttribute("userEmail", response.getUserEmail());
		model.addAttribute("expired", response.isUserExpired());
		model.addAttribute("loginRoute", loginRouteProvider.getRoute());
	}

	private Request createRequest(long userId, String tempPassword) {
		return Request.newBuilder()
				.withUserId(userId)
				.withTempPassword(tempPassword)
				.build();
	}
}
