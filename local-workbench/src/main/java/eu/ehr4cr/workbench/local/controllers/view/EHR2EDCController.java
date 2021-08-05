package eu.ehr4cr.workbench.local.controllers.view;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import eu.ehr4cr.workbench.local.WebRoutes;
import eu.ehr4cr.workbench.local.WebViews;
import eu.ehr4cr.workbench.local.controllers.BaseController;
import eu.ehr4cr.workbench.local.controllers.NavigationItem;
import eu.ehr4cr.workbench.local.properties.SupportSettings;

@Controller
class EHR2EDCController extends BaseController {
	private static final String CONTACT_ADDRESS = "contactAddress";
	private static final String BUNDLE_LOCATION = "bundleLocation";
	private static final String BASEURL = "baseUrl";

	private final SupportSettings supportSettings;
	private final String bundleLocation;

	public EHR2EDCController(SupportSettings supportSettings,
			@Value("${ehr2edc.ui.bundle.location:}") String bundleLocation) {
		this.supportSettings = supportSettings;
		this.bundleLocation = bundleLocation;
	}

	@GetMapping(value = WebRoutes.EHR2EDC_STUDIES)
	String getEHR2EDC(Model model) {
		indexSegmentSpring.process(model, context, this.getUser(), NavigationItem.EHR2EDC, "ehr2edc-studies.json");
		addAttributes(model);
		return WebViews.EHR2EDC_STUDIES;
	}

	private void addAttributes(Model model) {
		model.addAttribute(CONTACT_ADDRESS, supportSettings.getMailAddress());
		model.addAttribute(BUNDLE_LOCATION, bundleLocation);
		model.addAttribute(BASEURL, "/");
	}

	@GetMapping(value = WebRoutes.EHR2EDC_STUDIES + "/**")
	String forwardEHR2EDCReactRoutes(Model model) {
		return "forward:" + WebRoutes.EHR2EDC_STUDIES + "/";
	}
}