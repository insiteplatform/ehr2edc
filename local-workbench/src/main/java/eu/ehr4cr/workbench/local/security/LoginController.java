package eu.ehr4cr.workbench.local.security;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import eu.ehr4cr.workbench.local.WebRoutes;
import eu.ehr4cr.workbench.local.WebViews;

@Controller
@RequestMapping("/")
public class LoginController {
	@RequestMapping(value = WebRoutes.login,
					method = RequestMethod.GET)
	public String login(ModelMap modelMap) {
		return WebViews.login;

	}
}