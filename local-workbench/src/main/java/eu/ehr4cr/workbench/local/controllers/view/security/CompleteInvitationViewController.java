package eu.ehr4cr.workbench.local.controllers.view.security;

import java.util.Locale;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.custodix.insite.local.user.GetUserForActivation;
import com.custodix.insite.local.user.GetUserForActivation.InvitationAlreadyCompletedException;
import com.custodix.insite.local.user.GetUserForActivation.InvitationCompletionInvalidException;
import com.custodix.insite.local.user.GetUserForActivation.Request;
import com.custodix.insite.local.user.GetUserForActivation.Response;

import eu.ehr4cr.workbench.local.WebRoutes;
import eu.ehr4cr.workbench.local.WebViews;
import eu.ehr4cr.workbench.local.controllers.BaseController;
import eu.ehr4cr.workbench.local.controllers.LoginRouteProvider;

@Controller
class CompleteInvitationViewController extends BaseController {
	private static final String ERROR_ALREADY_COMPLETED = "This user has already accepted. Please log in.";
	private static final String ERROR_INVALID_URL = "Invalid acceptation URL. Please use the link provided in the invitation mail.";

	private final GetUserForActivation getUserForActivation;
	private final LoginRouteProvider loginRouteProvider;

	CompleteInvitationViewController(GetUserForActivation getUserForActivation, LoginRouteProvider loginRouteProvider) {
		this.getUserForActivation = getUserForActivation;
		this.loginRouteProvider = loginRouteProvider;
	}

	@GetMapping(WebRoutes.completeInvitation)
	protected String doGet(@RequestParam("userId") long userId, @RequestParam("password") String tempPassword,
			Model model, Locale locale) {
		Request request = createRequest(userId, tempPassword, locale);
		try {
			Response response = getUserForActivation.getUser(request);
			populateModel(model, response);
		} catch (InvitationAlreadyCompletedException e) {
			model.addAttribute("error", ERROR_ALREADY_COMPLETED);
		} catch (InvitationCompletionInvalidException e) {
			model.addAttribute("error", ERROR_INVALID_URL);
		}
		return WebViews.completeInvitation;
	}

	private void populateModel(Model model, Response response) {
		model.addAttribute("userId", response.getUserId());
		model.addAttribute("userEmail", response.getUserEmail());
		model.addAttribute("expired", response.isUserExpired());
		model.addAttribute("questions", response.getSecurityQuestions());
		model.addAttribute("loginRoute", loginRouteProvider.getRoute());
	}

	private Request createRequest(long userId, String tempPassword, Locale locale) {
		return Request.newBuilder()
				.withUserId(userId)
				.withTempPassword(tempPassword)
				.withLocale(locale)
				.build();
	}
}
