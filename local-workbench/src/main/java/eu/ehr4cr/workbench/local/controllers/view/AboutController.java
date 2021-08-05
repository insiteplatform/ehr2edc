package eu.ehr4cr.workbench.local.controllers.view;

import eu.ehr4cr.workbench.local.controllers.BaseController;
import eu.ehr4cr.workbench.local.controllers.NavigationItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import eu.ehr4cr.workbench.local.WebRoutes;
import eu.ehr4cr.workbench.local.WebViews;
import eu.ehr4cr.workbench.local.service.impl.AboutService;

@Controller
public class AboutController extends BaseController {

	@Autowired
	private AboutService aboutService;

	/**
	 * returns the about page response: 'lwbVersion': current version of the
	 * local workbench application (taken from pom.xml)
	 */
	@RequestMapping(value = WebRoutes.about, method = RequestMethod.GET)
	protected String doGet(Model model) {
		model.addAttribute("lwbVersion", aboutService.getLwbVersion());
		indexSegmentSpring.process(model, context, this.getUser(), NavigationItem.about, WebViews.about);
		return WebViews.index;
	}
}
