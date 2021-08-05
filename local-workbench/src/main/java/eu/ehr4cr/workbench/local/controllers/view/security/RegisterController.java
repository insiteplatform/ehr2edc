package eu.ehr4cr.workbench.local.controllers.view.security;

import eu.ehr4cr.workbench.local.WebRoutes;
import eu.ehr4cr.workbench.local.WebViews;
import eu.ehr4cr.workbench.local.controllers.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
class RegisterController extends BaseController {

	@RequestMapping(value = WebRoutes.registerAccount, method = RequestMethod.GET)
	protected String doGet() {
		return WebViews.registerAccount;
	}

}