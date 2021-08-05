package eu.ehr4cr.workbench.local.controllers.view.security;

import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.custodix.insite.local.user.GetAccount;
import com.custodix.insite.local.user.GetAccount.Request;
import com.custodix.insite.local.user.GetAccount.Response;
import com.custodix.insite.local.user.GetAdministrators;

import eu.ehr4cr.workbench.local.WebRoutes;
import eu.ehr4cr.workbench.local.WebViews;
import eu.ehr4cr.workbench.local.controllers.BaseController;
import eu.ehr4cr.workbench.local.controllers.NavigationItem;
import eu.ehr4cr.workbench.local.global.AuthorityType;

/**
 * returns the user account details page and updates various user details
 */
@Controller
class AccountController extends BaseController {
	private final GetAccount getAccount;
	private final GetAdministrators getAdministrators;

	AccountController(GetAccount getAccount, GetAdministrators getAdministrators) {
		this.getAccount = getAccount;
		this.getAdministrators = getAdministrators;
	}

	@GetMapping(WebRoutes.myAccount)
	protected String doGet(Model model, Locale locale) {
		indexSegmentSpring.process(model, context, this.getUser(), NavigationItem.myAccount, WebViews.myAccount);
		model.addAttribute("account", getAccount(locale));
		boolean canManage = canManage();
		model.addAttribute("canManage", canManage);
		if (!canManage) {
			model.addAttribute("adminMails", getAdminMails());
		}
		return WebViews.index;
	}

	private AccountViewModel getAccount(Locale locale) {
		Request request = Request.newBuilder()
				.withUserIdentifier(getUser().getIdentifier())
				.withLocale(locale)
				.build();
		Response response = getAccount.getAccount(request);
		return createAccountViewModel(response);
	}

	private AccountViewModel createAccountViewModel(Response response) {
		return AccountViewModel.newBuilder()
				.withSecurityQuestions(response.getQuestions())
				.withUserName(response.getUserName())
				.withEmail(response.getUserEmail())
				.withPasswordExpiryDate(response.getPasswordExpiryDate()
						.orElse(null))
				.withTreatingPhysician(response.getTreatingPhysician()
						.orElse(null))
				.withSecurityQuestionId(response.getSecurityQuestionId())
				.withSecurityAnswer(response.getSecurityQuestionAnswer())
				.build();
	}

	private boolean canManage() {
		return getUser().hasAuthority(AuthorityType.MANAGE_ACCOUNTS);
	}

	private String[] getAdminMails() {
		List<String> adminMails = getAdministrators.getAdministrators()
				.getEmails();
		return adminMails.toArray(new String[0]);
	}

}
