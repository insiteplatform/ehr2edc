package eu.ehr4cr.workbench.local.controllers.view;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import eu.ehr4cr.workbench.local.WebRoutes;
import eu.ehr4cr.workbench.local.controllers.BaseController;

@Controller
class HomeController extends BaseController {
	private final Environment environment;

	HomeController(Environment environment) {
		this.environment = environment;
	}

	@GetMapping("/")
	public String root() {
		return redirectToHomePage();
	}

	@GetMapping(WebRoutes.home)
	public String home() {
		return redirectToHomePage();
	}

	private String redirectToHomePage() {
		return "redirect:" + context.getContextPath() + WebRoutes.getHomeURL(environment);
	}
}
