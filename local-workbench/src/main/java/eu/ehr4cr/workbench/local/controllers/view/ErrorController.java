package eu.ehr4cr.workbench.local.controllers.view;

import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import eu.ehr4cr.workbench.local.WebRoutes;
import eu.ehr4cr.workbench.local.WebViews;
import eu.ehr4cr.workbench.local.controllers.BaseController;

@Controller
public class ErrorController extends BaseController
		implements org.springframework.boot.web.servlet.error.ErrorController {

	@RequestMapping(value = WebRoutes.error,
					method = RequestMethod.GET)
	protected String error(@RequestParam Optional<String> trackingId, Model model) {
		trackingId.ifPresent(t -> model.addAttribute("trackingId", t));
		indexSegmentSpring.process(model, context, this.getUser(), null, WebViews.error);
		return WebViews.index;
	}

	@Override
	public String getErrorPath() {
		return WebRoutes.error;
	}

}
